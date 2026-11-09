package com.example.pr_garg_ii.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr_garg_ii.R;
import com.example.pr_garg_ii.db.LibroEntity;

import java.util.ArrayList;
import java.util.List;

// Adapter de favoritos
public class FavoritoAdapter extends RecyclerView.Adapter<FavoritoAdapter.ViewHolder> {

    public interface Listener {
        void onFavoritoClick(LibroEntity libro);
        void onEliminarClick(LibroEntity libro);
    }

    private final List<LibroEntity> favoritos = new ArrayList<>();
    private final Listener listener;

    public FavoritoAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setFavoritos(List<LibroEntity> nuevos) {
        favoritos.clear();
        favoritos.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_libro, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        LibroEntity libro = favoritos.get(position);
        LibroAdapter.LibroViewBinder.bind(h.itemView, libro.toLibro());
        h.btnEliminar.setImageResource(R.drawable.ic_delete);
        h.btnEliminar.setContentDescription(h.itemView.getContext().getString(R.string.eliminar));
        h.itemView.setOnClickListener(v -> listener.onFavoritoClick(libro));
        h.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(libro));
    }

    @Override
    public int getItemCount() { return favoritos.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageButton btnEliminar;

        ViewHolder(View v) {
            super(v);
            btnEliminar = v.findViewById(R.id.btnAccion);
        }
    }
}
