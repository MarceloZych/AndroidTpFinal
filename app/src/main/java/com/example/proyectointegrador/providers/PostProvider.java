package com.example.proyectointegrador.providers; // Paquete donde se encuentra la clase

import android.util.Log; // Importa Log para registrar mensajes en el Logcat
import androidx.lifecycle.LiveData; // Importa LiveData para observar datos que pueden cambiar
import androidx.lifecycle.MutableLiveData; // Importa MutableLiveData para datos que pueden ser modificados
import com.example.proyectointegrador.model.Post; // Importa el modelo Post
import com.example.proyectointegrador.model.User; // Importa el modelo User
import com.parse.ParseException; // Importa ParseException para manejar errores de Parse
import com.parse.ParseObject; // Importa ParseObject para trabajar con objetos de Parse
import com.parse.ParseQuery; // Importa ParseQuery para realizar consultas a Parse
import com.parse.ParseRelation; // Importa ParseRelation para manejar relaciones entre objetos
import com.parse.ParseUser; // Importa ParseUser para trabajar con usuarios de Parse
import com.parse.SaveCallback; // Importa SaveCallback para manejar resultados de guardado
import java.util.ArrayList; // Importa ArrayList para manejar listas dinámicas
import java.util.List; // Importa List para trabajar con colecciones

// Clase que proporciona métodos para interactuar con las publicaciones en el backend
public class PostProvider {

    // Método para agregar una nueva publicación
    public LiveData<String> addPost(Post post) {
        MutableLiveData<String> postResult = new MutableLiveData<>(); // Crea un MutableLiveData para almacenar el resultado del post

        // Establece los atributos del post en Parse
        post.put("titulo", post.getTitulo());
        post.put("descripcion", post.getDescripcion());
        post.put("duracion", post.getDuracion());
        post.put("categoria", post.getCategoria());
        post.put("presupuesto", post.getPresupuesto());
        post.put("user", ParseUser.getCurrentUser()); // Asocia el post al usuario actual

        // Guarda el post en segundo plano
        post.saveInBackground(e -> {
            if (e == null) { // Si no hay error en la creación del post
                ParseRelation<ParseObject> relation = post.getRelation("images"); // Obtiene la relación de imágenes asociadas al post

                for (String url : post.getImagenes()) { // Itera sobre cada URL de imagen
                    ParseObject imageObject = new ParseObject("Image"); // Crea un nuevo objeto de imagen en Parse
                    imageObject.put("url", url); // Establece la URL de la imagen

                    // Guarda la imagen en segundo plano
                    imageObject.saveInBackground(imgSaveError -> {
                        if (imgSaveError == null) { // Si no hay error al guardar la imagen
                            relation.add(imageObject); // Agrega la imagen a la relación del post

                            // Guarda nuevamente el post después de agregar las imágenes
                            post.saveInBackground(saveError -> {
                                if (saveError == null) {
                                    postResult.setValue("Post creado correctamente"); // Notifica éxito en la creación del post
                                } else {
                                    postResult.setValue("Error al crear post: " + saveError.getMessage()); // Notifica error al guardar el post
                                }
                            });
                        } else {
                            postResult.setValue("Error al guardar la imagen: " + imgSaveError.getMessage()); // Notifica error al guardar la imagen
                        }
                    });
                }
            } else {
                postResult.setValue("Error al crear post: " + e.getMessage()); // Notifica error al crear el post
            }
        });
        return postResult; // Devuelve el resultado del proceso de creación del post
    }

    // Método para obtener las publicaciones del usuario actual
    public LiveData<List<Post>> getPostsByCurrentUser() {
        MutableLiveData<List<Post>> postsResult = new MutableLiveData<>(); // Crea un MutableLiveData para almacenar los posts

        ParseUser currentUser = ParseUser.getCurrentUser(); // Obtiene el usuario actual

        if (currentUser == null) {
            postsResult.setValue(new ArrayList<>()); // Si no hay usuario, devuelve una lista vacía
            return postsResult;
        }

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class); // Crea una consulta para obtener posts
        query.whereEqualTo("user", currentUser); // Filtra los posts por el usuario actual
        query.include("user"); // Incluye información del usuario en los resultados

        query.findInBackground((posts, e) -> {
            if (e == null) {
                postsResult.setValue(posts); // Si no hay error, establece los posts obtenidos como resultado
            } else {
                postsResult.setValue(new ArrayList<>());
                Log.e("ParseError", "Error al obtener los posts: ", e); // Registra el error si ocurre
            }
        });

