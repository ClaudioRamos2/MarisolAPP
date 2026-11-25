package com.example.marisolapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();

        // Si ya inició sesión antes, entra directo al panel
        if (auth.getCurrentUser() != null) {
            irAlPanel();
            return;
        }

        EditText txtUsuario = findViewById(R.id.txtUsuario);
        EditText txtPassword = findViewById(R.id.txtPassword);
        Button btnIngresar = findViewById(R.id.btnIngresar);
        Button btnRegresar = findViewById(R.id.btnRegresar);

        btnIngresar.setOnClickListener(v -> {
            String correo = txtUsuario.getText().toString().trim();
            String clave = txtPassword.getText().toString();

            if (correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            btnIngresar.setEnabled(false);
            auth.signInWithEmailAndPassword(correo, clave)
                    .addOnSuccessListener(result -> irAlPanel())
                    .addOnFailureListener(e -> {
                        btnIngresar.setEnabled(true);
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_LONG).show();
                    });
        });

        btnRegresar.setOnClickListener(v -> finish());
    }

    private void irAlPanel() {
        startActivity(new Intent(this, AdminActivity.class));
        finish();
    }
}