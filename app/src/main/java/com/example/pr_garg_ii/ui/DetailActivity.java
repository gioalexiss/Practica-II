package com.example.pr_garg_ii.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pr_garg_ii.R;
import com.example.pr_garg_ii.api.ApiErrorHandler;
import com.example.pr_garg_ii.api.RetrofitClient;
import com.example.pr_garg_ii.db.FavoritosRepository;
import com.example.pr_garg_ii.models.Libro;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Detalle del libro
public class DetailActivity extends BaseActivity {

    private static final String EXTRA_ID = "extra_libro_id";

    public static Intent intent(Context ctx, long id) {
        return new Intent(ctx, DetailActivity.class).putExtra(EXTRA_ID, id);
    }

    private long libroId;
    private Libro libro;
    private boolean esFavorito;
    private FavoritosRepository repo;
    private Call<Libro> llamada;

    private View cardDetalle;
    private ProgressBar progress;
    private TextView tvTitulo, tvId, tvAutor, tvEditorial, tvPaginas, tvDisponible, tvOrigen, tvError;
    private MaterialButton btnFavorito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarPantalla(R.layout.activity_detail, getString(R.string.titulo_detalle), true);

        libroId = getIntent().getLongExtra(EXTRA_ID, -1);
        repo = new FavoritosRepository(this);

        cardDetalle = findViewById(R.id.cardDetalle);
        progress = findViewById(R.id.progress);
        tvTitulo = findViewById(R.id.tvTitulo);
        tvId = findViewById(R.id.tvId);
        tvAutor = findViewById(R.id.tvAutor);
        tvEditorial = findViewById(R.id.tvEditorial);
        tvPaginas = findViewById(R.id.tvPaginas);
        tvDisponible = findViewById(R.id.tvDisponible);
        tvOrigen = findViewById(R.id.tvOrigen);
        tvError = findViewById(R.id.tvError);
        btnFavorito = findViewById(R.id.btnFavorito);

        btnFavorito.setOnClickListener(v -> alternarFavorito());
        findViewById(R.id.btnReintentar).setOnClickListener(v -> cargar());

        cargar();
    }

    @Override
    protected void onDestroy() {
        if (llamada != null) llamada.cancel();
        super.onDestroy();
    }

    private void cargar() {
        tvError.setVisibility(View.GONE);
        findViewById(R.id.btnReintentar).setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        // Primero Room
        repo.obtenerPorId(libroId, local -> {
            if (!activa()) return;
            esFavorito = local != null;
            if (local != null && libro == null) {
                mostrar(local.toLibro(), false);
            }
            actualizarBoton();
            // Luego el servidor
            consultarServidor();
        });
    }

    private void consultarServidor() {
        llamada = RetrofitClient.getService().obtenerLibro(libroId);
        llamada.enqueue(new Callback<Libro>() {
            @Override
            public void onResponse(@NonNull Call<Libro> call, @NonNull Response<Libro> response) {
                if (!activa()) return;
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    mostrar(response.body(), true);
                } else {
                    error(ApiErrorHandler.mensaje(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Libro> call, @NonNull Throwable t) {
                if (call.isCanceled() || !activa()) return;
                progress.setVisibility(View.GONE);
                error(ApiErrorHandler.mensaje(t));
            }
        });
    }

    private void error(String mensaje) {
        if (libro != null) {
            // Hay copia local
            Toast.makeText(this, getString(R.string.mostrando_offline, mensaje), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            tvError.setText(mensaje);
            tvError.setVisibility(View.VISIBLE);
            findViewById(R.id.btnReintentar).setVisibility(View.VISIBLE);
        }
    }

    private void mostrar(Libro l, boolean desdeServidor) {
        libro = l;
        cardDetalle.setVisibility(View.VISIBLE);
        btnFavorito.setVisibility(View.VISIBLE);
        tvTitulo.setText(l.getTitulo());
        tvId.setText(String.valueOf(l.getId()));
        tvAutor.setText(l.getAutor());
        tvEditorial.setText(l.getEditorial());
        tvPaginas.setText(String.valueOf(l.getPaginas()));
        tvDisponible.setText(l.isDisponible() ? R.string.disponible : R.string.no_disponible);
        tvDisponible.setTextColor(getColor(l.isDisponible() ? R.color.disponible_texto : R.color.no_disponible_texto));
        tvOrigen.setText(desdeServidor ? R.string.origen_servidor : R.string.origen_local);
    }

    private void actualizarBoton() {
        btnFavorito.setText(esFavorito ? R.string.quitar_favorito : R.string.agregar_favorito);
        btnFavorito.setIconResource(esFavorito ? R.drawable.ic_star : R.drawable.ic_star_border);
    }

    private void alternarFavorito() {
        if (libro == null) return;
        btnFavorito.setEnabled(false);
        if (esFavorito) {
            repo.eliminar(libro.getId(), r -> {
                esFavorito = false;
                btnFavorito.setEnabled(true);
                actualizarBoton();
                Toast.makeText(this, getString(R.string.favorito_eliminado, libro.getTitulo()), Toast.LENGTH_SHORT).show();
            });
        } else {
            repo.agregar(libro, r -> {
                esFavorito = true;
                btnFavorito.setEnabled(true);
                actualizarBoton();
                Toast.makeText(this, getString(R.string.favorito_agregado, libro.getTitulo()), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