        return postsResult; // Devuelve los resultados de los posts obtenidos
    }

    // Método para obtener todas las publicaciones disponibles
    public LiveData<List<Post>> getAllPosts() {
        MutableLiveData<List<Post>> result = new MutableLiveData<>();

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("user");  // Asegúrate de incluir el usuario relacionado

        query.findInBackground((posts, e) -> {
            if (e == null) {
                List<Post> postList = new ArrayList<>();

                for (ParseObject postObject : posts) {
                    Log.d("PostObject", "ID: " + postObject.getObjectId() + ", Title: " + postObject.getString("titulo"));

                    Post post = ParseObject.create(Post.class);
                    post.setObjectId(postObject.getObjectId());
                    post.setTitulo(postObject.getString("titulo"));
                    post.setDescripcion(postObject.getString("descripcion"));
                    post.setDuracion(postObject.getInt("duracion"));
                    post.setCategoria(postObject.getString("categoria"));
                    post.setPresupuesto(postObject.getDouble("presupuesto"));

                    // Obtener imágenes relacionadas con el post
                    ParseRelation<ParseObject> relation = postObject.getRelation("images");
                    try {
                        List<ParseObject> images = relation.getQuery().find();
                        List<String> imageUrls = new ArrayList<>();

                        for (ParseObject imageObject : images) {
                            imageUrls.add(imageObject.getString("url"));
                        }
                        post.setImagenes(imageUrls);  // Establece las URLs de las imágenes en el objeto Post

                    } catch (ParseException parseException) {
                        parseException.printStackTrace();  // Maneja excepciones al obtener las imágenes
                    }

                    // Mapeo del usuario asociado al Post
                    ParseUser parseUser = postObject.getParseUser("user");
                    if (parseUser != null) {
                        try {
                            parseUser.fetchIfNeeded();  // Obtiene información adicional del usuario si es necesario

                            User user = ParseObject.createWithoutData(User.class, parseUser.getObjectId());
                            user.setUsername(parseUser.getUsername());
                            user.setEmail(parseUser.getEmail());
                            user.setFotoPerfil(parseUser.getString("fotoperfil"));
                            user.setRedSocial(parseUser.getString("redSocial"));

                            post.setUser(user);  // Asigna el usuario convertido al objeto Post

                        } catch (ParseException parseException) {
                            Log.e("FetchUserError", "Error al obtener el usuario: ", parseException);  // Maneja errores al obtener datos del usuario
                        }
                    } else {
                        Log.d("UserPointer", "User pointer es null");  // Registra si no hay un puntero a un usuario asociado.
                    }

                    postList.add(post);  // Agrega el objeto Post a la lista de resultados.
                }
                result.setValue(postList);  // Establece la lista completa de Posts como resultado.
            } else {
                result.setValue(new ArrayList<>());  // Si hay un error, devuelve una lista vacía.
                Log.e("ParseError", "Error al recuperar todos los posts: ", e);  // Registra el error.
            }
        });

        return result;  // Devuelve los resultados obtenidos.
    }

    // Método para eliminar un Post por su ID.
    public void removePost(String postId) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.getInBackground(postId, (post, e) -> {
            if (e == null) {
                post.deleteInBackground(e1 -> {
                    if (e1 == null) {
                        Log.d("PostDelete", "Post eliminado con éxito.");  // Registra éxito en la eliminación.
                    } else {
                        Log.e("PostDelete", "Error al eliminar el post: ", e1);  // Registra error si ocurre.
                    }
                });
            } else {
                Log.e("PostDelete", "Error al encontrar el post: ", e);  // Registra error si no se encuentra el Post.
            }
        });
    }

    // Método para obtener detalles específicos de un Post por su ID.
    public LiveData<Post> getPostDetail(String postId) {
        MutableLiveData<Post> result = new MutableLiveData<>();

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("user");  // Incluye información sobre el usuario que creó el Post.
        query.include("images");  // Incluye imágenes asociadas.

        query.getInBackground(postId, (post, e) -> {
            if (e == null) {
                ParseRelation<ParseObject> relation = post.getRelation("images");

                try {
                    List<ParseObject> images = relation.getQuery().find();
                    List<String> imageUrls = new ArrayList<>();

                    for (ParseObject imageObject : images) {
                        imageUrls.add(imageObject.getString("url"));  // Agrega URLs de imágenes a la lista.
                    }

                    post.setImagenes(imageUrls);  // Establece las imágenes en el objeto Post.

                } catch (ParseException parseException) {
                    parseException.printStackTrace();  // Maneja excepciones al obtener las imágenes.
                }

                ParseObject userObject = post.getParseObject("user");

                if (userObject != null)
                    try {
                        userObject.fetchIfNeeded();

                        User user = new User();
                        user.setUsername(userObject.getString("username"));
                        user.setEmail(userObject.getString("email"));
                        user.setFotoPerfil(userObject.getString("foto_perfil"));

                        post.setUser(user);  // Asigna el usuario asociado al Post.

                    } catch (ParseException userFetchException) {
                        userFetchException.printStackTrace();  // Maneja errores al obtener datos del usuario.
                    } else {
                    Log.w("PostDetail", "El usuario asociado al post es nulo.");  //
                }

                result.setValue(post);  //
            } else {
                Log.e("ParseError", "Error al obtener el post: ", e);
                result.setValue(null);  //
            }
        });

        return result;  //
    }

    public interface CommentsCallback {
        void onSuccess(List<ParseObject> comments);  //
        void onFailure(Exception e);   //
    }

    public void fetchComments(String postId, CommentsCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comentario");

        query.whereEqualTo("post", ParseObject.createWithoutData("Post", postId));

        query.include("user");  //

        query.findInBackground((comentarios, e) -> {
            if (e == null) {
                callback.onSuccess(comentarios);
            } else {
                callback.onFailure(e);
            }
        });
    }

    public void saveComment(String postId, String commentText, ParseUser currentUser, SaveCallback callback) {
        ParseObject post = ParseObject.createWithoutData("Post", postId);
        ParseObject comentario = new ParseObject("Comentario");
        comentario.put("texto", commentText);
        comentario.put("post", post);
        comentario.put("user", currentUser);
        comentario.saveInBackground(callback);
    }
}