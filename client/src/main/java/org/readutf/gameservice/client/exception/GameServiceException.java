package org.readutf.gameservice.client.exception;

public class GameServiceException extends RuntimeException {
    public GameServiceException(String message) {
        super(message);
    }

    public GameServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
