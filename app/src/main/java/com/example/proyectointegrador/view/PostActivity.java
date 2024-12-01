package com.example.proyectointegrador.view;

/*import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;*/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.proyectointegrador.Manifest;
import com.example.proyectointegrador.adapters.ImageAdapter;
import com.example.proyectointegrador.databinding.ActivityPostBinding;
import com.example.proyectointegrador.model.Post;
import com.example.proyectointegrador.viewmodel.PostViewModel;
import com.example.proyectointegrador.util.ImageUtils;
import com.example.proyectointegrador.util.Validaciones;

import java.util.ArrayList;
import java.util.List;
/*import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyectointegrador.R;
import com.example.proyectointegrador.databinding.ActivityUserBinding;
import com.example.proyectointegrador.model.User;
import com.example.proyectointegrador.viewmodel.PostViewModel;*/

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
        //setupCategorySpinner();
        setupGalleryLauncher();
        binding.btPublicar.setOnClickListener(v -> publicarPost());
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
           String message = success ? "Publicación exitosa" : "Error al publicar";
           Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
           if (success) finish();
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData();
                        if (imageUri != null && imagenesUrls.size() < MAX_IMAGE) {
                            ImageUtils.subirImagenParse(PostActivity.this, imageUri, new ImageUtils.OnImageUploadedListener() {
                                @Override
                                public void onSuccess(String imageUrl) {
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
        binding.btAgregarImagen.setOnClickListener(v -> {
            Log.d("PostActivity", "Botón de agregar imagen clickeado");
            ImageUtils.pedirPermisos(PostActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },
                    REQUEST_IMAGE);
        });
    }

    private void publicarPost() {
        String titulo = binding.etTitulo.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();
        String duracionStr = binding.etDuracion.getText().toString().trim();
        String presupuestoStr = binding.etPresupuesto.getText().toString().trim();

        if (!Validaciones.validarTexto(titulo)) {
            binding.etTitulo.setError("El título no es válido");
            return;
        }
        if (!Validaciones.validarTexto(descripcion)) {
            binding.etDescripcion.setError("La descripción no es válida");
            return;
        }
        int duracion = Validaciones.validarDuracion(duracionStr);
        if (duracion == 1) {
            binding.etDuracion.setError("Duración no válida");
            return;
        }
        double presupuesto;
        try {
            presupuesto = Double.parseDouble(presupuestoStr);
        } catch (NumberFormatException e) {
            binding.etPresupuesto.setError("Presupuesto no válido");
            return;
        }

        Post post = new Post(titulo, descripcion, categoria, duracion, presupuesto, new ArrayList<>(imagenesUrls));
        postViewModel.publicar(post);
    }

    private void updateRecyclerViewVisibility() {
        boolean hasImages = !imagenesUrls.isEmpty();
        binding.recyclerView.setVisibility(hasImages ? View.VISIBLE : View.GONE);
        binding.btAgregarImagen.setVisibility(imagenesUrls.size() < MAX_IMAGE ? View.VISIBLE : View.GONE);
    }
}




   /* private ActivityUserBinding binding;
    private PostViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_post);
        viewModel = new ViewModelProvider(this).get(PostViewModel.class);
        esperandoObservers();
        manejarEventos();
    }

    private void esperandoObservers() {
        viewModel.getOperationStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String status) {
                Toast.makeText(PostActivity.this, status, Toast.LENGTH_SHORT).show();
                limpiar();
            }
        });
        viewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) mostrarUsuario(user);
            }
        });
        }


    private void manejarEventos() {
        binding.btCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = obtenerDatosUsuario();
                viewModel.createUser(usuario, PostActivity.this);
            }
        });
        binding.btUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = obtenerDatosUsuario();
                viewModel.updateUser(user, PostActivity.this);
            }
        });
        binding.btDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = binding.etId.getText().toString().trim();
                viewModel.deleteUser(id, PostActivity.this);
            }
        });
        binding.btReadUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etMail.getText().toString().trim();
                viewModel.getUser(email, PostActivity.this);
            }
        });
        binding.circuloBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private User obtenerDatosUsuario() {
        String username = binding.etUsuario.getText().toString().trim();
        String email = binding.etMail.getText().toString().trim();
        String id = binding.etId.getText().toString().trim();
        String password = binding.etPassword1.getText().toString().trim();
        return new User(username, email, id, password);
    }

    private void mostrarUsuario(User user) {
        binding.etUsuario.getText().toString().trim();
        binding.etMail.getText().toString().trim();
        binding.etId.getText().toString().trim();
        binding.etPassword1.getText().toString().trim();
        Log.d("mostrar", user.getId() +"-"+ user.getUsername());
    }

    private void limpiar() {
        binding.etUsuario.setText("");
        binding.etMail.setText("");
        binding.etId.setText("");
        binding.etPassword1.setText("");
    }*/
