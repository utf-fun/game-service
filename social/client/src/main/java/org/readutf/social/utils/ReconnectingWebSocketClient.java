package org.readutf.social.utils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A WebSocket client that automatically attempts to reconnect when the connection is lost.
 * Uses exponential backoff for reconnection attempts.
 */
public class ReconnectingWebSocketClient {
    @NotNull
    private final URI serverUri;
    @Nullable
    private final Map<String, String> httpHeaders;
    @Nullable
    private WebSocketClient client;
    @NotNull
    private final ScheduledExecutorService reconnectService;
    @Nullable
    private ScheduledFuture<?> reconnectTask;

    @NotNull
    private final AtomicBoolean reconnectOnClose = new AtomicBoolean(true);
    @NotNull
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);

    // Configurable parameters
    private final int maxReconnectAttempts;
    private final long initialReconnectDelay; // in milliseconds
    private final long maxReconnectDelay; // in milliseconds
    private final float reconnectBackoffMultiplier;

    // Event listeners
    @Nullable
    private final EventListener onOpen;
    @Nullable
    private final EventListener onClose;
    @Nullable
    private final MessageListener onMessage;
    @Nullable
    private final ErrorListener onError;
    @Nullable
    private final ReconnectListener onReconnect;
    @Nullable
    private final ReconnectFailedListener onMaxReconnectAttemptsExceeded;

    // Logger for logging connection, reconnection and errors
    private static final Logger LOGGER = Logger.getLogger(ReconnectingWebSocketClient.class.getName());

    /**
     * Private constructor used by the Builder.
     */
    private ReconnectingWebSocketClient(@NotNull Builder builder) {
        this.serverUri = builder.serverUri;
        this.httpHeaders = builder.httpHeaders;
        this.maxReconnectAttempts = builder.maxReconnectAttempts;
        this.initialReconnectDelay = builder.initialReconnectDelay;
        this.maxReconnectDelay = builder.maxReconnectDelay;
        this.reconnectBackoffMultiplier = builder.reconnectBackoffMultiplier;
        this.onOpen = builder.onOpen;
        this.onClose = builder.onClose;
        this.onMessage = builder.onMessage;
        this.onError = builder.onError;
        this.onReconnect = builder.onReconnect;
        this.onMaxReconnectAttemptsExceeded = builder.onMaxReconnectAttemptsExceeded;

        this.reconnectService = new ScheduledThreadPoolExecutor(1);
        createWebSocketClient();
    }

    private void createWebSocketClient() {
        client = new WebSocketClient(serverUri, httpHeaders) {
            @Override
            public void onOpen(@NotNull ServerHandshake handshakedata) {
                LOGGER.info(() -> String.format("WebSocket opened: %s", serverUri));
                reconnectAttempts.set(0);
                if (onOpen != null) {
                    onOpen.onEvent(handshakedata);
                }
            }

            @Override
            public void onMessage(@NotNull String message) {
                LOGGER.fine(() -> String.format("Received message on %s: %s", serverUri, message));
                if (onMessage != null) {
                    onMessage.onMessage(message);
                }
            }

            @Override
            public void onClose(int code, @Nullable String reason, boolean remote) {
                LOGGER.info(() -> String.format(
                        "WebSocket closed [%s] code=%d, reason=%s, remote=%s", serverUri, code, reason, remote));
                if (onClose != null) {
                    onClose.onEvent(new CloseEvent(code, reason, remote));
                }

                if (reconnectOnClose.get() && (code != 1000 || remote)) {
                    scheduleReconnect();
                }
            }

            @Override
            public void onError(@NotNull Exception ex) {
                LOGGER.log(Level.WARNING, String.format("WebSocket error on %s: %s", serverUri, ex.getMessage()), ex);
                if (onError != null) {
                    onError.onError(ex);
                }
                // Don't attempt to reconnect here - let onClose handle that
            }
        };
    }

    private void scheduleReconnect() {
        int attempts = reconnectAttempts.incrementAndGet();

        if (maxReconnectAttempts != -1 && attempts > maxReconnectAttempts) {
            LOGGER.warning(() -> String.format(
                    "Max reconnect attempts (%d) exceeded for %s", maxReconnectAttempts, serverUri));
            if (onMaxReconnectAttemptsExceeded != null) {
                onMaxReconnectAttemptsExceeded.onMaxReconnectAttemptsExceeded(maxReconnectAttempts);
            }
            return;
        }

        // Calculate exponential backoff delay
        long delay = calculateReconnectDelay(attempts);

        LOGGER.info(() -> String.format(
                "Scheduling reconnect #%d to %s in %d ms", attempts, serverUri, delay));

        if (onReconnect != null) {
            onReconnect.onReconnect(attempts, delay);
        }

        if (reconnectTask != null && !reconnectTask.isDone()) {
            reconnectTask.cancel(true);
        }

        reconnectTask = reconnectService.schedule(this::reconnect, delay, TimeUnit.MILLISECONDS);
    }

    private long calculateReconnectDelay(int attempts) {
        // Exponential backoff, capped at maxReconnectDelay
        double expDelay = initialReconnectDelay * Math.pow(reconnectBackoffMultiplier, attempts - 1);
        return Math.min((long) expDelay, maxReconnectDelay);
    }

    private void reconnect() {
        if (client != null && client.isOpen()) {
            LOGGER.fine(() -> String.format("Reconnect skipped: already open [%s]", serverUri));
            return;
        }

        try {
            LOGGER.info(() -> String.format("Attempting reconnect to %s...", serverUri));
            // Create a fresh client to avoid potential issues with the old one
            createWebSocketClient();
            if (client != null) {
                client.connect();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, String.format("Reconnect failed on %s: %s", serverUri, e.getMessage()), e);
            if (onError != null) {
                onError.onError(e);
            }
            // Try again later
            scheduleReconnect();
        }
    }

    /**
     * Connect to the WebSocket server.
     */
    public void connect() {
        reconnectOnClose.set(true);
        LOGGER.info(() -> String.format("Connecting to %s...", serverUri));
        if (client != null) {
            client.connect();
        }
    }

    /**
     * Send a text message to the server.
     *
     * @param message The message to send
     */
    public void send(@NotNull String message) {
        LOGGER.fine(() -> String.format("Sending message to %s: %s", serverUri, message));
        if (client != null) {
            client.send(message);
        }
    }

    /**
     * Closes the WebSocket connection with normal status code (1000).
     * Will not attempt to reconnect after this call.
     */
    public void close() {
        reconnectOnClose.set(false);
        if (reconnectTask != null) {
            reconnectTask.cancel(true);
        }
        LOGGER.info(() -> String.format("Closing connection to %s", serverUri));
        if (client != null) {
            client.close();
        }
    }

    /**
     * Closes the WebSocket connection with specified code and reason.
     * Will not attempt to reconnect after this call.
     */
    public void close(int code, @Nullable String reason) {
        reconnectOnClose.set(false);
        if (reconnectTask != null) {
            reconnectTask.cancel(true);
        }
        LOGGER.info(() -> String.format("Closing connection to %s with code=%d, reason=%s", serverUri, code, reason));
        if (client != null) {
            client.close(code, reason);
        }
    }

    /**
     * Checks if the connection is open.
     *
     * @return true if the connection is open
     */
    public boolean isOpen() {
        return client != null && client.isOpen();
    }

    /**
     * Shuts down the reconnection scheduler.
     * Call this method when you're done with the client to free resources.
     */
    public void shutdown() {
        close();
        reconnectService.shutdown();
        LOGGER.info(() -> String.format("Shutdown called on %s", serverUri));
    }

    // Listener interfaces

    public interface EventListener {
        void onEvent(@Nullable Object data);
    }

    public interface MessageListener {
        void onMessage(@NotNull String message);
    }

    public interface ErrorListener {
        void onError(@NotNull Exception exception);
    }

    public interface ReconnectListener {
        void onReconnect(int attempt, long delay);
    }

    public interface ReconnectFailedListener {
        void onMaxReconnectAttemptsExceeded(int maxAttempts);
    }

    // Event data classes

    public static class CloseEvent {
        private final int code;
        @Nullable
        private final String reason;
        private final boolean remote;

        public CloseEvent(int code, @Nullable String reason, boolean remote) {
            this.code = code;
            this.reason = reason;
            this.remote = remote;
        }

        public int getCode() {
            return code;
        }

        @Nullable
        public String getReason() {
            return reason;
        }

        public boolean isRemote() {
            return remote;
        }
    }

    @NotNull
    public static Builder builder(@NotNull URI uri) {
        return new Builder(uri);
    }

    /**
     * Builder for creating instances of ReconnectingWebSocketClient with a fluent API.
     */
    public static class Builder {
        // Required parameters
        @NotNull
        private final URI serverUri;

        // Optional parameters with default values
        @Nullable
        private Map<String, String> httpHeaders = null;
        private int maxReconnectAttempts = 10;
        private long initialReconnectDelay = 1000; // in milliseconds
        private long maxReconnectDelay = 30000; // in milliseconds
        private float reconnectBackoffMultiplier = 1.5f;

        // Event listeners
        @Nullable
        private EventListener onOpen = null;
        @Nullable
        private EventListener onClose = null;
        @Nullable
        private MessageListener onMessage = null;
        @Nullable
        private ErrorListener onError = null;
        @Nullable
        private ReconnectListener onReconnect = null;
        @Nullable
        private ReconnectFailedListener onMaxReconnectAttemptsExceeded = null;

        /**
         * Construct a builder with the required server URI.
         *
         * @param serverUri the WebSocket server URI
         */
        public Builder(@NotNull URI serverUri) {
            this.serverUri = serverUri;
        }

        /**
         * Set HTTP headers for the WebSocket connection.
         *
         * @param httpHeaders the HTTP headers
         * @return the builder instance
         */
        @NotNull
        public Builder httpHeaders(@Nullable Map<String, String> httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        /**
         * Set the maximum number of reconnection attempts.
         * Use -1 for unlimited reconnection attempts.
         *
         * @param maxReconnectAttempts the maximum number of reconnect attempts
         * @return the builder instance
         */
        @NotNull
        public Builder maxReconnectAttempts(int maxReconnectAttempts) {
            this.maxReconnectAttempts = maxReconnectAttempts;
            return this;
        }

        /**
         * Set the initial delay between reconnection attempts in milliseconds.
         *
         * @param initialReconnectDelay the initial delay in milliseconds
         * @return the builder instance
         */
        @NotNull
        public Builder initialReconnectDelay(long initialReconnectDelay) {
            this.initialReconnectDelay = initialReconnectDelay;
            return this;
        }

        /**
         * Set the maximum delay between reconnection attempts in milliseconds.
         *
         * @param maxReconnectDelay the maximum delay in milliseconds
         * @return the builder instance
         */
        @NotNull
        public Builder maxReconnectDelay(long maxReconnectDelay) {
            this.maxReconnectDelay = maxReconnectDelay;
            return this;
        }

        /**
         * Set the multiplier for exponential backoff between reconnection attempts.
         *
         * @param reconnectBackoffMultiplier the multiplier for exponential backoff
         * @return the builder instance
         */
        @NotNull
        public Builder reconnectBackoffMultiplier(float reconnectBackoffMultiplier) {
            this.reconnectBackoffMultiplier = reconnectBackoffMultiplier;
            return this;
        }

        /**
         * Set the event handler for connection open events.
         *
         * @param onOpen the event listener
         * @return the builder instance
         */
        @NotNull
        public Builder onOpen(@Nullable EventListener onOpen) {
            this.onOpen = onOpen;
            return this;
        }

        /**
         * Set the event handler for connection close events.
         *
         * @param onClose the event listener
         * @return the builder instance
         */
        @NotNull
        public Builder onClose(@Nullable EventListener onClose) {
            this.onClose = onClose;
            return this;
        }

        /**
         * Set the event handler for message events.
         *
         * @param onMessage the message listener
         * @return the builder instance
         */
        @NotNull
        public Builder onMessage(@Nullable MessageListener onMessage) {
            this.onMessage = onMessage;
            return this;
        }

        /**
         * Set the event handler for error events.
         *
         * @param onError the error listener
         * @return the builder instance
         */
        @NotNull
        public Builder onError(@Nullable ErrorListener onError) {
            this.onError = onError;
            return this;
        }

        /**
         * Set the event handler for reconnection attempts.
         *
         * @param onReconnect the reconnect listener
         * @return the builder instance
         */
        @NotNull
        public Builder onReconnect(@Nullable ReconnectListener onReconnect) {
            this.onReconnect = onReconnect;
            return this;
        }

        /**
         * Set the event handler for when max reconnection attempts are exceeded.
         *
         * @param onMaxReconnectAttemptsExceeded the listener
         * @return the builder instance
         */
        @NotNull
        public Builder onMaxReconnectAttemptsExceeded(@Nullable ReconnectFailedListener onMaxReconnectAttemptsExceeded) {
            this.onMaxReconnectAttemptsExceeded = onMaxReconnectAttemptsExceeded;
            return this;
        }

        /**
         * Build the configured ReconnectingWebSocketClient.
         *
         * @return a new ReconnectingWebSocketClient instance
         */
        @NotNull
        public ReconnectingWebSocketClient build() {
            return new ReconnectingWebSocketClient(this);
        }
    }
}