package com.example.pr_garg_ii.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

// Respuesta del catálogo
public class LibrosResponse {

    @SerializedName("_embedded")
    private Embedded embedded;

    public List<Libro> getLibros() {
        if (embedded == null || embedded.libroDtoList == null) return new ArrayList<>();
        return embedded.libroDtoList;
    }

    public static class Embedded {
        @SerializedName("libroDtoList")
        List<Libro> libroDtoList;
    }
}
