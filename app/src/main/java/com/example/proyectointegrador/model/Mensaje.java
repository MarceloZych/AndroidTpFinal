package com.example.proyectointegrador.model;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

public class Mensaje extends ParseObject {
    public static final String KEY_TEXTO = "text";
    public static final String KEY_REMITENTE = "remitente";
    public static final String KEY_DESTINATARIO = "destinatario";
    public Mensaje() {}

    public String getTexto() {
        return getString(KEY_TEXTO);
    }
    public void setTexto(String texto) {
        put(KEY_TEXTO, texto);
    }
    public ParseUser getRemitente() {
        return getParseUser(KEY_REMITENTE);
    }
    public void setRemitente(ParseUser user) {
        put(KEY_REMITENTE, user);
    }
    public ParseUser getDestinatario() {
        return getParseUser(KEY_DESTINATARIO);
    }
    public void setDestinatario(ParseUser user) {
        put(KEY_DESTINATARIO, user);
    }
    public Date getFecha() {
        return getDate("fecha");
    }
}
