package com.example.pr_garg_ii.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pr_garg_ii.R;
import com.example.pr_garg_ii.adapters.LibroAdapter;
import com.example.pr_garg_ii.api.ApiErrorHandler;
import com.example.pr_garg_ii.api.RetrofitClient;
import com.example.pr_garg_ii.db.FavoritosRepository;
import com.example.pr_garg_ii.models.Libro;
import com.example.pr_garg_ii.models.LibrosResponse;
import com.example.pr_garg_ii.prefs.PreferencesManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Catálogo de libros
public class CatalogActivity extends BaseActivity implements LibroAdapter.Listener {

    private LibroAdapter adapter;
    private FavoritosRepository repo;
    private PreferencesManager prefs;

    private SwipeRefreshLayout swipe;
    private ProgressBar progress;
    private View layoutError;
    private TextView tvError, tvResumen;

    private Call<LibrosResponse> llamada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarPantalla(R.layout.activity_catalog, getString(R.string.titulo_catalogo), true);

        repo = new FavoritosRepository(this);
        prefs = new PreferencesManager(this);

        swipe = findViewById(R.id.swipe);
        progress = findViewById(R.id.progress);
        layoutError = findViewById(R.id.layoutError);
        tvError = findViewById(R.id.tvError);
        tvResumen = findViewById(R.id.tvResumen);

        RecyclerView rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibroAdapter(this);
        rv.setAdapter(adapter);

        swipe.setOnRefreshListener(() -> cargarCatalogo(true));
        findViewById(R.id.btnReintentar).setOnClickListener(v -> cargarCatalogo(false));

        cargarCatalogo(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs.setUltimaSeccion(PreferencesManager.SECCION_CATALOGO);
        refrescarFavoritos();
    }

    @Override
    protected void onDestroy() {
        if (llamada != null) llamada.cancel();
        super.onDestroy();
    }

    // Petición al servidor
    private void cargarCatalogo(boolean manual) {
        if (llamada != null) llamada.cancel();
        layoutError.setVisibility(View.GONE);
        if (!manual) progress.setVisibility(View.VISIBLE);

        llamada = RetrofitClient.getService().obtenerLibros();
        llamada.enqueue(new Callback<LibrosResponse>() {
            @Override
            public void onResponse(@NonNull Call<LibrosResponse> call, @NonNull Response<LibrosResponse> response) {
                if (!activa()) return;
                terminarCarga();
                if (response.isSuccessful() && response.body() != null) {
                    List<Libro> libros = response.body().getLibros();
                    adapter.setLibros(libros);
                    tvResumen.setText(getString(R.string.resumen_catalogo, libros.size()));
                    tvResumen.setVisibility(View.VISIBLE);
                    refrescarFavoritos();
                    repo.sincronizar(libros, null);
                    if (libros.isEmpty()) {
                        mostrarError(getString(R.string.catalogo_vacio));
                    } else if (manual) {
                        Toast.makeText(CatalogActivity.this,
                                getString(R.string.catalogo_actualizado, libros.size()), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    manejarError(ApiErrorHandler.mensaje(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LibrosResponse> call, @NonNull Throwable t) {
                if (call.isCanceled() || !activa()) return;
                terminarCarga();
                manejarError(ApiErrorHandler.mensaje(t));
            }
        });
    }

    private void terminarCarga() {
        progress.setVisibility(View.GONE);
        swipe.setRefreshing(false);
    }

    private void manejarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        if (adapter.isEmpty()) mostrarError(mensaje);
    }

    private void mostrarError(String mensaje) {
        tvError.setText(mensaje);
        layoutError.setVisibility(View.VISIBLE);
    }

    private void refrescarFavoritos() {
        repo.obtenerIds(ids -> {
            if (activa()) adapter.setFavoritos(ids);
        });
    }

    @Override
    public void onLibroClick(Libro libro) {
        startActivity(DetailActivity.intent(this, libro.getId()));
    }

    @Override
    public void onFavoritoClick(Libro libro, boolean esFavorito) {
        if (esFavorito) {
            repo.eliminar(libro.getId(), r -> {
                Toast.makeText(this, getString(R.string.favorito_eliminado, libro.getTitulo()), Toast.LENGTH_SHORT).show();
                refrescarFavoritos();
            });
        } else {
            repo.agregar(libro, r -> {
                Toast.makeText(this, getString(R.string.favorito_agregado, libro.getTitulo()), Toast.LENGTH_SHORT).show();
                refrescarFavoritos();
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            swipe.setRefreshing(true);
            cargarCatalogo(true);
            return true;
        } else if (item.getItemId() == R.id.action_favoritos) {
            startActivity(new Intent(this, FavoritesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
