package com.example.pr_garg_ii.api;

import com.example.pr_garg_ii.models.ErrorResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Response;

// Mensajes de error
public final class ApiErrorHandler {

    private ApiErrorHandler() { }

    // Errores HTTP
    public static String mensaje(Response<?> response) {
        String detalle = null;
        try {
            if (response.errorBody() != null) {
                ErrorResponse error = new Gson().fromJson(response.errorBody().charStream(), ErrorResponse.class);
                if (error != null && error.mensajes != null && !error.mensajes.isEmpty()) {
                    detalle = error.mensajes.values().iterator().next();
                } else if (error != null) {
                    detalle = error.error;
                }
            }
        } catch (Exception ignored) {
            // Mensaje genérico
        }

        int code = response.code();
        String base;
        if (code == 400) base = "Petición inválida (400)";
        else if (code == 404) base = "Libro no encontrado (404)";
        else if (code >= 500) base = "Error interno del servidor (" + code + ")";
        else base = "Error del servidor (" + code + ")";

        return detalle != null && !detalle.isEmpty() ? base + ": " + detalle : base;
    }

    // Errores de red
    public static String mensaje(Throwable t) {
        if (t instanceof UnknownHostException) {
            return "Sin conexión a Internet. Verifica tu red e inténtalo de nuevo.";
        } else if (t instanceof SocketTimeoutException) {
            return "El servidor tardó demasiado en responder. Inténtalo de nuevo.";
        } else if (t instanceof IOException) {
            return "Error de conexión con el servidor.";
        }
        return "Error inesperado al procesar la respuesta.";
    }
}
