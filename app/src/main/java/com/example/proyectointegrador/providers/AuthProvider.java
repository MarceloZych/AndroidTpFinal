package com.example.proyectointegrador.providers; // Paquete donde se encuentra la clase

import static com.parse.Parse.getApplicationContext; // Importa el contexto de la aplicación

import android.content.Context; // Importa Context para acceder a recursos y servicios de la aplicación
import android.util.Log; // Importa Log para registrar mensajes en el Logcat

import androidx.lifecycle.LiveData; // Importa LiveData para observar datos que pueden cambiar
import androidx.lifecycle.MutableLiveData; // Importa MutableLiveData para datos que pueden ser modificados

import com.example.proyectointegrador.R; // Importa los recursos del proyecto
import com.example.proyectointegrador.model.User; // Importa el modelo User
import com.parse.LogInCallback; // Importa LogInCallback para manejar resultados de inicio de sesión
import com.parse.Parse; // Importa Parse para inicializar y configurar Parse
import com.parse.ParseUser; // Importa ParseUser para trabajar con usuarios de Parse

// Clase que proporciona métodos para manejar la autenticación de usuarios
public class AuthProvider {

    // Constructor por defecto
    public AuthProvider() {
    }

    /*
    // Constructor que inicializa Parse con la configuración necesaria
    public AuthProvider(Context context) {
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId(context.getString(R.string.back4app_app_id)) // ID de la aplicación
                .clientKey(context.getString(R.string.back4app_client_key)) // Clave del cliente
                .server(context.getString(R.string.back4app_server_url)) // URL del servidor
                .build()
        );
    }
    */

    // Método para iniciar sesión con email y contraseña
    public LiveData<String> signIn(String email, String password) {
        MutableLiveData<String> loginResult = new MutableLiveData<>(); // Crea un MutableLiveData para almacenar el resultado del inicio de sesión

        // Intenta iniciar sesión en segundo plano
        ParseUser.logInInBackground(email, password, (user, e) -> {
            if (e == null) { // Si no hay error en el inicio de sesión
                loginResult.setValue(user.getObjectId()); // Establece el ID del usuario como resultado
                Log.d("AuthProvider", "Login Exitoso: " + user.getObjectId()); // Registra el éxito del inicio de sesión
            } else { // Si hay un error durante el inicio de sesión
                Log.e("AuthProvider", "Error durante login: " + e); // Registra el error
                loginResult.setValue(null); // Establece el resultado como nulo
            }
        });
        return loginResult; // Devuelve el resultado del inicio de sesión
    }

    // Método para registrar un nuevo usuario
    public LiveData<String> signUp(User user) {
        MutableLiveData<String> signUpResult = new MutableLiveData<>(); // Crea un MutableLiveData para almacenar el resultado del registro

        // Verifica si alguno de los valores requeridos es nulo
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            Log.e("AuthProvider", "Uno o más valores son nulos: " +
                    "Username=" + user.getUsername() + " , " +
                    "Password=" + user.getPassword() + " , " +
                    "Email=" + user.getEmail() + " , "
            );
            signUpResult.setValue(null); // Establece el resultado como nulo si hay valores nulos
            return signUpResult; // Devuelve inmediatamente el resultado nulo
        }

        ParseUser parseUser = new ParseUser(); // Crea una nueva instancia de ParseUser

        // Establece los atributos del usuario, usando valores por defecto si son nulos
        parseUser.setUsername(user.getUsername() != null ? user.getUsername() : "defaultUsername");
        parseUser.setPassword(user.getPassword() != null ? user.getPassword() : "defaultPassword");
        parseUser.setEmail(user.getEmail() != null ? user.getEmail() : "default@example.com");

        parseUser.signUpInBackground(e -> {  // Intenta registrar al usuario en segundo plano
            if (e == null) {  // Si no hay error en el registro
                signUpResult.setValue(parseUser.getObjectId());  // Establece el ID del nuevo usuario como resultado
                Log.d("AuthProvider", "Registro exitoso: " + parseUser.getObjectId());  // Registra el éxito del registro
            } else {  // Si hay un error durante el registro
                Log.e("AuthProvider", "Error durante el registro: ", e);  // Registra el error
                signUpResult.setValue(null);  // Establece el resultado como nulo
            }
        });

        return signUpResult;  // Devuelve el resultado del registro (puede ser nulo si hubo un error)
    }

    // Método para cerrar sesión del usuario actual
    public LiveData<Boolean> logout() {
        MutableLiveData<Boolean> logoutResult = new MutableLiveData<>();  // Crea un MutableLiveData para almacenar el resultado del cierre de sesión

        ParseUser.logOutInBackground(e -> {  // Intenta cerrar sesión en segundo plano
            if (e == null) {  // Si no hay error durante el cierre de sesión
                logoutResult.setValue(true);  // Establece el resultado como verdadero (cierre exitoso)
                Log.d("AuthProvider", "Logout exitoso con caché eliminada y usuario desconectado.");  // Registra el éxito del cierre de sesión
            } else {  // Si hay un error durante el cierre de sesión
                logoutResult.setValue(false);  // Establece el resultado como falso (cierre fallido)
                Log.e("AuthProvider", "Error durante el logout: " + e);  // Registra el error
            }
        });

        return logoutResult;  // Devuelve el resultado del cierre de sesión (true o false)
    }
}
