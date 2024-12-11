package com.example.proyectointegrador.viewmodel; // Paquete donde se encuentra la clase

import android.util.Log; // Importa Log para registrar mensajes en el Logcat

import androidx.lifecycle.LiveData; // Importa LiveData para observar datos que pueden cambiar
import androidx.lifecycle.MutableLiveData; // Importa MutableLiveData para datos que pueden ser modificados
import androidx.lifecycle.Observer; // Importa Observer para observar cambios en LiveData
import androidx.lifecycle.ViewModel; // Importa ViewModel para crear un modelo de datos que sobreviva a cambios de configuración

import com.example.proyectointegrador.model.User; // Importa el modelo User
import com.example.proyectointegrador.providers.AuthProvider; // Importa AuthProvider para manejar la autenticación

// Clase que representa el ViewModel para el registro de usuarios
public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<String> registerResult = new MutableLiveData<>(); // LiveData que contiene el resultado del registro (ID del usuario o null)
    private final AuthProvider authProvider; // Proveedor que maneja operaciones de autenticación

    // Constructor del ViewModel
    public RegisterViewModel() {
        this.authProvider = new AuthProvider(); // Inicializa el AuthProvider
    }

    // Método público que devuelve el LiveData del resultado del registro
    public LiveData<String> getRegisterResult() {
        return registerResult; // Devuelve el LiveData que contiene el resultado del registro
    }

    // Método público para registrar un nuevo usuario
    public void register(User user) {
        LiveData<String> result = authProvider.signUp(user); // Llama al método signUp en AuthProvider y obtiene el resultado como LiveData

        result.observeForever(new Observer<String>() { // Observa los cambios en el resultado del registro
            @Override
            public void onChanged(String objectId) { // Método llamado cuando hay un cambio en el resultado observado
                if (objectId != null) {
                    registerResult.setValue(objectId); // Si el registro fue exitoso, establece el ID del usuario en registerResult
                } else {
                    registerResult.setValue(null); // Si hubo un error, establece registerResult como null
                    Log.e("RegisterViewModel", "Error al registrar el usuario"); // Registra un error en caso de fallo en el registro
                }
                result.removeObserver(this); // Elimina el observador después de recibir la primera actualización
            }
        });
    }
}
