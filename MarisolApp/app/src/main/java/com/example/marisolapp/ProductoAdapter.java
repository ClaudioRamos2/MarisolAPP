package com.example.marisolapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    public interface OnProductoClickListener {
        void onProductoClick(Productos producto);
        void onProductoLongClick(Productos producto);
    }

    private final List<Productos> listaProductos;
    private final OnProductoClickListener listener;

    public ProductoAdapter(List<Productos> listaProductos, OnProductoClickListener listener) {
        this.listaProductos = listaProductos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gestion_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Productos p = listaProductos.get(position);

        holder.tvNombre.setText(p.getNombre());
        String info = String.format(Locale.getDefault(), "%s • L. %.2f", p.getCategoria(), p.getPrecio());
        holder.tvCategoriaPrecio.setText(info);
        holder.tvEstado.setText(p.isDisponible() ? "Disponible" : "No disponible");

        if (p.getImagenUrl() != null && !p.getImagenUrl().trim().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(p.getImagenUrl().trim())
                    .centerCrop()
                    .into(holder.ivProducto);
        } else {
            holder.ivProducto.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductoClick(p);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onProductoLongClick(p);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProducto;
        TextView tvNombre, tvCategoriaPrecio, tvEstado;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProducto = itemView.findViewById(R.id.ivItemProducto);
            tvNombre = itemView.findViewById(R.id.tvItemNombre);
            tvCategoriaPrecio = itemView.findViewById(R.id.tvItemCategoriaPrecio);
            tvEstado = itemView.findViewById(R.id.tvItemEstado);
        }
    }
}