package com.example.pr_garg_ii.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.pr_garg_ii.models.Libro;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Room en segundo plano
public class FavoritosRepository {

    public interface Callback<T> {
        void onResult(T result);
    }

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private final LibroDao dao;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public FavoritosRepository(Context context) {
        dao = AppDatabase.getInstance(context).libroDao();
    }

    private <T> void entregar(Callback<T> cb, T result) {
        if (cb != null) mainHandler.post(() -> cb.onResult(result));
    }

    public void agregar(Libro libro, Callback<Void> cb) {
        EXECUTOR.execute(() -> {
            dao.insertar(LibroEntity.desde(libro));
            entregar(cb, null);
        });
    }

    public void eliminar(long id, Callback<Void> cb) {
        EXECUTOR.execute(() -> {
            dao.eliminarPorId(id);
            entregar(cb, null);
        });
    }

    public void obtenerTodos(Callback<List<LibroEntity>> cb) {
        EXECUTOR.execute(() -> entregar(cb, dao.obtenerTodos()));
    }

    public void obtenerPorId(long id, Callback<LibroEntity> cb) {
        EXECUTOR.execute(() -> entregar(cb, dao.obtenerPorId(id)));
    }

    public void obtenerIds(Callback<Set<Long>> cb) {
        EXECUTOR.execute(() -> entregar(cb, new HashSet<>(dao.obtenerIds())));
    }

    public void contar(Callback<Integer> cb) {
        EXECUTOR.execute(() -> entregar(cb, dao.contar()));
    }

    // Actualiza favoritos guardados
    public void sincronizar(List<Libro> librosServidor, Callback<Integer> cb) {
        EXECUTOR.execute(() -> {
            int actualizados = 0;
            for (Libro libro : librosServidor) {
                LibroEntity existente = dao.obtenerPorId(libro.getId());
                if (existente != null) {
                    LibroEntity nuevo = LibroEntity.desde(libro);
                    nuevo.fechaGuardado = existente.fechaGuardado;
                    actualizados += dao.actualizar(nuevo);
                }
            }
            entregar(cb, actualizados);
        });
    }
}
