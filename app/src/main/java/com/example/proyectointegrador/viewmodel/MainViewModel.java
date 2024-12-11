package com.example.proyectointegrador.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectointegrador.providers.AuthProvider;

public class MainViewModel extends ViewModel {
    public final AuthProvider authProvider;

    public MainViewModel() {
        authProvider = new AuthProvider();
    }

    public LiveData<String> login(String email, String password) {
        MutableLiveData<String> loginResult = new MutableLiveData<>();
        authProvider.signIn(email, password).observeForever(user_id -> {
            loginResult.setValue(user_id);
        });
        return loginResult;
    }
}
