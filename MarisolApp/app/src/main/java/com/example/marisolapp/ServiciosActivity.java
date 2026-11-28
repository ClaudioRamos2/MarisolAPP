package com.example.marisolapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ServiciosActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView rvServiciosCliente;
    private ProgressBar progressServicios;
    private TextView tvSinServiciosCliente;

    private List<Servicio> listaServicios;
    private ServicioClienteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_servicios);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        ImageButton btnRegresar = findViewById(R.id.btnRegresarServiciosCliente);
        rvServiciosCliente = findViewById(R.id.rvServiciosCliente);
        progressServicios = findViewById(R.id.progressServicios);
        tvSinServiciosCliente = findViewById(R.id.tvSinServiciosCliente);

        listaServicios = new ArrayList<>();
        adapter = new ServicioClienteAdapter(listaServicios);
        rvServiciosCliente.setLayoutManager(new LinearLayoutManager(this));
        rvServiciosCliente.setAdapter(adapter);

        btnRegresar.setOnClickListener(v -> finish());

        escucharServiciosEnTiempoReal();
    }


    private void escucharServiciosEnTiempoReal() {
        db.collection("servicios")
                .orderBy("nombre", Query.Direction.ASCENDING)
                .addSnapshotListener(this, (snapshots, error) -> {
                    progressServicios.setVisibility(View.GONE);

                    if (error != null) {
                        Toast.makeText(this, "Error al cargar servicios: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
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
                    tvSinServiciosCliente.setVisibility(listaServicios.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }
}