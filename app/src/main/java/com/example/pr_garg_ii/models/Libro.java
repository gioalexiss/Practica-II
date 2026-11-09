package com.example.pr_garg_ii.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

// DTO del libro
public class Libro implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("autor")
    private String autor;

    @SerializedName("editorial")
    private String editorial;

    @SerializedName("paginas")
    private Integer paginas;

    @SerializedName("disponible")
    private Boolean disponible;

    public Libro() { }

    public Libro(Long id, String titulo, String autor, String editorial, Integer paginas, Boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.paginas = paginas;
        this.disponible = disponible;
    }

    public long getId() { return id != null ? id : 0L; }
    public String getTitulo() { return titulo != null ? titulo : ""; }
    public String getAutor() { return autor != null ? autor : ""; }
    public String getEditorial() { return editorial != null ? editorial : ""; }
    public int getPaginas() { return paginas != null ? paginas : 0; }
    public boolean isDisponible() { return disponible != null && disponible; }
}
