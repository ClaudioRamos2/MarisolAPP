package com.example.marisolapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnProductos = findViewById(R.id.btnProductos);
        Button btnServicios = findViewById(R.id.btnServicios);

        btnProductos.setOnClickListener(v ->
                startActivity(new Intent(this, GestionProductosActivity.class)));

        btnServicios.setOnClickListener(v ->
                startActivity(new Intent(this, GestionServiciosActivity.class)));
    }

    public void cerrar(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent cerrar = new Intent(this, LoginActivity.class);
        startActivity(cerrar);
        finish();
    }
}