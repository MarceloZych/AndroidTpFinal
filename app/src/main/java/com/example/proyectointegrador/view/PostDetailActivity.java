package com.example.proyectointegrador.view; // Paquete donde se encuentra la clase

import android.os.Bundle; // Importa Bundle para manejar el estado de la actividad
import android.view.View;
import android.widget.EditText; // Importa EditText para manejar campos de texto
import android.widget.LinearLayout; // Importa LinearLayout para crear un layout vertical
import android.widget.RelativeLayout; // Importa RelativeLayout para crear un layout relativo
import android.widget.Toast; // Importa Toast para mostrar mensajes breves al usuario
import com.example.proyectointegrador.model.User;

import androidx.appcompat.app.AlertDialog; // Importa AlertDialog para mostrar diálogos de alerta
import androidx.appcompat.app.AppCompatActivity; // Importa AppCompatActivity para utilizar características modernas de Android
import androidx.lifecycle.ViewModelProvider; // Importa ViewModelProvider para obtener instancias de ViewModels
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyectointegrador.R; // Importa los recursos del proyecto
import com.example.proyectointegrador.adapters.ComentarioAdapter;
import com.example.proyectointegrador.adapters.ImageSliderAdapter; // Importa el adaptador personalizado para el slider de imágenes
import com.example.proyectointegrador.databinding.ActivityPostDetailBinding; // Importa el binding generado para ActivityPostDetail
import com.example.proyectointegrador.util.ImageUtils;
import com.example.proyectointegrador.viewmodel.PostDetailViewModel; // Importa el ViewModel asociado a los detalles del post
import com.google.android.material.tabs.TabLayoutMediator; // Importa TabLayoutMediator para conectar TabLayout y ViewPager2
import com.parse.ParseUser;
import com.squareup.picasso.Picasso; // Importa Picasso, una biblioteca para cargar imágenes

import java.util.ArrayList; // Importa ArrayList para manejar listas dinámicas

// Clase que representa la actividad donde se muestran los detalles de un post específico
public class PostDetailActivity extends AppCompatActivity {
    private ActivityPostDetailBinding binding; // Variable para el binding de la actividad
    private PostDetailViewModel viewModel; // Variable para el ViewModel asociado a esta actividad
    private ComentarioAdapter comentarioAdapter;
    private String postId; // Variable que almacenará el ID del post

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Llama al método onCreate de la clase padre

        viewModel = new ViewModelProvider(this).get(PostDetailViewModel.class); // Obtiene una instancia del ViewModel

        binding = ActivityPostDetailBinding.inflate(getLayoutInflater()); // Infla el layout utilizando View Binding
        setContentView(binding.getRoot()); // Establece el contenido de la actividad a la vista inflada

        viewModel = new ViewModelProvider(this).get(PostDetailViewModel.class);
        postId = getIntent().getStringExtra("postId");

        binding.rvComentarios.setLayoutManager(new LinearLayoutManager(this));
        comentarioAdapter = new ComentarioAdapter(new ArrayList<>());
        binding.rvComentarios.setAdapter(comentarioAdapter);

        if (postId != null) {
            viewModel.fetchComments(postId);
            viewModel.getPostDetail(postId);
        }

        viewModel.getCommentsLiveData().observe(this, comentarios -> {
            comentarioAdapter.setComentarios(comentarios);
            comentarioAdapter.notifyDataSetChanged();
        });

        String currentUser = ParseUser.getCurrentUser().getUsername();
        String perfilUserId = getIntent().getStringExtra("username");


        if (postId != null && currentUser.equals(perfilUserId)) {
            binding.btnEliminarPost.setVisibility(View.VISIBLE);
            binding.btnEliminarPost.setOnClickListener(v -> confirmaBorrar());
        } else {
            binding.btnEliminarPost.setVisibility(View.GONE);
        }

        setupDeleteButtonVisibility();
        detailInfo(); // Muestra la información del post en la interfaz
        setupObservers(); // Configura los observadores del ViewModel

