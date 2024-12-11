package com.example.proyectointegrador.view.fragments; // Paquete donde se encuentra la clase

import android.view.LayoutInflater; // Importa LayoutInflater para inflar layouts
import android.view.View; // Importa View para manejar vistas
import android.view.ViewGroup; // Importa ViewGroup para manejar grupos de vistas
import android.os.Bundle; // Importa Bundle para manejar el estado del fragmento

import androidx.fragment.app.Fragment; // Importa Fragment para crear un fragmento

import com.example.proyectointegrador.R; // Importa los recursos del proyecto

// Clase que representa un fragmento para mostrar chats
public class ChatsFragment extends Fragment {

    // Constructor vacío por defecto
    public ChatsFragment() {
    }

    // Método estático que crea una nueva instancia de ChatsFragment
    public static ChatsFragment newInstance(String param1, String param2) {
        return new ChatsFragment(); // Devuelve una nueva instancia del fragmento
    }

    // Método llamado para crear la vista del fragmento
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla el layout del fragmento (fragment_chats.xml) y lo devuelve como vista
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }
}