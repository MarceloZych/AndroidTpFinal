package com.example.proyectointegrador.util;

public class Validaciones {
    public static boolean validarUsuario(String usuario) {
        return usuario != null && !usuario.isEmpty() && usuario.length() >= 3;
    }

    public static boolean validarCorreo(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email != null && email.matches(emailPattern);
    }

    public static String ValidarContrasena(String password, String password1) {
        if (password == null || password.isEmpty() || password1 == null || password1.isEmpty()) {
            return "Las contraseñas no pueden estar vacías";
        }
        if (password.length() < 6 || password1.length() < 6) {
            return "Las contraseñas deben tener al menos 6 caracteres";
        }
        if (!password.equals(password1)) {
            return "Las contraseñas no coinciden";
        }
        return null;
    }

    public static boolean controlarPassword(String password) {
        return (password != null && password.length() >= 6);
    }
} 