        binding.fabComentar.setOnClickListener(v -> showDialogComment());  // Configura el evento al hacer clic en el botón flotante (FAB) para comentar.
    }

        private void setupDeleteButtonVisibility() {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser == null) {
                binding.btnEliminarPost.setVisibility(View.GONE);
                return;
            }

            viewModel.getPostLiveData().observe(this, post -> {
                if (post != null && postId != null) {
                    User postOwner = post.getUser();
                    if (postOwner != null && currentUser.getUsername().equals(postOwner.getUsername())) {
                        binding.btnEliminarPost.setVisibility(View.VISIBLE);
                        binding.btnEliminarPost.setOnClickListener(v -> confirmaBorrar());
                    } else {
                        binding.btnEliminarPost.setVisibility(View.GONE);
                    }
                }
            });
        }

    private void confirmaBorrar() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmación");
        alert.setMessage("¿Estás seguro de que deseas eliminar este post?");

        alert.setPositiveButton("Eliminar", (dialog, which) -> viewModel.eliminarPost(postId));
        alert.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        alert.show();
    }

    // Método privado que muestra un diálogo para agregar un comentario al post.
    private void showDialogComment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);  // Crea un builder para un AlertDialog.
        alert.setTitle("¡COMENTARIO!");  // Establece el título del diálogo.
        alert.setMessage("Escribe tu comentario: ");  // Establece el mensaje del diálogo.

        EditText editText = new EditText(PostDetailActivity.this);  // Crea un nuevo EditText para ingresar el comentario.
        editText.setHint("Texto");  // Establece un hint en el campo de texto.

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );  // Crea parámetros de layout que permiten que el EditText ocupe todo el espacio disponible.

        editText.setLayoutParams(params);  // Aplica los parámetros al EditText.
        params.setMargins(36, 0, 36, 36);  // Establece márgenes alrededor del EditText.

        RelativeLayout container = new RelativeLayout(PostDetailActivity.this);  // Crea un contenedor relativo para el EditText.
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );  // Crea parámetros relativos que permiten que el contenedor ocupe todo el ancho y ajuste su alto al contenido.

        container.setLayoutParams(relativeParams);  // Aplica los parámetros al contenedor.
        container.addView(editText);  // Agrega el EditText al contenedor relativo.

        alert.setView(container);  // Establece el contenedor como vista del diálogo.

        alert.setPositiveButton("Ok", (dialog, which) -> {
            String value = editText.getText().toString().trim();  // Obtiene y limpia el texto ingresado por el usuario.
            if (!value.isEmpty()) {
                viewModel.saveComment(postId, value);  // Si hay texto, llama al método saveComment en el ViewModel con el ID del post y el comentario.
            } else {
                Toast.makeText(PostDetailActivity.this, "Debes escribir un comentario", Toast.LENGTH_SHORT).show();  // Muestra un mensaje si no hay texto ingresado.
            }
        });

        alert.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();  // Cierra el diálogo si se presiona "Cancelar".
        });

        alert.show();  // Muestra el diálogo al usuario.
    }

    private void setupObservers() {
        viewModel.getCommentsLiveData().observe(this, comentarios -> {
            if (comentarios != null) {
                comentarioAdapter.setComentarios(comentarios);
                comentarioAdapter.notifyDataSetChanged();
            }
        });

        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "ERROR: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void detailInfo() {
        binding.nameUser.setText(getIntent().getStringExtra("name"));  // Muestra el nombre del usuario que creó el post.
        binding.emailUser.setText(getIntent().getStringExtra("email"));  // Muestra el correo electrónico del usuario.
        binding.insta.setText(getIntent().getStringExtra("insta"));  // Muestra la cuenta de Instagram del usuario.

        String fotoUrl = getIntent().getStringExtra("foto_perfil");  // Obtiene la URL de la foto de perfil desde los extras del Intent.

        if (fotoUrl != null) {
            Picasso.get()
                    .load(fotoUrl)  // Carga la imagen desde la URL proporcionada.
                    .placeholder(R.drawable.ic_person)  // Muestra un ícono por defecto mientras se carga la imagen.
                    .error(R.drawable.ic_person)  // Muestra un ícono por defecto si hay un error al cargar.
                    .into(binding.circleImageView);  // Establece la imagen en el ImageView correspondiente.
        } else {
            binding.circleImageView.setImageResource(R.drawable.ic_person);  // Si no hay URL, muestra un ícono por defecto.
        }

        ArrayList<String> urls = getIntent().getStringArrayListExtra("imagenes"); // Obtiene las URLs de las imágenes desde los extras del Intent

        String titulo = "Lugar: " + getIntent().getStringExtra("title"); // Crea una cadena con información sobre el lugar.
        binding.lugar.setText(titulo); // Establece la cadena en la vista correspondiente.

        String categoria = "Categoria: " + getIntent().getStringExtra("categoria"); // Crea una cadena con información sobre la categoría.

        binding.categoria.setText(categoria); // Establece la cadena en la vista correspondiente.

        String comentario = "Descripción: " + getIntent().getStringExtra("description"); // Crea una cadena con información sobre la descripción.

        binding.description.setText(comentario); // Establece la cadena en la vista correspondiente.

        String duracion = "Duración: " + getIntent().getIntExtra("duration", 0) + " día/s"; // Crea una cadena con información sobre la duración.

        binding.duracion.setText(duracion); // Establece la cadena en la vista correspondiente.

        String presupuesto = "Presupuesto: U$ " + getIntent().getDoubleExtra("presupuesto", 0.0); // Crea una cadena con información sobre el presupuesto.

        binding.presupuesto.setText(presupuesto); // Establece la cadena en la vista correspondiente.

        if (urls != null && !urls.isEmpty()) { // Verifica si hay URLs válidas y no vacías.
            ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(urls); // Crea una nueva instancia del adaptador con las URLs obtenidas.

            binding.viewPager.setAdapter(imageSliderAdapter); // Establece el adaptador en un ViewPager.

            binding.viewPager.setPageTransformer(new ImageUtils.EfectoTransformer()); // Aplica una transformación visual a las páginas del ViewPager.

            new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            }).attach();
            // Conecta TabLayout con ViewPager2 (actualmente sin configuración adicional).
        }
    }
}
