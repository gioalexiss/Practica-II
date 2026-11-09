package com.example.pr_garg_ii.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pr_garg_ii.R;
import com.example.pr_garg_ii.db.FavoritosRepository;
import com.example.pr_garg_ii.prefs.PreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.util.Date;

// Pantalla de bienvenida
public class WelcomeActivity extends BaseActivity {

    private PreferencesManager prefs;
    private FavoritosRepository repo;

    private TextView tvSaludo, tvPrefNombre, tvPrefTema, tvPrefSeccion, tvPrefAcceso;
    private TextInputEditText etNombre;
    private MaterialSwitch switchTema;
    private MaterialButton btnFavoritos, btnContinuar;

    private long accesoAnterior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarPantalla(R.layout.activity_welcome, getString(R.string.app_name), false);

        prefs = new PreferencesManager(this);
        repo = new FavoritosRepository(this);

        tvSaludo = findViewById(R.id.tvSaludo);
        tvPrefNombre = findViewById(R.id.tvPrefNombre);
        tvPrefTema = findViewById(R.id.tvPrefTema);
        tvPrefSeccion = findViewById(R.id.tvPrefSeccion);
        tvPrefAcceso = findViewById(R.id.tvPrefAcceso);
        etNombre = findViewById(R.id.etNombre);
        switchTema = findViewById(R.id.switchTema);
        btnFavoritos = findViewById(R.id.btnFavoritos);
        btnContinuar = findViewById(R.id.btnContinuar);

        // Solo primer arranque
        if (savedInstanceState == null) {
            accesoAnterior = prefs.getUltimoAcceso();
            prefs.setUltimoAcceso(System.currentTimeMillis());
        } else {
            accesoAnterior = savedInstanceState.getLong("accesoAnterior", 0L);
        }

        etNombre.setText(prefs.getNombre());

        // Estado inicial
        switchTema.setChecked(prefs.isTemaOscuro());
        switchTema.setOnCheckedChangeListener((b, oscuro) -> {
            prefs.setTemaOscuro(oscuro);
            Toast.makeText(this, oscuro ? R.string.tema_oscuro_guardado : R.string.tema_claro_guardado,
                    Toast.LENGTH_SHORT).show();
            PreferencesManager.aplicarTema(oscuro);
        });

        findViewById(R.id.btnGuardarNombre).setOnClickListener(v -> guardarNombre());
        findViewById(R.id.btnCatalogo).setOnClickListener(v ->
                startActivity(new Intent(this, CatalogActivity.class)));
        btnFavoritos.setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
        btnContinuar.setOnClickListener(v -> continuarUltimaSeccion());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("accesoAnterior", accesoAnterior);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarPreferencias();
        repo.contar(total -> {
            if (activa()) btnFavoritos.setText(getString(R.string.ver_favoritos_n, total));
        });
    }

    private void guardarNombre() {
        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError(getString(R.string.error_nombre_vacio));
            return;
        }
        prefs.setNombre(nombre);
        ocultarTeclado();
        Toast.makeText(this, getString(R.string.nombre_guardado, nombre), Toast.LENGTH_SHORT).show();
        mostrarPreferencias();
    }

    private void mostrarPreferencias() {
        String nombre = prefs.getNombre();
        tvSaludo.setText(TextUtils.isEmpty(nombre)
                ? getString(R.string.saludo_anonimo)
                : getString(R.string.saludo_nombre, nombre));

        tvPrefNombre.setText(TextUtils.isEmpty(nombre) ? getString(R.string.sin_definir) : nombre);
        tvPrefTema.setText(prefs.isTemaOscuro() ? R.string.tema_oscuro : R.string.tema_claro);

        String seccion = prefs.getUltimaSeccion();
        tvPrefSeccion.setText(PreferencesManager.nombreSeccion(seccion));
        tvPrefAcceso.setText(accesoAnterior > 0
                ? DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date(accesoAnterior))
                : getString(R.string.primer_acceso));

        if (TextUtils.isEmpty(seccion)) {
            btnContinuar.setVisibility(View.GONE);
        } else {
            btnContinuar.setVisibility(View.VISIBLE);
            btnContinuar.setText(getString(R.string.continuar_en, PreferencesManager.nombreSeccion(seccion)));
        }
    }

    private void continuarUltimaSeccion() {
        String seccion = prefs.getUltimaSeccion();
        if (PreferencesManager.SECCION_FAVORITOS.equals(seccion)) {
            startActivity(new Intent(this, FavoritesActivity.class));
        } else {
            startActivity(new Intent(this, CatalogActivity.class));
        }
    }

    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(etNombre.getWindowToken(), 0);
        etNombre.clearFocus();
    }
}
