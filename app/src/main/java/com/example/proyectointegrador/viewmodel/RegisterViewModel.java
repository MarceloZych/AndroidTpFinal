package com.example.proyectointegrador.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectointegrador.model.User;
import com.parse.ParseUser;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<String> registerResult = new MutableLiveData<>();

    public LiveData<String> getRegisterResult() {
        return registerResult;
    }
    public void register(User user) {
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(user.getUsername());
        parseUser.setPassword(user.getPassword());
        parseUser.setEmail(user.getEmail());
        // Registra el usuario en Parse
        parseUser.signUpInBackground(e -> {
            if (e == null) {
                Log.d("Registro", "Usuario registrado correctamente."+e);
                registerResult.setValue("Registro exitoso");
            } else {
                registerResult.setValue("Error: " + e.getMessage());
            }
        });
    }
}

