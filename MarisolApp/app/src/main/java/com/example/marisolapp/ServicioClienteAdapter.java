package com.example.marisolapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ServicioClienteAdapter extends RecyclerView.Adapter<ServicioClienteAdapter.ViewHolder> {

    private final List<Servicio> listaServicios;

    public ServicioClienteAdapter(List<Servicio> listaServicios) {
        this.listaServicios = listaServicios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_servicio, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Servicio servicio = listaServicios.get(position);
        holder.tvNombre.setText(servicio.getNombre());
        holder.tvDescripcion.setText(servicio.getDescripcion());
        holder.tvIcono.setText(iconoPara(servicio.getNombre()));
    }

    // Elige un emoji según el nombre del servicio; si no reconoce ninguno, usa uno genérico.
    private String iconoPara(String nombre) {
        if (nombre == null) return "🛠️";
        String n = nombre.toLowerCase(Locale.ROOT);

        if (n.contains("encuadern")) return "📚";
        if (n.contains("maquet")) return "🏠";
        if (n.contains("manualid")) return "✂️";
        if (n.contains("personaliz")) return "🎁";
        if (n.contains("impres")) return "🖨️";
        if (n.contains("lamin")) return "📄";
        if (n.contains("copia") || n.contains("fotocop")) return "📑";
        return "🛠️";
    }

    @Override
    public int getItemCount() {
        return listaServicios.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcono, tvNombre, tvDescripcion;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcono = itemView.findViewById(R.id.tvIconoServicio);
            tvNombre = itemView.findViewById(R.id.tvNombreServicioCliente);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionServicioCliente);
        }
    }
}