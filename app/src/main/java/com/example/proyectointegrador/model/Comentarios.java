package com.example.proyectointegrador.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Comentario")
public class Comentarios extends ParseObject {

    public Comentarios() {}

    public String getId() {
        return getObjectId();
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }
    public Post getPost(Post post) {
        return (Post) getParseObject("post");
    }

    public void setPost(Post post) {
        put("post", post);
    }

    public String getTexto() {
        return getString("texto");
    }

    public void setTexto(String texto) {
        put("texto", texto);
    }
    public Date getCreateAt() {
        return getCreatedAt();
    }
}
