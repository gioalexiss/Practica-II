package com.example.pr_garg_ii.api;

import com.example.pr_garg_ii.models.Libro;
import com.example.pr_garg_ii.models.LibrosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

// Endpoints de la API
public interface LibroApiService {

    // Todos los libros
    @GET("api/v1/libros")
    Call<LibrosResponse> obtenerLibros();

    // Libro por ID
    @GET("api/v1/libros/{id}")
    Call<Libro> obtenerLibro(@Path("id") long id);
}
