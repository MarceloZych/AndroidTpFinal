package com.example.proyectointegrador.view; // Paquete donde se encuentra la clase

import android.annotation.SuppressLint; // Importa la anotación para suprimir advertencias de lint
import android.app.Activity; // Importa Activity para manejar actividades de Android
import android.content.Intent; // Importa Intent para iniciar nuevas actividades
import android.content.pm.PackageManager; // Importa PackageManager para manejar permisos
import android.net.Uri; // Importa Uri para manejar URIs (identificadores de recursos)
import android.os.Bundle; // Importa Bundle para manejar el estado de la actividad
import android.util.Log; // Importa Log para registrar mensajes en el Logcat
import android.view.View; // Importa View para manejar vistas
import android.widget.AdapterView; // Importa AdapterView para manejar eventos de selección en vistas adaptadoras
import android.widget.ArrayAdapter; // Importa ArrayAdapter para gestionar listas de elementos
import android.widget.Toast; // Importa Toast para mostrar mensajes breves al usuario

import androidx.activity.result.ActivityResultLauncher; // Importa ActivityResultLauncher para manejar resultados de actividades
import androidx.activity.result.contract.ActivityResultContracts; // Importa contratos de resultados de actividades
import androidx.annotation.NonNull; // Importa la anotación para indicar que un parámetro no puede ser nulo
import androidx.appcompat.app.AppCompatActivity; // Importa AppCompatActivity para utilizar características modernas de Android
import androidx.lifecycle.ViewModelProvider; // Importa ViewModelProvider para obtener instancias de ViewModels
import androidx.recyclerview.widget.GridLayoutManager; // Importa GridLayoutManager para gestionar la disposición de elementos en un RecyclerView

import android.Manifest; // Importa Manifest para manejar permisos

import com.example.proyectointegrador.R; // Importa los recursos del proyecto
import com.example.proyectointegrador.adapters.ImageAdapter; // Importa el adaptador personalizado para imágenes
import com.example.proyectointegrador.databinding.ActivityPostBinding; // Importa el binding generado para ActivityPost
import com.example.proyectointegrador.model.Post; // Importa el modelo Post
import com.example.proyectointegrador.viewmodel.PostViewModel; // Importa el ViewModel asociado a los posts
import com.example.proyectointegrador.util.ImageUtils; // Importa la clase utilitaria ImageUtils
import com.example.proyectointegrador.util.Validaciones; // Importa la clase utilitaria Validaciones

import java.util.ArrayList; // Importa ArrayList para manejar listas dinámicas
import java.util.List; // Importa List para trabajar con colecciones

// Clase que representa la actividad donde los usuarios pueden crear un nuevo post
public class PostActivity extends AppCompatActivity {
    private static final int MAX_IMAGE = 3; // Número máximo de imágenes permitidas por post
    private final int REQUEST_IMAGE = 1; // Código de solicitud para abrir la galería de imágenes

    private ActivityPostBinding binding; // Variable para el binding de la actividad
    private PostViewModel postViewModel; // Variable para el ViewModel asociado a esta actividad

    private final List<String> imagenesUrls = new ArrayList<>(); // Lista que almacenará las URLs de las imágenes seleccionadas
    private ImageAdapter adapter; // Adaptador para mostrar las imágenes en un RecyclerView

    private String categoria; // Variable que almacenará la categoría seleccionada del post

    private ActivityResultLauncher<Intent> galleryLauncher; // Launcher para abrir la galería y obtener resultados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Llama al método onCreate de la clase padre

        binding = ActivityPostBinding.inflate(getLayoutInflater()); // Infla el layout utilizando View Binding
        setContentView(binding.getRoot()); // Establece el contenido de la actividad a la vista inflada

        setupRecyclerView(); // Configura el RecyclerView para mostrar las imágenes seleccionadas
        setupViewModel(); // Configura el ViewModel asociado a esta actividad
        setupCategorySpinner(); // Configura el spinner (selector) de categorías
        setupGalleryLauncher(); // Configura el launcher para abrir la galería al seleccionar imágenes

