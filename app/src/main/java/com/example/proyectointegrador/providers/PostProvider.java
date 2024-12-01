package com.example.proyectointegrador.providers;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectointegrador.model.Post;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PostProvider {

    public LiveData<String> addPost(Post post) {
        MutableLiveData<String> postResult = new MutableLiveData<>();
        ParseObject postObject = new ParseObject("Post");

        postObject.put("titulo", post.getTitulo());
        postObject.put("descripcion", post.getDescripcion());
        postObject.put("duracion", post.getDuracion());
        postObject.put("categoria", post.getCategoria());
        postObject.put("presupuesto", post.getPresupuesto());
        postObject.put("user", ParseUser.getCurrentUser());
        postObject.saveInBackground(e -> {
            if (e == null) {
                ParseRelation<ParseObject> relation = postObject.getRelation("images");
                for (String url: post.getImagenes()) {
                    ParseObject imageObject = new ParseObject("Image");
                    imageObject.put("url", url);
                    imageObject.saveInBackground(imgSaveError -> {
                        if (imgSaveError == null) {
                            relation.add(imageObject);
                            postObject.saveInBackground(saveError -> {
                                if (saveError == null) {
                                    postResult.setValue("Post creado correctamente");
                                } else {
                                    postResult.setValue("Error al crear post: " + saveError.getMessage());
                                }
                            });
                        } else {
                            postResult.setValue("Error al guardar la imagen: " + imgSaveError.getMessage());
                        }
                    });
                }
            } else {
                postResult.setValue("Error al crear post: " + e.getMessage());
            }
        });

        return postResult;
    }

    public LiveData<List<Post>> getPostsByCurrentUser() {
        MutableLiveData<List<Post>> postsResult = new MutableLiveData<>();
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            postsResult.setValue(new ArrayList<>());
            return postsResult;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
        query.whereEqualTo("user", currentUser);
        query.include("user");
        query.findInBackground((posts, e) -> {
          if (e == null) {
              List<Post> postsList = new ArrayList<>();

              for (ParseObject parseObject : posts) {
                  Post post = new Post(
                          parseObject.getString("titulo"),
                          parseObject.getString("descripcion"),
                          parseObject.getString("categoria"),
                          parseObject.getInt("duracion"),
                          parseObject.getDouble("presupuesto")
                  );

                  Log.d("PostProvider", "Post encontrado: " + post.getTitulo());
                  ParseRelation<ParseObject> relation = parseObject.getRelation("images");

                  try {
                      List<ParseObject> images = relation.getQuery().find();
                      List<String> imageUrls = new ArrayList<>();

                      for (ParseObject imageObject : images) {
                          imageUrls.add(imageObject.getString("url"));
                      }

                      post.setImagenes(imageUrls);
                      Log.d("PostProvider", "Imagenes del post: " + post.getImagenes());

                  } catch (ParseException parseException) {
                    parseException.printStackTrace();
                  }

                  postsList.add(post);
              }
              postsResult.setValue(postsList);
          } else {
              postsResult.setValue(new ArrayList<>());
              Log.e("ParseError", "Error al obtener los posts: ", e);
          }
        });

        return postsResult;
    }







    //private ParseUser currentUser;

    // Clase predeterminada de Parse para usuarios
/*    private static final String USER_CLASS_NAME = "User";

    public LiveData<String> createUser(User user) {
        MutableLiveData<String> result = new MutableLiveData<>();

        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(user.getUsername());
        parseUser.setPassword(user.getPassword());
        parseUser.setEmail(user.getEmail());

        parseUser.signUpInBackground(e -> {
            if (e == null) {
                result.setValue("Usuario creado correctamente");
            } else {
                result.setValue("Error al crear usuario: " + e.getMessage());
            }
        });
        return result;
    }

    public LiveData<User> getUser(String mail) {
        MutableLiveData<User> userData = new MutableLiveData<>();

        ParseQuery<ParseUser> query = ParseQuery.getQuery(USER_CLASS_NAME);
        query.whereEqualTo("email", mail);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null && !users.isEmpty()) {
                    ParseUser parseUser = users.get(0);
                    User user = new User();
                    user.setId(parseUser.getObjectId());
                    user.setUsername(parseUser.getUsername());
                    user.setEmail(parseUser.getEmail());
                    userData.setValue(user);
                    Log.d("UserProvider", "Usuario encontrado: " + user.getUsername());
                } else {
                    Log.d("UserProvider", "Usuario no encontrado");
                    userData.setValue(null);
                }
            }
        });
       *//* .collection(USER_COLLECTION)
                .whereEqualTO("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            Log.d("UserProvider", "Usuario encontrado: " + user.getUsername());
                        } else {
                            Log.d("UserProvider", "Usuario encontrado pero no se pudo obtener los datos");
                        }
                        userData.setValue(user);
                    } else {
                        Log.d("UserProvider", "Usuario no encontrado");
                        userData.setValue(null);
                    }
                });*//*
        return userData;
    }

    public LiveData<String> updateUser(User user) {
        MutableLiveData<String> result = new MutableLiveData<>();

        ParseQuery<ParseUser> query = ParseQuery.getQuery(USER_CLASS_NAME);
        query.getInBackground(user.getId(), (parseUser, e) -> {
            if (e == null) {
                parseUser.setUsername(user.getUsername());
                parseUser.setEmail(user.getEmail());
                parseUser.setPassword(user.getPassword());
                parseUser.saveInBackground(e1 -> {
                    if (e1 == null) {
                        result.setValue("Usuario actualizado correctamente");
                        Log.d("UserProvider", "Usuario actualizado: " + parseUser.getObjectId());
                    } else {
                        result.setValue("Error al actualizar usuario: " + e1.getMessage());
                        Log.e("UserProvider", "Error al actualizar usuario: " + e1);
                    }
                });
            } else {
                result.setValue("Error al encontrar usuario: " + e.getMessage());
                Log.e("UserProvider", "Error al encontrar usuario: " + e);
            }
        });
        *//*firestore.collection(USER_COLLECTION).document(user.getId()).set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.setValue("Usuario actualizado correctamente");
                    } else {
                        result.setValue("Error al actualizar usuario");
                    }
                });*//*
        return result;
    }


    public LiveData<String> deleteUser(String userId) {
        MutableLiveData<String> result = new MutableLiveData<>();

        ParseQuery<ParseUser> query = ParseQuery.getQuery(USER_CLASS_NAME);
        query.getInBackground(userId, (parseUser, e) -> {
            if (e == null) {
                parseUser.deleteInBackground(e1 -> {
                    if (e1 == null) {
                        result.setValue("Usuario eliminado correctamente");
                        Log.d("UserProvider", "Usuario eliminado: " + userId);
                    } else {
                        result.setValue("Error al eliminar usuario: " + e1.getMessage());
                        Log.e("UserProvider", "Error al eliminar usuario: " + e1);
                    }
                });
            } else {
                result.setValue("Error al encontrar usuario: " + e.getMessage());
                Log.e("UserProvider", "Error al encontrar usuario: " + e);
            }
        });
        *//*firestore.collection(USER_COLLECTION).document(userId).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.setValue("Usuario eliminado correctamente");
                    } else {
                        result.setValue("Error al eliminar usuario");
                    }
                });*//*
        return result;
    }*/
}