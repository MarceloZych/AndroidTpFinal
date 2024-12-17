package com.example.proyectointegrador.view; // Paquete donde se encuentra la clase

import android.content.Intent; // Importa Intent para iniciar nuevas actividades
import android.os.Bundle; // Importa Bundle para manejar el estado de la actividad
import android.util.Log; // Importa Log para registrar mensajes en el Logcat
import android.widget.Toast; // Importa Toast para mostrar mensajes breves al usuario
import android.view.View; // Importa View para manejar vistas

import androidx.appcompat.app.AppCompatActivity; // Importa AppCompatActivity para utilizar características modernas de Android
import androidx.lifecycle.ViewModelProvider; // Importa ViewModelProvider para obtener instancias de ViewModels

import com.example.proyectointegrador.databinding.ActivityRegisterBinding; // Importa el binding generado para ActivityRegister
import com.example.proyectointegrador.model.User; // Importa el modelo User
import com.example.proyectointegrador.util.Validaciones; // Importa la clase utilitaria Validaciones
import com.example.proyectointegrador.viewmodel.RegisterViewModel; // Importa el ViewModel asociado al registro

// Clase que representa la actividad de registro de nuevos usuarios
public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding; // Variable para el binding de la actividad
    private RegisterViewModel viewModel; // Variable para el ViewModel asociado a esta actividad

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Llama al método onCreate de la clase padre

        binding = ActivityRegisterBinding.inflate(getLayoutInflater()); // Infla el layout utilizando View Binding
        setContentView(binding.getRoot()); // Establece el contenido de la actividad a la vista inflada

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class); // Obtiene una instancia del ViewModel

        viewModel.getRegisterResult().observe(this, this::showToast); // Observa el resultado del registro y muestra un Toast con el mensaje correspondiente

        manejarEventos(); // Configura los eventos de los elementos interactivos en la interfaz
    }

    // Método privado que maneja los eventos de clic en los elementos interactivos
    private void manejarEventos() {
        binding.circuloBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class); // Crea un Intent para volver a MainActivity
            startActivity(intent); // Inicia MainActivity al hacer clic en "Volver"
        });

        binding.btRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarRegistro(); // Llama al método realizarRegistro cuando se hace clic en el botón "Registrar"
            }
        });
    }

    // Método privado que maneja el proceso de registro del usuario
    private void realizarRegistro() {
        String usuario = binding.tietUsuario.getText().toString().trim();  // Obtiene y limpia el texto del campo de usuario
        String email = binding.tietMail.getText().toString().trim();  // Obtiene y limpia el texto del campo de correo electrónico
        String password1 = binding.etPassword1.getText().toString().trim();  // Obtiene y limpia el texto del primer campo de contraseña
        String password2 = binding.etPassword2.getText().toString().trim();  // Obtiene y limpia el texto del segundo campo de contraseña

        /*if (!Validaciones.validarUsuario(usuario)) {  // Valida si el nombre de usuario es correcto
            showToast("Usuario incorrecto");  // Muestra un mensaje si no es válido
            return;
        }

        if (!Validaciones.validarCorreo(email)) {  // Valida si el correo electrónico es correcto
            showToast("Email incorrecto");  // Muestra un mensaje si no es válido
            return;
        }

        String passError = Validaciones.ValidarContrasena(password1, password2);  // Valida las contraseñas ingresadas
        if (passError != null) {
            showToast(passError);  // Muestra un mensaje con el error si hay problemas con las contraseñas
            return;
        }*/

        User user = new User();  // Crea una nueva instancia del modelo User
        user.setEmail(email);  // Establece el correo electrónico como red social del usuario
        user.setUsername(usuario);  // Establece el nombre de usuario
        user.setPassword(password1);  // Establece la contraseña
        Log.d("RegisterActivity", "Correo electrónico ingresado: " + user.getEmail());
        Log.d("RegisterActivity", "Usuario registrado: " + usuario + ", Email: " + email + ", Password: " + password1);
        viewModel.register(user);  // Llama al método register en el ViewModel con la información del nuevo usuario
    }

    // Método privado que muestra un Toast con un mensaje específico
    private void showToast(String message) {
        if (message != null) { // Verifica que el mensaje no sea nulo
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show(); // Muestra un Toast con el mensaje proporcionado
        }
    }
}
