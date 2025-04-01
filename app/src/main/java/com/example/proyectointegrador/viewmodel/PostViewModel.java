package com.example.proyectointegrador.viewmodel; // Paquete donde se encuentra la clase

import android.util.Log; // Importa Log para registrar mensajes en el Logcat

import androidx.lifecycle.LiveData; // Importa LiveData para observar datos que pueden cambiar
import androidx.lifecycle.MutableLiveData; // Importa MutableLiveData para datos que pueden ser modificados
import androidx.lifecycle.ViewModel; // Importa ViewModel para crear un modelo de datos que sobreviva a cambios de configuración

import com.example.proyectointegrador.model.Post; // Importa el modelo Post
import com.example.proyectointegrador.providers.PostProvider; // Importa PostProvider para manejar operaciones relacionadas con los posts
import com.parse.ParseObject; // Importa ParseObject para trabajar con objetos en Parse

import java.util.List; // Importa List para trabajar con colecciones

// Clase que representa el ViewModel para manejar operaciones relacionadas con los posts
public class PostViewModel extends ViewModel {

    private final MutableLiveData<String> postSuccess = new MutableLiveData<>(); // LiveData que indica si la publicación fue exitosa
    private final PostProvider postProvider; // Proveedor que maneja operaciones relacionadas con los posts
    private LiveData<List<Post>> posts; // LiveData que contiene una lista de posts
    private final MutableLiveData<List<ParseObject>> commentsLiveData = new MutableLiveData<>(); // LiveData que contiene una lista de comentarios
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(); // LiveData que contiene mensajes de error

    // Método público que devuelve el LiveData de éxito al publicar un post
    public LiveData<String> getPostSuccess() {
        return postSuccess;
    }

    // Constructor del ViewModel
    public PostViewModel() {
        posts = new MutableLiveData<>(); // Inicializa el LiveData para la lista de posts
        postProvider = new PostProvider(); // Crea una nueva instancia del PostProvider
    }

    public LiveData<List<Post>> getPostsByCurrentUser() {
        return postProvider.getPostsByCurrentUser();
    }

    // Método público para publicar un nuevo post
    public void publicar(Post post) {
        postProvider.addPost(post) // Llama al método addPost en el PostProvider para agregar el post
                .observeForever(result -> { // Observa el resultado de la operación de publicación
                    postSuccess.setValue(result); // Establece el resultado en postSuccess, notificando a los observadores
                });
    }

    // Método público para obtener todos los posts disponibles
    public LiveData<List<Post>> getPosts() {
        posts = postProvider.getAllPosts(); // Llama al método getAllPosts en el PostProvider y asigna el resultado a posts
        return posts; // Devuelve la lista de posts observables
    }
}
