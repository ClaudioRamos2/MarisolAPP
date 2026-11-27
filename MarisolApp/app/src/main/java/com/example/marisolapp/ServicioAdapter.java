package com.example.marisolapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> {

    public interface OnServicioActionListener {
        void onEditar(Servicio servicio);
        void onEliminar(Servicio servicio);
    }

    private final List<Servicio> listaServicios;
    private final OnServicioActionListener listener;

    public ServicioAdapter(List<Servicio> listaServicios, OnServicioActionListener listener) {
        this.listaServicios = listaServicios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_servicio_admin, parent, false);
        return new ServicioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioViewHolder holder, int position) {
        Servicio servicio = listaServicios.get(position);
        holder.tvNombre.setText(servicio.getNombre());
        holder.tvDescripcion.setText(servicio.getDescripcion());

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(servicio));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(servicio));
    }

    @Override
    public int getItemCount() {
        return listaServicios.size();
    }

    static class ServicioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion;
        Button btnEditar, btnEliminar;

        public ServicioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreServicio);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionServicio);
            btnEditar = itemView.findViewById(R.id.btnEditarItem);
            btnEliminar = itemView.findViewById(R.id.btnEliminarItem);
        }
    }
}