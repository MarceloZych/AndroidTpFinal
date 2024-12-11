package com.example.proyectointegrador.util; // Paquete donde se encuentra la clase

import android.util.Log; // Importa Log para registrar mensajes en el Logcat

// Clase utilitaria que proporciona métodos para validar diferentes tipos de datos
public class Validaciones {

    // Método estático para validar un texto
    public static boolean validarTexto(String texto) {
        // Verifica que el texto no sea nulo, no esté vacío y tenga al menos 3 caracteres
        return texto != null
                && !texto.isEmpty()
                && texto.length() >= 3;
        // Se puede descomentar la siguiente línea para validar que el texto contenga solo letras:
        // && usuario.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+$");
    }

    // Método estático para validar un número en formato String
    public static int validarNumero(String numero) {
        try {
            int valor = Integer.parseInt(numero); // Intenta convertir el String a un entero
            return valor >= 0 ? valor : -1; // Retorna el número si es positivo; de lo contrario, retorna -1
        } catch (NumberFormatException e) {
            return -1; // Retorna -1 si no es un número válido (por ejemplo, si no se puede convertir)
        }
    }

    // Método estático para validar un nombre de usuario
    public static boolean validarUsuario(String usuario) {
        // Verifica que el usuario no sea nulo, no esté vacío y tenga al menos 3 caracteres
        return usuario != null && !usuario.isEmpty() && usuario.length() >= 3;
    }

    // Método estático para validar un correo electrónico
    public static boolean validarCorreo(String email) {
        // Patrón regex para validar el formato del correo electrónico
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        // Verifica que el correo no sea nulo y coincida con el patrón definido
        return email != null && email.matches(emailPattern);
    }

    // Método estático para validar contraseñas
    public static String ValidarContrasena(String password, String password1) {
        Log.d("datos", "password: " + password + ", password1: " + password1); // Registra las contraseñas ingresadas

        // Verifica que ninguna de las contraseñas esté vacía
        if (password == null || password.isEmpty() || password1 == null || password1.isEmpty()) {
            return "Las contraseñas no pueden estar vacías"; // Mensaje de error si están vacías
        }

        // Verifica que la contraseña tenga al menos 6 caracteres
        if (password.length() < 6) {
            return "Las contraseñas deben tener al menos 6 caracteres"; // Mensaje de error si es demasiado corta
        }

        // Verifica que ambas contraseñas coincidan
        if (!password.equals(password1)) {
            return "Las contraseñas no coinciden"; // Mensaje de error si no coinciden
        }

        return null; // Retorna null si todas las validaciones son exitosas (sin errores)
    }

    // Método estático para controlar la longitud mínima de una contraseña
    public static boolean controlarPassword(String password) {
        // Verifica que la contraseña no sea nula y tenga al menos 6 caracteres
        return (password != null && password.length() >= 6);
    }
}