        binding.btnPublicar.setOnClickListener(v -> publicarPost()); // Configura el evento al hacer clic en el botón "Publicar"
    }

    private void setupRecyclerView() {
        adapter = new ImageAdapter(imagenesUrls, this); // Crea una nueva instancia del adaptador con las URLs de las imágenes y el contexto actual
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));  // Configura un GridLayoutManager con 3 columnas en el RecyclerView
        binding.recyclerView.setAdapter(adapter);  // Establece el adaptador en el RecyclerView

        updateRecyclerViewVisibility();  // Actualiza la visibilidad del RecyclerView según si hay imágenes o no
    }

    private void setupViewModel() {
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);  // Obtiene una instancia del ViewModel asociado a esta actividad

        postViewModel.getPostSuccess().observe(this, success -> {  // Observa los cambios en el resultado del posteo
            Toast.makeText(this, success, Toast.LENGTH_SHORT).show();  // Muestra un mensaje al usuario si se publicó con éxito
            finish();  // Cierra esta actividad y vuelve a la anterior (si corresponde)
        });
    }

    private  void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, getResources().getStringArray(R.array.categorias_array)
        );  // Crea un ArrayAdapter con las categorías disponibles desde los recursos

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  // Establece el layout del dropdown del spinner
        binding.spinnerCategoria.setAdapter(adapter);  // Establece el adaptador en el spinner
        binding.spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = (String) parent.getItemAtPosition(position);  // Almacena la categoría seleccionada
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categoria = null;  // Si no se seleccionó nada, establece categoría como nula
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")  // Suprime advertencias sobre notificaciones del adaptador (puede ser necesario)
    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();  // Obtiene el URI de la imagen seleccionada desde los resultados

                        if (imageUri != null && imagenesUrls.size() < MAX_IMAGE) {
                            ImageUtils.subirImagenParse(PostActivity.this, imageUri, new ImageUtils.ImageUploadCallback() {
                                @Override
                                public void onSuccess(String imageUrl) {
                                    Log.d("PostActivity", "Imagen subida con éxito: " + imageUrl);
                                    imagenesUrls.add(imageUrl);  // Agrega la URL de la imagen a la lista
                                    adapter.notifyDataSetChanged();  // Notifica al adaptador que los datos han cambiado
                                    updateRecyclerViewVisibility();  // Actualiza la visibilidad del RecyclerView
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

        binding.uploadImage.setOnClickListener(v -> {  // Configura un listener al hacer clic en el botón "Subir Imagen"
            ImageUtils.pedirPermisos(PostActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE  // Solicita permiso para leer almacenamiento externo
            }, REQUEST_IMAGE);
        });
    }

    private void publicarPost() {
        String titulo = binding.itTitulo.getText().toString().trim();  // Obtiene y limpia el texto del título del post
        String descripcion = binding.etDescripcion.getText().toString().trim();  // Obtiene y limpia el texto de descripción del post
        String duracionStr = binding.etDuracion.getText().toString().trim();  // Obtiene y limpia el texto de duración del post
        String presupuestoStr = binding.etPresupuesto.getText().toString().trim();  // Obtiene y limpia el texto del presupuesto

        if (!Validaciones.validarTexto(titulo)) {
            binding.itTitulo.setError("El título no es válido");  // Muestra un error si el título no es válido
            return;
        }

        if (!Validaciones.validarTexto(descripcion)) {
            binding.etDescripcion.setError("La descripción no es válida");  // Muestra un error si la descripción no es válida
            return;
        }

        int duracion = Validaciones.validarNumero(duracionStr);  // Valida y obtiene duración como número entero

        if (duracion == -1) {
            binding.etDuracion.setError("Duración no válida");  // Muestra un error si la duración no es válida
            return;
        }

        double presupuesto;

        try {
            presupuesto = Double.parseDouble(presupuestoStr);  // Intenta convertir presupuesto a double
        } catch (NumberFormatException e) {
            binding.etPresupuesto.setError("Presupuesto no válido");  // Muestra un error si no se puede convertir a número válido
            return;
        }

        Post post = new Post();  // Crea una nueva instancia del modelo Post
        post.setTitulo(titulo);  // Establece el título del post
        post.setDescripcion(descripcion);  // Establece la descripción del post
        post.setDuracion(duracion);  // Establece la duración del post
        post.setCategoria(categoria);  // Establece la categoría seleccionada del post
        post.setPresupuesto(presupuesto);  // Establece el presupuesto asignado al post.

        post.setImagenes(new ArrayList<>(imagenesUrls));  // Establece las URLs de las imágenes seleccionadas.

                postViewModel.publicar(post);   // Llama al método publicar en el ViewModel con el nuevo objeto Post.
    }

    private void updateRecyclerViewVisibility() {
        boolean hasImages = !imagenesUrls.isEmpty();   // Verifica si hay imágenes seleccionadas.
                binding.recyclerView.setVisibility(hasImages ? View.VISIBLE : View.GONE);   // Muestra u oculta el RecyclerView según haya imágenes.
        binding.uploadImage.setVisibility(imagenesUrls.size() < MAX_IMAGE ? View.VISIBLE : View.GONE);   // Muestra u oculta el botón "Subir Imagen" según cuántas imágenes haya.
    }

    @Override   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("PostActivity", "onRequestPermissionsResult ejecutado");   // Registra que se ha ejecutado este método.

        if (requestCode == REQUEST_IMAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("PostActivity", "Permiso concedido, abriendo galería");   // Registra que se ha concedido permiso.
            ImageUtils.openGallery(PostActivity.this, galleryLauncher);   // Abre la galería utilizando el launcher configurado.
        } else {
            Log.d("PostActivity", "Permiso denegado");   // Registra que se ha denegado permiso.
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();   // Muestra un mensaje indicando que se ha denegado permiso.
        }
    }
}
