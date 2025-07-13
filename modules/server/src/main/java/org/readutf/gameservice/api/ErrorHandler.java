package org.readutf.gameservice.api;

import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;
import org.jetbrains.annotations.NotNull;

public class ErrorHandler implements ExceptionHandler<Exception> {
    @Override
    public void handle(@NotNull Exception exception, @NotNull Context ctx) {

    }
}
