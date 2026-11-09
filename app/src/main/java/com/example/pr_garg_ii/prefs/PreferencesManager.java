package com.example.pr_garg_ii.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

// Preferencias del usuario
public class PreferencesManager {

    public static final String SECCION_CATALOGO = "catalogo";
    public static final String SECCION_FAVORITOS = "favoritos";

    private static final String PREFS_NAME = "explorador_libros_prefs";
    private static final String KEY_NOMBRE = "nombre_usuario";
    private static final String KEY_TEMA_OSCURO = "tema_oscuro";
    private static final String KEY_ULTIMA_SECCION = "ultima_seccion";
    private static final String KEY_ULTIMO_ACCESO = "ultimo_acceso";

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getNombre() { return prefs.getString(KEY_NOMBRE, ""); }
    public void setNombre(String nombre) { prefs.edit().putString(KEY_NOMBRE, nombre).apply(); }

    public boolean isTemaOscuro() { return prefs.getBoolean(KEY_TEMA_OSCURO, false); }
    public void setTemaOscuro(boolean oscuro) { prefs.edit().putBoolean(KEY_TEMA_OSCURO, oscuro).apply(); }

    public String getUltimaSeccion() { return prefs.getString(KEY_ULTIMA_SECCION, ""); }
    public void setUltimaSeccion(String seccion) { prefs.edit().putString(KEY_ULTIMA_SECCION, seccion).apply(); }

    public long getUltimoAcceso() { return prefs.getLong(KEY_ULTIMO_ACCESO, 0L); }
    public void setUltimoAcceso(long millis) { prefs.edit().putLong(KEY_ULTIMO_ACCESO, millis).apply(); }

    public static void aplicarTema(boolean oscuro) {
        AppCompatDelegate.setDefaultNightMode(oscuro
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static String nombreSeccion(String seccion) {
        if (SECCION_CATALOGO.equals(seccion)) return "Catálogo";
        if (SECCION_FAVORITOS.equals(seccion)) return "Favoritos";
        return "Ninguna";
    }
}
