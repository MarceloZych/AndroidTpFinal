package com.example.proyectointegrador.viewmodel; // Paquete donde se encuentra la clase

import androidx.lifecycle.LiveData; // Importa LiveData para observar datos que pueden cambiar
import androidx.lifecycle.MutableLiveData; // Importa MutableLiveData para datos que pueden ser modificados
import androidx.lifecycle.ViewModel; // Importa ViewModel para crear un modelo de datos que sobreviva a cambios de configuración

import com.parse.ParseUser; // Importa ParseUser para trabajar con usuarios de Parse
import com.example.proyectointegrador.providers.PostProvider; // Importa PostProvider para manejar operaciones relacionadas con los posts
import com.parse.ParseObject; // Importa ParseObject para trabajar con objetos de Parse

import java.util.List; // Importa List para trabajar con colecciones

// Clase que representa el ViewModel para los detalles de un post, incluyendo comentarios
public class PostDetailViewModel extends ViewModel {
    private final MutableLiveData<List<ParseObject>> commentsLiveData = new MutableLiveData<>(); // LiveData que contiene una lista de comentarios
    private final MutableLiveData<String> errorLivedData = new MutableLiveData<>(); // LiveData que contiene mensajes de error
    private final PostProvider postProvider; // Proveedor que maneja operaciones relacionadas con los posts

    // Constructor privado para inicializar el ViewModel y su proveedor
    public PostDetailViewModel() {
        postProvider = new PostProvider(); // Crea una nueva instancia del PostProvider
    }

    // Método público que devuelve el LiveData de comentarios
    public LiveData<List<ParseObject>> getCommentsLiveData() {
        return commentsLiveData; // Devuelve la lista de comentarios observables
    }

    // Método público que devuelve el LiveData de errores
    public LiveData<String> getErrorLiveData() {
        return errorLivedData; // Devuelve los mensajes de error observables
    }

    // Método público para obtener los comentarios de un post específico
    public void fetchComments(String postId) {
        postProvider.fetchComments(postId, new PostProvider.CommentsCallback() { // Llama al método fetchComments en el PostProvider
            @Override
            public void onSuccess(List<ParseObject> comments) {
                commentsLiveData.setValue(comments); // Si la operación es exitosa, establece la lista de comentarios en LiveData
            }

            @Override
            public void onFailure(Exception e) {
                errorLivedData.setValue(e.getMessage()); // Si hay un error, establece el mensaje de error en LiveData
            }
        });
    }

    // Método público para guardar un nuevo comentario en un post específico
    public void saveComment(String postId, String commentText) {
        ParseUser currentUser = ParseUser.getCurrentUser(); // Obtiene el usuario actual logueado

        postProvider.saveComment(postId, commentText, currentUser, e -> { // Llama al método saveComment en el PostProvider
            if (e == null) { // Si no hay error al guardar el comentario
                fetchComments(postId); // Vuelve a obtener los comentarios del post después de agregar uno nuevo
            } else { // Si hay un error al guardar el comentario
                errorLivedData.postValue(e.getMessage()); // Establece el mensaje de error en LiveData
            }
        });
    }
}
