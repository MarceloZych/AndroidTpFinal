package com.example.proyectointegrador.adapters;

import android.content.Context; // Importa Context para acceder a recursos y servicios de la aplicación
import android.view.LayoutInflater; // Importa LayoutInflater para inflar layouts
import android.view.View; // Importa View para manejar vistas
import android.view.ViewGroup; // Importa ViewGroup para manejar grupos de vistas
import android.widget.ImageView; // Importa ImageView para mostrar imágenes

import androidx.annotation.NonNull; // Importa la anotación para indicar que un parámetro no puede ser nulo
import androidx.recyclerview.widget.RecyclerView; // Importa RecyclerView para crear listas de elementos desplazables

import com.example.proyectointegrador.R; // Importa los recursos del proyecto
import com.squareup.picasso.Picasso; // Importa Picasso, una biblioteca para cargar imágenes

import java.util.*; // Importa las utilidades de Java, como List

// Clase adaptadora para mostrar imágenes en un RecyclerView
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> imageUrls; // Lista de URLs de imágenes a mostrar
    private Context context; // Contexto de la aplicación

    // Constructor que inicializa la lista de URLs y el contexto
    public ImageAdapter(List<String> imageUrls, Context context) {
        this.imageUrls = imageUrls; // Asigna la lista de URLs a la variable de instancia
        this.context = context; // Asigna el contexto a la variable de instancia
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Infla el layout del item (item_image.xml) y crea un nuevo ViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view); // Devuelve un nuevo ImageViewHolder con la vista inflada
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Obtiene la URL de la imagen en la posición actual
        String imageUrl = imageUrls.get(position);
        // Usa Picasso para cargar la imagen desde la URL en el ImageView del ViewHolder
        Picasso.get().load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size(); // Devuelve el número total de elementos en la lista de URLs
    }

    // Método para actualizar la lista de URLs y notificar al adaptador que los datos han cambiado
    public void setImageUrls(List<String> newImageUrls) {
        this.imageUrls = newImageUrls; // Actualiza la lista de URLs con la nueva lista proporcionada
        notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado y se deben volver a dibujar
    }

    // Clase interna estática que actúa como ViewHolder para cada elemento del RecyclerView
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView; // Variable para el ImageView que mostrará la imagen

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView); // Llama al constructor del ViewHolder padre
            imageView = itemView.findViewById(R.id.image_view); // Inicializa el ImageView buscando por su ID en el layout inflado
        }
    }
}