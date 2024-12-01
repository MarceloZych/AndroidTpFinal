package com.example.proyectointegrador.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.proyectointegrador.providers.AuthProvider;

public class RegisterViewModel extends ViewModel {
/*    private final AuthProvider authProvider;
    private final UserProvider userProvider;

    public RegisterViewModel() {
        authProvider = new AuthProvider();
        userProvider = new UserProvider();
    }

    public LiveData<String> register(User user) {
        MutableLiveData <String> registerResult = new MutableLiveData<>();

        authProvider.signUp(user.getEmail(), user.getPassword()).observeForever(new Observer<String>() {
            @Override
            public void onChanged(String userId) {
                if (userId != null){
                    user.setId(userId);
                    userProvider.createUser(user).observeForever(new Observer<String>() {
                        @Override
                        public void onChanged(String result) {
                            registerResult.setValue(result);
                        }
                    });
                } else {
                    registerResult.setValue("Error al registrar usuario");
                }
            }
        });
        return registerResult;
    }*/
}
