package com.example.marisolapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionServiciosActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextInputEditText etNombreServicio, etDescripcionServicio;
    private TextView tvModoFormulario, tvSinServicios;
    private Button btnGuardarServicio, btnCancelarEdicion;
    private RecyclerView rvServiciosAdmin;

    private List<Servicio> listaServicios;
    private ServicioAdapter adapter;
    private String servicioEnEdicionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_servicios);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        ImageButton btnRegresar = findViewById(R.id.btnRegresarServicios);
        tvModoFormulario = findViewById(R.id.tvModoFormulario);
        tvSinServicios = findViewById(R.id.tvSinServicios);
        etNombreServicio = findViewById(R.id.etNombreServicio);
        etDescripcionServicio = findViewById(R.id.etDescripcionServicio);
        btnGuardarServicio = findViewById(R.id.btnGuardarServicio);
        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicion);
        rvServiciosAdmin = findViewById(R.id.rvServiciosAdmin);

        listaServicios = new ArrayList<>();
        adapter = new ServicioAdapter(listaServicios, new ServicioAdapter.OnServicioActionListener() {
            @Override
            public void onEditar(Servicio servicio) {
                activarModoEdicion(servicio);
            }

            @Override
            public void onEliminar(Servicio servicio) {
                confirmarEliminacion(servicio);
            }
        });

        rvServiciosAdmin.setLayoutManager(new LinearLayoutManager(this));
        rvServiciosAdmin.setAdapter(adapter);

        btnRegresar.setOnClickListener(v -> finish());
        btnGuardarServicio.setOnClickListener(v -> guardarOActualizarServicio());
        btnCancelarEdicion.setOnClickListener(v -> limpiarFormulario());

        escucharServiciosEnTiempoReal();
    }

    private void escucharServiciosEnTiempoReal() {
        db.collection("servicios")
                .orderBy("nombre", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error al cargar servicios: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    listaServicios.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Servicio s = doc.toObject(Servicio.class);
                            if (s != null) {
                                s.setId(doc.getId());
                                listaServicios.add(s);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                    tvSinServicios.setVisibility(listaServicios.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void guardarOActualizarServicio() {
        String nombre = etNombreServicio.getText() != null ? etNombreServicio.getText().toString().trim() : "";
        String descripcion = etDescripcionServicio.getText() != null ? etDescripcionServicio.getText().toString().trim() : "";

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Completa el nombre y la descripción del servicio", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardarServicio.setEnabled(false);

        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", nombre);
        datos.put("descripcion", descripcion);

        if (servicioEnEdicionId == null) {
            db.collection("servicios")
                    .add(datos)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Servicio registrado correctamente", Toast.LENGTH_SHORT).show();
                        limpiarFormulario();
                        btnGuardarServicio.setEnabled(true);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        btnGuardarServicio.setEnabled(true);
                    });
        } else {
            db.collection("servicios")
                    .document(servicioEnEdicionId)
                    .update(datos)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Servicio actualizado correctamente", Toast.LENGTH_SHORT).show();
                        limpiarFormulario();
                        btnGuardarServicio.setEnabled(true);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        btnGuardarServicio.setEnabled(true);
                    });
        }
    }

    private void activarModoEdicion(Servicio servicio) {
        servicioEnEdicionId = servicio.getId();
        tvModoFormulario.setText("Editando Servicio");
        etNombreServicio.setText(servicio.getNombre());
        etDescripcionServicio.setText(servicio.getDescripcion());
        btnGuardarServicio.setText("ACTUALIZAR");
        btnCancelarEdicion.setVisibility(View.VISIBLE);
    }

    private void limpiarFormulario() {
        servicioEnEdicionId = null;
        tvModoFormulario.setText("Nuevo Servicio");
        etNombreServicio.setText("");
        etDescripcionServicio.setText("");
        btnGuardarServicio.setText("GUARDAR SERVICIO");
        btnCancelarEdicion.setVisibility(View.GONE);
    }

    private void confirmarEliminacion(Servicio servicio) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar servicio")
                .setMessage("¿Seguro que deseas eliminar \"" + servicio.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    db.collection("servicios")
                            .document(servicio.getId())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                if (servicio.getId().equals(servicioEnEdicionId)) {
                                    limpiarFormulario();
                                }
                                Toast.makeText(this, "Servicio eliminado", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}