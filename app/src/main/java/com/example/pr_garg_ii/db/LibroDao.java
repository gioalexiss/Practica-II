package com.example.pr_garg_ii.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// CRUD de favoritos
@Dao
public interface LibroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertar(LibroEntity libro);

    @Query("SELECT * FROM libros_favoritos ORDER BY fecha_guardado DESC")
    List<LibroEntity> obtenerTodos();

    @Query("SELECT * FROM libros_favoritos WHERE id = :id LIMIT 1")
    LibroEntity obtenerPorId(long id);

    @Query("SELECT id FROM libros_favoritos")
    List<Long> obtenerIds();

    @Query("SELECT COUNT(*) FROM libros_favoritos")
    int contar();

    @Update
    int actualizar(LibroEntity libro);

    @Delete
    void eliminar(LibroEntity libro);

    @Query("DELETE FROM libros_favoritos WHERE id = :id")
    void eliminarPorId(long id);
}
