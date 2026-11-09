package com.example.pr_garg_ii.ui;

import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr_garg_ii.R;
import com.google.android.material.appbar.MaterialToolbar;

// Configuración común
public abstract class BaseActivity extends AppCompatActivity {

    protected void configurarPantalla(int layoutRes, String titulo, boolean mostrarAtras) {
        EdgeToEdge.enable(this);
        setContentView(layoutRes);

        View root = findViewById(R.id.root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo);
            getSupportActionBar().setDisplayHomeAsUpEnabled(mostrarAtras);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    protected boolean activa() {
        return !isFinishing() && !isDestroyed();
    }
}
