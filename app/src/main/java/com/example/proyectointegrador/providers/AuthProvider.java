package com.example.proyectointegrador.providers;

import static com.parse.Parse.getApplicationContext;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectointegrador.R;
import com.example.proyectointegrador.model.User;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseUser;

public class AuthProvider {

    public AuthProvider() {
    }

    public AuthProvider(Context context)
    {
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId(context.getString(R.string.back4app_app_id))
                .clientKey(context.getString(R.string.back4app_client_key))
                .server(context.getString(R.string.back4app_server_url))
                .build()
        );
    }
    public LiveData<String> signIn(String email, String password)
    {
        MutableLiveData<String> loginResult = new MutableLiveData<>();

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                if (e == null) {
                    //Login exitoso
                    loginResult.setValue(user.getObjectId());
                    Log.d("AuthProvider", "Login Exitoso: " + user.getObjectId());
                }else {
                    //Error en el login
                    Log.e("AuthProvider", "Error durante login: " + e);
                    loginResult.setValue(null);
                }
            }
        });
        return loginResult;
    }

    public LiveData<String> signUp(User user) {
        MutableLiveData<String> signUpResult = new MutableLiveData<>();

        ParseUser parseUserser = new ParseUser();

        parseUserser.setUsername(user.getUsername());
        parseUserser.setPassword(user.getPassword());
        parseUserser.signUpInBackground(e -> {
            if (e == null) {
                signUpResult.setValue(parseUserser.getObjectId());
                Log.d("AuthProvider", "Registro exitoso"+ parseUserser.getObjectId());
            } else {
                Log.e("AuthProvider", "Error durante el registro: " + e);
                signUpResult.setValue(null);
            }
        });

        return signUpResult;
    }

    public LiveData<String> getCurrentID() {
        MutableLiveData<String> currentUserId = new MutableLiveData<>();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUserId.setValue(currentUser.getObjectId());
            Log.d("AuthProvider", "ID del usuario actual: "+ currentUser.getObjectId());
        }

        return currentUserId;
    }

    public LiveData<Boolean> logout() {
        MutableLiveData<Boolean> logoutResult = new MutableLiveData<>();
        ParseUser.logOutInBackground(e -> {
            if (e == null) {
                logoutResult.setValue(true);
                if (getApplicationContext() != null) {
                    getApplicationContext().getCacheDir().delete();
                }
                Log.d("AuthProvider", "Logout exitoso");
            } else {
                logoutResult.setValue(false);
                Log.e("AuthProvider", "Error durante el logout: " + e);
            }
        });

        return logoutResult;
    }
}
