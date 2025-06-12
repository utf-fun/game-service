package org.readutf.gameservice.exceptions;

public class GameServiceException extends Exception {
    public GameServiceException(String message) {
        super(message);
    }

    public GameServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
