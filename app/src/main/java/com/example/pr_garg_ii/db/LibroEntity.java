package com.example.pr_garg_ii.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.pr_garg_ii.models.Libro;

// Tabla de favoritos
@Entity(tableName = "libros_favoritos")
public class LibroEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public long id;

    @NonNull
    @ColumnInfo(name = "titulo")
    public String titulo = "";

    @NonNull
    @ColumnInfo(name = "autor")
    public String autor = "";

    @NonNull
    @ColumnInfo(name = "editorial")
    public String editorial = "";

    @ColumnInfo(name = "paginas")
    public int paginas;

    @ColumnInfo(name = "disponible")
    public boolean disponible;

    @ColumnInfo(name = "fecha_guardado")
    public long fechaGuardado;

    public static LibroEntity desde(Libro libro) {
        LibroEntity e = new LibroEntity();
        e.id = libro.getId();
        e.titulo = libro.getTitulo();
        e.autor = libro.getAutor();
        e.editorial = libro.getEditorial();
        e.paginas = libro.getPaginas();
        e.disponible = libro.isDisponible();
        e.fechaGuardado = System.currentTimeMillis();
        return e;
    }

    public Libro toLibro() {
        return new Libro(id, titulo, autor, editorial, paginas, disponible);
    }
}
