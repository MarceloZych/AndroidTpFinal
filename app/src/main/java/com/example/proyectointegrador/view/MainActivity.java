package com.example.proyectointegrador.view; // Paquete donde se encuentra la clase

import android.content.Intent; // Importa Intent para iniciar nuevas actividades
import android.os.Bundle; // Importa Bundle para manejar el estado de la actividad
import android.view.View; // Importa View para manejar vistas
import android.widget.EditText; // Importa EditText para manejar campos de texto
import android.widget.Toast; // Importa Toast para mostrar mensajes breves al usuario

import androidx.appcompat.app.AppCompatActivity; // Importa AppCompatActivity para utilizar características modernas de Android
import androidx.lifecycle.ViewModelProvider; // Importa ViewModelProvider para obtener instancias de ViewModels

import com.example.proyectointegrador.R; // Importa los recursos del proyecto
import com.example.proyectointegrador.databinding.ActivityMainBinding; // Importa el binding generado para ActivityMain
import com.example.proyectointegrador.providers.AuthProvider; // Importa AuthProvider para manejar autenticación
import com.example.proyectointegrador.util.Validaciones; // Importa la clase utilitaria Validaciones
import com.example.proyectointegrador.viewmodel.MainViewModel; // Importa el ViewModel principal de la actividad
import com.google.android.material.textfield.TextInputEditText; // Importa TextInputEditText para campos de texto con diseño Material

// Clase que representa la actividad principal donde el usuario puede iniciar sesión
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding; // Variable para el binding de la actividad
    private MainViewModel viewModel; // Variable para el ViewModel asociado a esta actividad

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Llama al método onCreate de la clase padre
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // Infla el layout utilizando View Binding
        setContentView(binding.getRoot()); // Establece el contenido de la actividad a la vista inflada
        viewModel = new ViewModelProvider(this).get(MainViewModel.class); // Obtiene una instancia del ViewModel
        manejarEventos(); // Configura los eventos de los elementos de la interfaz
    }

    @Override
    protected void onStart() {
        super.onStart(); // Llama al método onStart de la clase padre

        //Aquí se puede verificar si hay una sesión activa y redirigir al usuario a HomeActivity.
        //sDescomentando este bloque, se puede observar si el usuario está logueado.
        if (viewModel != null) {
            viewModel.verificarSesionActiva().observe(this, si -> {
                if (Boolean.TRUE.equals(si)) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
    }

    // Método privado que maneja los eventos de clic en los botones y otros elementos interactivos
    private void manejarEventos() {
        binding.tvRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); // Inicia RegisterActivity cuando se hace clic en "Registrar"
        });

        binding.btLogin.setOnClickListener(v -> {
            String email = obtenerTextoSeguro(binding.tietUsuario); // Obtiene el texto del campo de usuario
            String password = obtenerTextoSeguro(binding.etPassword); // Obtiene el texto del campo de contraseña

            if (!Validaciones.validarCorreo(email)) {
                showToast("Email incorrecto"); // Muestra un mensaje si el correo no es válido
                return;
            }

            if (!Validaciones.controlarPassword(password)) {
                showToast("Contraseña incorrecta"); // Muestra un mensaje si la contraseña no es válida
                return;
            }

            // Observa el resultado del inicio de sesión
            viewModel.login(email, password).observe(this, user_id -> {
                if (user_id != null) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent); // Inicia HomeActivity si el inicio de sesión es exitoso
                } else {
                    showToast("Login fallido"); // Muestra un mensaje si el inicio de sesión falla
                }
            });
        });
    }

    // Método privado que muestra un Toast con un mensaje específico
    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume(); // Llama al método onResume de la clase padre
        limpiarCampos(); // Limpia los campos de texto cuando se reanuda la actividad
    }

    // Método privado que limpia los campos de texto en la interfaz
    private void limpiarCampos() {
        if (binding != null) {
            binding.tietUsuario.setText("");  // Limpia el campo del nombre de usuario
            binding.etPassword.setText("");  // Limpia el campo de la contraseña
        }
    }

    // Método privado que obtiene texto seguro desde un EditText, evitando nulos y espacios en blanco
    private String obtenerTextoSeguro(EditText eText) {
        if (eText == null) return "";  // Retorna una cadena vacía si el EditText es nulo

        return eText.getText().toString().trim();  // Devuelve el texto del EditText sin espacios en blanco al principio o al final
    }
}
