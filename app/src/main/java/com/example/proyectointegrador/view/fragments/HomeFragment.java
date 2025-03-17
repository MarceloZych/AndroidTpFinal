package com.example.proyectointegrador.view.fragments;

import android.content.Intent; // Importa la clase Intent para iniciar nuevas actividades
import android.os.Bundle; // Importa la clase Bundle para manejar el estado del fragmento

import androidx.annotation.NonNull; // Importa la anotación para indicar que un parámetro no puede ser nulo
import androidx.appcompat.app.AppCompatActivity; // Importa AppCompatActivity para utilizar características modernas de Android
import androidx.core.view.MenuProvider; // Importa MenuProvider para manejar menús en el fragmento
import androidx.fragment.app.Fragment; // Importa Fragment para crear fragmentos
import androidx.lifecycle.Lifecycle; // Importa Lifecycle para gestionar el ciclo de vida del fragmento
import androidx.lifecycle.ViewModelProvider; // Importa ViewModelProvider para obtener instancias de ViewModels
import androidx.recyclerview.widget.LinearLayoutManager; // Importa LinearLayoutManager para gestionar la disposición de elementos en un RecyclerView

import android.util.Log; // Importa Log para registrar mensajes en el Logcat
import android.view.LayoutInflater; // Importa LayoutInflater para inflar layouts
import android.view.Menu; // Importa Menu para manejar menús
import android.view.MenuInflater; // Importa MenuInflater para inflar menús
import android.view.MenuItem; // Importa MenuItem para manejar elementos del menú
import android.view.View; // Importa View para manejar vistas
import android.view.ViewGroup; // Importa ViewGroup para manejar grupos de vistas
import android.widget.Toast; // Importa Toast para mostrar mensajes breves al usuario

import com.example.proyectointegrador.R; // Importa los recursos del proyecto
import com.example.proyectointegrador.adapters.PostAdapter; // Importa el adaptador personalizado para los posts
import com.example.proyectointegrador.databinding.FragmentHomeBinding; // Importa el binding generado para FragmentHome
import com.example.proyectointegrador.view.HomeActivity; // Importa HomeActivity, la actividad que contiene este fragmento
import com.example.proyectointegrador.view.MainActivity; // Importa MainActivity, a la que se redirige al cerrar sesión
import com.example.proyectointegrador.view.PostActivity; // Importa PostActivity, donde se crean nuevos posts
import com.example.proyectointegrador.viewmodel.AuthViewModel; // Importa el ViewModel de autenticación
import com.example.proyectointegrador.viewmodel.PostViewModel; // Importa el ViewModel de posts

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding; // Variable para el binding del fragmento
    private PostViewModel postViewModel; // Variable para el ViewModel de posts
    private AuthViewModel authViewModel; // Variable para el ViewModel de autenticación

    public HomeFragment() {
        // Constructor vacío requerido por el sistema.
    }

    public static HomeFragment newInstance() {
        return new HomeFragment(); // Método estático para crear una nueva instancia del fragmento.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Llama al método onCreate de la clase padre.
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class); // Obtiene una instancia del ViewModel de posts.
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class); // Obtiene una instancia del ViewModel de autenticación.
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false); // Infla el layout utilizando View Binding.
        return binding.getRoot(); // Devuelve la vista raíz del binding.
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState); // Llama al método onViewCreated de la clase padre.

        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.tools); // Configura la barra de herramientas.

        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PostActivity.class); // Crea un Intent para abrir PostActivity.
            startActivity(intent); // Inicia PostActivity.
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Establece el LayoutManager del RecyclerView.

        postViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null && !posts.isEmpty()) {
               // Log.d("HomeFragment", "Número de posts: " + posts.size()); // Registra el número de posts obtenidos.
                PostAdapter adapter = new PostAdapter(posts); // Crea un nuevo adaptador con los posts.
                binding.recyclerView.setAdapter(adapter); // Establece el adaptador en el RecyclerView.
                adapter.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado.
                ((HomeActivity) requireActivity()).hideProgressBar(); // Oculta el ProgressBar en HomeActivity.
            } else {
                Log.d("HomeFragment", "No se encontraron posts");
                ((HomeActivity) requireActivity()).hideProgressBar(); // Oculta el ProgressBar si no hay posts.
            }
        });
        setupMenu(); // Configura el menú del fragmento.
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.home_menu, menu); // Infla el menú desde los recursos.
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.itemLogout) {
                    onLogout();  // Llama al método onLogout si se seleccionó "Cerrar sesión".
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void onLogout() {
        authViewModel.logout().observe(getViewLifecycleOwner(), logoutResult -> {
            if (logoutResult != null && logoutResult) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);  // Inicia MainActivity y limpia la pila de actividades.
            } else {
                Toast.makeText(getContext(), "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                // Muestra un mensaje si hubo un error al cerrar sesión.
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Libera la referencia al binding cuando se destruye la vista.
    }
}