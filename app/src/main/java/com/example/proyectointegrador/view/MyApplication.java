package com.example.proyectointegrador.view; // Paquete donde se encuentra la clase

import android.app.Application; // Importa la clase Application para inicializar componentes de la aplicación

import com.example.proyectointegrador.R; // Importa los recursos del proyecto
import com.example.proyectointegrador.model.Comentarios;
import com.example.proyectointegrador.model.Mensaje;
import com.example.proyectointegrador.model.Post; // Importa el modelo Post
import com.example.proyectointegrador.model.User; // Importa el modelo User
import com.parse.Parse; // Importa Parse para inicializar y configurar Parse
import com.parse.ParseACL; // Importa ParseACL para manejar listas de control de acceso
import com.parse.ParseObject; // Importa ParseObject para trabajar con objetos en Parse

// Clase que extiende Application para configurar Parse al inicio de la aplicación
public class MyApplication extends Application {

    // Método llamado cuando la aplicación es creada
    @Override
    public void onCreate() {
        super.onCreate(); // Llama al método onCreate de la clase padre

        // Habilita el uso de un datastore local en el dispositivo
        Parse.enableLocalDatastore(this);

        // Registra las subclases de ParseObject que se utilizarán en la aplicación
        ParseObject.registerSubclass(Post.class); // Registra la subclase Post
        ParseObject.registerSubclass(User.class); // Registra la subclase User
        ParseObject.registerSubclass(Comentarios.class);
        ParseObject.registerSubclass(Mensaje.class);

        // Inicializa Parse con la configuración necesaria
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id)) // Establece el ID de la aplicación desde los recursos
                .clientKey(getString(R.string.back4app_client_key)) // Establece la clave del cliente desde los recursos
                .server(getString(R.string.back4app_server_url)) // Establece la URL del servidor desde los recursos
                .build() // Construye la configuración
        );

        // Crea una ACL (Access Control List) por defecto para los objetos en Parse
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true); // Permite el acceso de lectura público a todos los objetos
        defaultACL.setPublicWriteAccess(true); // Permite el acceso de escritura público a todos los objetos

        // Establece la ACL por defecto para todos los objetos nuevos en Parse
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
