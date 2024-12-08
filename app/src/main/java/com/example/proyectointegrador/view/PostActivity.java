package com.example.proyectointegrador.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;

import com.example.proyectointegrador.R;
import com.example.proyectointegrador.adapters.ImageAdapter;
import com.example.proyectointegrador.databinding.ActivityPostBinding;
import com.example.proyectointegrador.model.Post;
import com.example.proyectointegrador.viewmodel.PostViewModel;
import com.example.proyectointegrador.util.ImageUtils;
import com.example.proyectointegrador.util.Validaciones;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private static final int MAX_IMAGE = 3;
    private final int REQUEST_IMAGE = 1;
    private ActivityPostBinding binding;
    private PostViewModel postViewModel;
    private final List<String> imagenesUrls = new ArrayList<>();
    private ImageAdapter adapter;
    private String categoria;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupRecyclerView();
        setupViewModel();
        setupCategorySpinner();
        setupGalleryLauncher();
        binding.publicar.setOnClickListener(v -> publicarPost());
    }

    private void setupRecyclerView() {
        adapter = new ImageAdapter(imagenesUrls, this);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerView.setAdapter(adapter);
        updateRecyclerViewVisibility();
    }

    private void setupViewModel() {
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getPostSuccess().observe(this, success -> {
           String message = success ?"Publicación exitosa" : "Error al publicar";
           Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
           if (success) finish();
        });
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, getResources().getStringArray(R.array.categorias_array)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categoria = null;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null && imagenesUrls.size() < MAX_IMAGE) {
                            ImageUtils.subirImagenParse(PostActivity.this, imageUri, new ImageUtils.ImageUploadCallback() {
                                @Override
                                public void onSuccess(String imageUrl) {
                                    Log.d("PostActivity", "Imagen subida con éxito: " + imageUrl);
                                    imagenesUrls.add(imageUrl);
                                    adapter.notifyDataSetChanged();
                                    updateRecyclerViewVisibility();
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("PostActivity", "Error al subir la imagen", e);
                                    Toast.makeText(PostActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (imagenesUrls.size() >= MAX_IMAGE) {
                            Toast.makeText(PostActivity.this, "Máximo de imágenes alcanzado", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        binding.uploadImage.setOnClickListener(v -> {
            ImageUtils.pedirPermisos(PostActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },
                    REQUEST_IMAGE);
        });
    }

    private void publicarPost() {
        String titulo = binding.tituloPost.getText().toString().trim();
        String descripcion = binding.descripcion.getText().toString().trim();
        String duracionStr = binding.duracion.getText().toString().trim();
        String presupuestoStr = binding.presupuesto.getText().toString().trim();

        if (!Validaciones.validarTexto(titulo)) {
            binding.tituloPost.setError("El título no es válido");
            return;
        }
        if (!Validaciones.validarTexto(descripcion)) {
            binding.descripcion.setError("La descripción no es válida");
            return;
        }
        int duracion = Validaciones.validarNumero(duracionStr);
        if (duracion == 1) {
            binding.duracion.setError("Duración no válida");
            return;
        }
        double presupuesto;
        try {
            presupuesto = Double.parseDouble(presupuestoStr);
        } catch (NumberFormatException e) {
            binding.presupuesto.setError("Presupuesto no válido");
            return;
        }

        Post post = new Post(titulo, descripcion, categoria, duracion, presupuesto, new ArrayList<>(imagenesUrls));
        postViewModel.publicar(post);
    }

    private void updateRecyclerViewVisibility() {
        boolean hasImages = !imagenesUrls.isEmpty();
        binding.recyclerView.setVisibility(hasImages ? View.VISIBLE : View.GONE);
        binding.uploadImage.setVisibility(imagenesUrls.size() < MAX_IMAGE ? View.VISIBLE : View.GONE);
    }

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull ll String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("PostActivity", "onRequestPermissionsResult ejecutado");
        if (requestCode == REQUEST_IMAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("PostActivity", "Permiso concedido, abriendo galería");
            ImageUtils.openGallery(PostActivity.this, galleryLauncher);
        } else {
            Log.d("PostActivity", "Permiso denegado");
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    }*/
}