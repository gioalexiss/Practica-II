package com.example.pr_garg_ii.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr_garg_ii.R;
import com.example.pr_garg_ii.models.Libro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Adapter del catálogo
public class LibroAdapter extends RecyclerView.Adapter<LibroAdapter.ViewHolder> {

    public interface Listener {
        void onLibroClick(Libro libro);
        void onFavoritoClick(Libro libro, boolean esFavorito);
    }

    private final List<Libro> libros = new ArrayList<>();
    private final Set<Long> favoritos = new HashSet<>();
    private final Listener listener;

    public LibroAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setLibros(List<Libro> nuevos) {
        libros.clear();
        libros.addAll(nuevos);
        notifyDataSetChanged();
    }

    public void setFavoritos(Set<Long> ids) {
        favoritos.clear();
        favoritos.addAll(ids);
        notifyDataSetChanged();
    }

    public boolean isEmpty() { return libros.isEmpty(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_libro, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Libro libro = libros.get(position);
        boolean esFavorito = favoritos.contains(libro.getId());
        LibroViewBinder.bind(h.itemView, libro);

        h.btnFavorito.setImageResource(esFavorito ? R.drawable.ic_star : R.drawable.ic_star_border);
        h.btnFavorito.setContentDescription(h.itemView.getContext().getString(
                esFavorito ? R.string.quitar_favorito : R.string.agregar_favorito));

        h.itemView.setOnClickListener(v -> listener.onLibroClick(libro));
        h.btnFavorito.setOnClickListener(v -> listener.onFavoritoClick(libro, esFavorito));
    }

    @Override
    public int getItemCount() { return libros.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageButton btnFavorito;

        ViewHolder(View v) {
            super(v);
            btnFavorito = v.findViewById(R.id.btnAccion);
        }
    }

    // Pinta un libro
    public static final class LibroViewBinder {
        private LibroViewBinder() { }

        public static void bind(View item, Libro libro) {
            ((TextView) item.findViewById(R.id.tvId)).setText("#" + libro.getId());
            ((TextView) item.findViewById(R.id.tvTitulo)).setText(libro.getTitulo());
            ((TextView) item.findViewById(R.id.tvAutor)).setText(libro.getAutor());
            ((TextView) item.findViewById(R.id.tvEditorial)).setText(libro.getEditorial());
            ((TextView) item.findViewById(R.id.tvPaginas)).setText(
                    item.getContext().getString(R.string.paginas_formato, libro.getPaginas()));
            TextView tvDisp = item.findViewById(R.id.tvDisponible);
            tvDisp.setText(libro.isDisponible() ? R.string.disponible : R.string.no_disponible);
            tvDisp.setBackgroundResource(libro.isDisponible() ? R.drawable.bg_chip_ok : R.drawable.bg_chip_no);
            tvDisp.setTextColor(item.getContext().getColor(
                    libro.isDisponible() ? R.color.disponible_texto : R.color.no_disponible_texto));
        }
    }
}
