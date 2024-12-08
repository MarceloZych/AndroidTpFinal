package com.example.proyectointegrador.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Fábrica de ViewModel para la aplicación principal.
 *
 * @author Javier Zader
 * @version 1.0
 */
public class MainViewModelFactory implements ViewModelProvider.Factory {

    /**
     * El contexto de la aplicación.
     */
    private Context context;

    /**
     * Constructor para crear una instancia de la fábrica de ViewModel.
     *
     * @param context El contexto de la aplicación.
     */
    public MainViewModelFactory(Context context) {
        this.context = context;
    }

    /**
     * Crea una instancia de ViewModel según la clase proporcionada.
     *
     * @param modelClass La clase de ViewModel a crear.
     * @param <T> El tipo de ViewModel a crear.
     * @return La instancia de ViewModel creada.
     */
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // Verificar si la clase de ViewModel es assignable a MainViewModel
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            // Crear una instancia de MainViewModel con el contexto proporcionado
            return (T) (MainViewModel) new MainViewModel(context);
        }
        // Si la clase de ViewModel no es assignable a MainViewModel, lanzar una excepción
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
