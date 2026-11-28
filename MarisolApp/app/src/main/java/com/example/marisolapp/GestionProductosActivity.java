package com.example.marisolapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionProductosActivity extends AppCompatActivity {

    private static final String COLECCION_PRODUCTOS = "productos";

    private EditText etNombre, etCategoria, etPrecio, etImagenUrl, etDescripcion;
    private SwitchMaterial swDisponible;
    private Button btnGuardar, btnLimpiar;
    private RecyclerView rvProductos;

    private FirebaseFirestore db;
    private ProductoAdapter adapter;
    private final List<Productos> listaProductos = new ArrayList<>();
    private String productoIdEditando = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_productos);

        db = FirebaseFirestore.getInstance();

        etNombre = findViewById(R.id.etNombreProducto);
        etCategoria = findViewById(R.id.etCategoriaProducto);
        etPrecio = findViewById(R.id.etPrecioProducto);
        etImagenUrl = findViewById(R.id.etImagenUrl);
        etDescripcion = findViewById(R.id.etDescripcionProducto);
        swDisponible = findViewById(R.id.swDisponible);
        btnGuardar = findViewById(R.id.btnGuardarProducto);
        btnLimpiar = findViewById(R.id.btnLimpiarProducto);
        rvProductos = findViewById(R.id.rvGestionProductos);

        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductoAdapter(listaProductos, new ProductoAdapter.OnProductoClickListener() {
            @Override
            public void onProductoClick(Productos producto) {
                cargarProductoEnFormulario(producto);
            }

            @Override
            public void onProductoLongClick(Productos producto) {
                confirmarEliminarProducto(producto);
            }
        });
        rvProductos.setAdapter(adapter);

        btnGuardar.setOnClickListener(v -> guardarOActualizarProducto());
        btnLimpiar.setOnClickListener(v -> limpiarFormulario());

        escucharProductosFirestore();
    }

    private void guardarOActualizarProducto() {
        String nombre = etNombre.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String imagenUrl = etImagenUrl.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        boolean disponible = swDisponible.isChecked();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(categoria) || TextUtils.isEmpty(precioStr)) {
            Toast.makeText(this, "Completa nombre, categoría y precio", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingresa un precio válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("nombre", nombre);
        data.put("categoria", categoria);
        data.put("precio", precio);
        data.put("imagenUrl", imagenUrl);
        data.put("descripcion", descripcion);
        data.put("disponible", disponible);

        if (productoIdEditando == null) {
            db.collection(COLECCION_PRODUCTOS)
                    .add(data)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show();
                        limpiarFormulario();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            db.collection(COLECCION_PRODUCTOS)
                    .document(productoIdEditando)
                    .set(data)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                        limpiarFormulario();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void cargarProductoEnFormulario(Productos producto) {
        productoIdEditando = producto.getId();
        etNombre.setText(producto.getNombre());
        etCategoria.setText(producto.getCategoria());
        etPrecio.setText(String.valueOf(producto.getPrecio()));
        etImagenUrl.setText(producto.getImagenUrl());
        etDescripcion.setText(producto.getDescripcion());
        swDisponible.setChecked(producto.isDisponible());
        btnGuardar.setText("Actualizar");
    }

    private void confirmarEliminarProducto(Productos producto) {
        if (producto.getId() == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Deseas eliminar \"" + producto.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) ->
                        db.collection(COLECCION_PRODUCTOS)
                                .document(producto.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    if (producto.getId().equals(productoIdEditando)) {
                                        limpiarFormulario();
                                    }
                                    Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void escucharProductosFirestore() {
        db.collection(COLECCION_PRODUCTOS)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (snapshots == null) return;

                    listaProductos.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Productos p = doc.toObject(Productos.class);
                        if (p != null) {
                            p.setId(doc.getId());
                            listaProductos.add(p);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void limpiarFormulario() {
        productoIdEditando = null;
        etNombre.setText("");
        etCategoria.setText("");
        etPrecio.setText("");
        etImagenUrl.setText("");
        etDescripcion.setText("");
        swDisponible.setChecked(true);
        btnGuardar.setText("Guardar");
    }
}