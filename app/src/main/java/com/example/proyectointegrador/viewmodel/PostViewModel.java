package com.example.proyectointegrador.viewmodel;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.proyectointegrador.model.Post;
import com.example.proyectointegrador.model.User;
import com.example.proyectointegrador.providers.AuthProvider;
import com.example.proyectointegrador.providers.PostProvider;

import java.util.List;

public class PostViewModel extends ViewModel {

    private final MutableLiveData<Boolean> postSuccess = new MutableLiveData<>();
    private final PostProvider postProvider;
    private LiveData<List<Post>> posts;

    public LiveData<Boolean> getPostSuccess() { return postSuccess; }

    public PostViewModel() {
        posts = new MutableLiveData<>();
        postProvider = new PostProvider();
    }

    public void publicar(Post post) {
        postProvider.addPost(post)
                .observeForever(result -> {
                    if ("Post publicado".equals(result)) {
                        postSuccess.setValue(true);
                    } else {
                        postSuccess.setValue(false);
                    }
                });
    }

    public LiveData<List<Post>> getPosts() {
        posts = postProvider.getPostsByCurrentUser();
        Log.d("PostViewModel", "Posts: ");
        return posts;
    }




/*    private final AuthProvider authProvider;
    private final PostProvider userProvider;
    private final MutableLiveData<User> currentUser;
    private final MutableLiveData<String> estado;

    public PostViewModel() {
        authProvider = new AuthProvider();
        userProvider = new PostProvider();
        currentUser = new MutableLiveData<>();
        estado = new MutableLiveData<>();;
    }

    public LiveData<User> getCurrentUser() { return currentUser; }
    public LiveData<String> getOperationStatus() { return estado; }

    public void createUser(User user, LifecycleOwner lifecycleOwner) {
        authProvider.signUp(user.getEmail(), user.getPassword()).observe(lifecycleOwner, uid -> {
            if (uid != null) {
                user.setId(uid);
                userProvider.createUser(user).observe(lifecycleOwner, status -> {
                    if (status != null) {
                        estado.setValue(status);
                    } else {
                        estado.setValue("Error al crear el usuario");
                    }
                });
            } else {
                estado.setValue("Error al registrar el usuario");
            }
        });
    }

    public void updateUser(User user, LifecycleOwner lifecycleOwner) {
        LiveData<String> result = userProvider.updateUser(user);
        result.observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                estado.setValue(result.getValue());
            }
        });
    }

    public void deleteUser(String id, LifecycleOwner lifecycleOwner) {
        LiveData<String> result = userProvider.deleteUser(id);
        result.observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                estado.setValue(s);
            }
        });
    }

    public void getUser(String email, LifecycleOwner lifecycleOwner) {
        LiveData<User> user = userProvider.getUser(email);
        user.observe(lifecycleOwner, new Observer<User>() {
            @Override
            public void onChanged(User foundUser) {
                if (foundUser != null) {
                    Log.d("User info", "ID: " + foundUser.getId() + ", Usuario: " + foundUser.getUsername());
                    currentUser.setValue(foundUser);
                } else {
                    estado.setValue("Usuario no encontrado");
                }
            }
        });
    }*/
}
