package com.example.proyectointegrador.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Mensaje")
public class Mensaje extends ParseObject {
    public static final String KEY_TEXTO = "text";
    public static final String KEY_REMITENTE = "remitente";
    public static final String KEY_DESTINATARIO = "destinatario";
    public static final String KEY_FECHA = "fecha";
    public Mensaje() {}

    public String getTexto() {
        return getString(KEY_TEXTO);
    }

    public void setTexto(String texto) {
        if (texto != null) put(KEY_TEXTO, texto);
    }
    public ParseUser getRemitente() {
        return getParseUser(KEY_REMITENTE);
    }
    public void setRemitente(ParseUser user) {
        if (user != null) put(KEY_REMITENTE, user);
    }
    public ParseUser getDestinatario() {
        return getParseUser(KEY_DESTINATARIO);
    }
    public void setDestinatario(ParseUser user) {
        if (user != null) put(KEY_DESTINATARIO, user);
    }
    public Date getFecha() {
        return getDate(KEY_FECHA);
    }
    public void setFecha(Date fecha) {
        if (fecha != null) put(KEY_FECHA, fecha);
    }
}
