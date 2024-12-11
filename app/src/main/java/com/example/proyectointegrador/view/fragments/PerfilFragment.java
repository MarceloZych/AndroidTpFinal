package com.example.proyectointegrador.view.fragments; // Paquete donde se encuentra la clase

import android.Manifest; // Importa Manifest para manejar permisos
import android.content.Intent; // Importa Intent para iniciar nuevas actividades
import android.graphics.Bitmap; // Importa Bitmap para manejar imágenes
import android.net.Uri; // Importa Uri para manejar URIs (identificadores de recursos)
import android.os.Build; // Importa Build para verificar la versión del sistema operativo
import android.os.Bundle; // Importa Bundle para manejar el estado del fragmento
import android.provider.MediaStore; // Importa MediaStore para acceder a contenido multimedia
import android.util.Log; // Importa Log para registrar mensajes en el Logcat
import android.view.LayoutInflater; // Importa LayoutInflater para inflar layouts
import android.view.Menu; // Importa Menu para manejar menús
import android.view.MenuInflater; // Importa MenuInflater para inflar menús
import android.view.MenuItem; // Importa MenuItem para manejar elementos del menú
import android.view.View; // Importa View para manejar vistas
import android.view.ViewGroup; // Importa ViewGroup para manejar grupos de vistas
import android.widget.Toast; // Importa Toast para mostrar mensajes breves al usuario

import androidx.activity.result.ActivityResultLauncher; // Importa ActivityResultLauncher para manejar resultados de actividades
import androidx.activity.result.contract.ActivityResultContracts; // Importa contratos de resultados de actividades
import androidx.annotation.NonNull; // Importa la anotación para indicar que un parámetro no puede ser nulo
import androidx.appcompat.app.AppCompatActivity; // Importa AppCompatActivity para utilizar características modernas de Android
import androidx.core.view.MenuProvider; // Importa MenuProvider para manejar menús en el fragmento
import androidx.fragment.app.Fragment; // Importa Fragment para crear un fragmento
import androidx.lifecycle.Lifecycle; // Importa Lifecycle para gestionar el ciclo de vida del fragmento

import com.example.proyectointegrador.R; // Importa los recursos del proyecto
import com.example.proyectointegrador.databinding.FragmentPerfilBinding; // Importa el binding generado para FragmentPerfil
import com.example.proyectointegrador.util.ImageUtils; // Importa la clase utilitaria ImageUtils
import com.parse.ParseUser; // Importa ParseUser para trabajar con usuarios de Parse
import com.squareup.picasso.Picasso; // Importa Picasso, una biblioteca para cargar imágenes

import java.io.IOException; // Importa IOException para manejar excepciones de entrada/salida

// Clase que representa un fragmento donde se muestra y edita el perfil del usuario
public class PerfilFragment extends Fragment {
    private FragmentPerfilBinding binding; // Variable para el binding del fragmento
    private ActivityResultLauncher<Intent> galleryLauncher; // Launcher para abrir la galería

    // Constructor vacío por defecto
    public PerfilFragment() {
    }

    // Método llamado al crear la vista del fragmento
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstantState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false); // Infla el layout del fragmento utilizando View Binding

        setupMenu(); // Configura el menú del fragmento
        setupToolbar(); // Configura la barra de herramientas (toolbar)
        displayUserInfo(); // Muestra la información del usuario actual en la interfaz
        setupGalleryLauncher(); // Configura el launcher para abrir la galería de imágenes
        setupProfileImageClick(); // Configura el evento de clic en la imagen de perfil

        return binding.getRoot(); // Devuelve la vista raíz del binding
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.close_menu, menu); // Infla el menú desde los recursos (close_menu.xml)
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.itemClose) {
                    Toast.makeText(getContext(), "Perfil", Toast.LENGTH_SHORT).show();  // Muestra un mensaje cuando se selecciona "Cerrar"
                    return false;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);  // Asocia el menú al ciclo de vida del fragmento
    }

    private void setupToolbar() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.getRoot().findViewById(R.id.toolbar_filtro));
        // Configura la barra de herramientas utilizando el layout inflado y lo establece como soporte en la actividad actual.
    }

    private void displayUserInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();  // Obtiene el usuario actual desde Parse

        if (currentUser != null) {  // Verifica si hay un usuario logueado
            binding.nameUser.setText(currentUser.getUsername());  // Muestra el nombre de usuario en la interfaz
            binding.emailUser.setText(currentUser.getEmail());  // Muestra el correo electrónico en la interfaz
            binding.insta.setText(currentUser.getString("instagram"));  // Muestra el Instagram del usuario

            String fotoUrl = currentUser.getString("foto_perfil");  // Obtiene la URL de la foto de perfil

            if (fotoUrl != null) {  // Si hay una URL válida, carga la imagen con Picasso
                Picasso.get()
                        .load(fotoUrl)  // Carga la imagen desde la URL proporcionada
                        .placeholder(R.drawable.ic_person)  // Muestra un ícono por defecto mientras se carga la imagen.
                        .error(R.drawable.ic_person)  // Muestra un ícono por defecto si hay un error al cargar.
                        .into(binding.circleImageView);  // Establece la imagen en el ImageView correspondiente.
            } else {
                binding.circleImageView.setImageResource(R.drawable.ic_person);  // Si no hay URL, muestra un ícono por defecto.
            }
        } else {
            Toast.makeText(getContext(), "Usuario no logueado", Toast.LENGTH_SHORT).show();  // Notifica si no hay usuario logueado.
        }
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();  // Obtiene el URI de la imagen seleccionada desde los resultados.
                        if (imageUri != null) {
                            handleImageSelection(imageUri);  // Llama al método que maneja la selección de imagen.
                        }
                    }
                }
        );
    }

    private void setupProfileImageClick() {
        binding.circleImageView.setOnClickListener(v -> {  // Configura un listener de clic en la imagen de perfil.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ImageUtils.pedirPermisos(requireActivity(),
                        new String[] {
                                Manifest.permission.READ_EXTERNAL_STORAGE,  // Solicita permiso para leer almacenamiento externo.
                        }, 1);
            }
            ImageUtils.openGallery(requireActivity(), galleryLauncher);  // Abre la galería utilizando el launcher configurado.
        });
    }

    private void handleImageSelection(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            binding.circleImageView.setImageBitmap(bitmap);  // Establece la imagen seleccionada en el ImageView.

            ImageUtils.subirImagenParse(requireContext(), imageUri, new ImageUtils.ImageUploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null) {
                        currentUser.put("foto_perfil", imageUrl);  // Actualiza el campo foto_perfil del usuario actual con la nueva URL.
                        currentUser.saveInBackground(e -> {
                            if (e == null) {
                                Toast.makeText(requireContext(), "Imagen de perfil actualizada", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Error al actualizar la imagen de perfil", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(requireContext(), "Error al subir la foto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            Log.e("PerfilFragment", "Error al obtener la imagen" + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error al obtener la imagen", Toast.LENGTH_SHORT).show();
        }
    }
}