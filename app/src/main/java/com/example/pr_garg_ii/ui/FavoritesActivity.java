package com.example.pr_garg_ii.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr_garg_ii.R;
import com.example.pr_garg_ii.adapters.FavoritoAdapter;
import com.example.pr_garg_ii.db.FavoritosRepository;
import com.example.pr_garg_ii.db.LibroEntity;
import com.example.pr_garg_ii.prefs.PreferencesManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

// Pantalla de favoritos
public class FavoritesActivity extends BaseActivity implements FavoritoAdapter.Listener {

    private FavoritoAdapter adapter;
    private FavoritosRepository repo;
    private PreferencesManager prefs;
    private TextView tvVacio, tvResumen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarPantalla(R.layout.activity_favorites, getString(R.string.titulo_favoritos), true);

        repo = new FavoritosRepository(this);
        prefs = new PreferencesManager(this);
        tvVacio = findViewById(R.id.tvVacio);
        tvResumen = findViewById(R.id.tvResumen);

        RecyclerView rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoritoAdapter(this);
        rv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs.setUltimaSeccion(PreferencesManager.SECCION_FAVORITOS);
        cargarFavoritos();
    }

    // Consulta sin Internet
    private void cargarFavoritos() {
        repo.obtenerTodos(lista -> {
            if (!activa()) return;
            adapter.setFavoritos(lista);
            tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
            tvResumen.setText(getString(R.string.resumen_favoritos, lista.size()));
        });
    }

    @Override
    public void onFavoritoClick(LibroEntity libro) {
        startActivity(DetailActivity.intent(this, libro.id));
    }

    @Override
    public void onEliminarClick(LibroEntity libro) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.eliminar_favorito_titulo)
                .setMessage(getString(R.string.eliminar_favorito_mensaje, libro.titulo))
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.eliminar, (d, w) ->
                        repo.eliminar(libro.id, r -> {
                            Toast.makeText(this, getString(R.string.favorito_eliminado, libro.titulo),
                                    Toast.LENGTH_SHORT).show();
                            cargarFavoritos();
                        }))
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_catalogo) {
            startActivity(new Intent(this, CatalogActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
