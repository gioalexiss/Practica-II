package com.example.pr_garg_ii;

import android.app.Application;

import com.example.pr_garg_ii.prefs.PreferencesManager;

// Aplica tema guardado
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesManager prefs = new PreferencesManager(this);
        PreferencesManager.aplicarTema(prefs.isTemaOscuro());
    }
}
