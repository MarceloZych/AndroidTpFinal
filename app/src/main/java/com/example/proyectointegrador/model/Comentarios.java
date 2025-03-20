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
        put("user", post);
    }
    public Date getCreateAt() {
        return super.getCreatedAt();
    }
}
