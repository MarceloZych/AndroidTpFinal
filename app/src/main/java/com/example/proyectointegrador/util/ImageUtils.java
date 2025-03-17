package com.example.proyectointegrador.util; // Paquete donde se encuentra la clase

import android.app.Activity; // Importa Activity para manejar actividades de Android
import android.content.Context; // Importa Context para acceder a recursos y servicios de la aplicación
import android.content.Intent; // Importa Intent para iniciar nuevas actividades
import android.database.Cursor; // Importa Cursor para manejar resultados de consultas a bases de datos
import android.net.Uri; // Importa Uri para manejar URIs (identificadores de recursos)
import android.os.Build; // Importa Build para verificar la versión del sistema operativo
import android.provider.MediaStore; // Importa MediaStore para acceder a contenido multimedia
import android.Manifest; // Importa Manifest para manejar permisos

import androidx.activity.result.ActivityResultLauncher; // Importa ActivityResultLauncher para manejar resultados de actividades
import androidx.core.app.ActivityCompat; // Importa ActivityCompat para manejar compatibilidad con versiones anteriores

import com.parse.ParseException; // Importa ParseException para manejar errores de Parse
import com.parse.ParseFile; // Importa ParseFile para trabajar con archivos en Parse
import com.parse.SaveCallback; // Importa SaveCallback para manejar resultados de guardado

import java.io.ByteArrayOutputStream; // Importa ByteArrayOutputStream para convertir datos en un arreglo de bytes
import java.io.IOException; // Importa IOException para manejar excepciones de entrada/salida
import java.io.InputStream; // Importa InputStream para leer datos desde flujos de entrada

// Clase utilitaria que proporciona métodos relacionados con la gestión de imágenes
public class ImageUtils {

    // Método estático que solicita permisos necesarios para acceder a imágenes
    public static void pedirPermisos(Activity activity, String[] permisos, int requestCode) {
        // Verifica la versión del SDK para determinar qué permisos solicitar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permisos = new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES // Permiso para leer imágenes en Android 13 y superior
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permisos = new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE // Permiso para leer almacenamiento externo en versiones anteriores
            };
        }
        ActivityCompat.requestPermissions(activity, permisos, requestCode); // Solicita los permisos al usuario
    }

    // Método estático que abre la galería de imágenes
    public static void openGallery(Context context, ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // Crea un Intent para seleccionar una imagen
        launcher.launch(intent); // Lanza el Intent utilizando el ActivityResultLauncher
    }

    // Método estático que sube una imagen a Parse
    public static void subirImagenParse(Context context, Uri imageUri, ImageUploadCallback callback) {
        InputStream inputStream = null; // Inicializa el InputStream que leerá la imagen

        try {
            inputStream = context.getContentResolver().openInputStream(imageUri); // Abre un flujo de entrada desde el URI de la imagen
            byte[] bytes = getBytesFromInputStream(inputStream); // Convierte el InputStream a un arreglo de bytes

            if (bytes == null) { // Verifica si el arreglo de bytes es nulo
                callback.onFailure(new Exception("El arreglo de bytes es nulo")); // Notifica error al callback si es nulo
                return;
            }

            ParseFile file = new ParseFile("imagen.jpg", bytes); // Crea un nuevo archivo ParseFile con los bytes leídos

            file.saveInBackground(new SaveCallback() { // Guarda el archivo en segundo plano
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        String imageUrl = file.getUrl(); // Obtiene la URL del archivo guardado en Parse
                        callback.onSuccess(imageUrl); // Notifica éxito al callback con la URL de la imagen
                    } else {
                        callback.onFailure(e); // Notifica error al callback si ocurre un problema al guardar el archivo
                    }
                }
            });
        } catch (IOException e) {
            callback.onFailure(e); // Notifica error al callback si ocurre una excepción al abrir el InputStream
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close(); // Cierra el InputStream si fue abierto correctamente
                } catch (IOException e) {
                    e.printStackTrace(); // Imprime la traza del error si no se puede cerrar el InputStream
                }
            }
        }
    }

    // Método privado que convierte un InputStream a un arreglo de bytes
    private static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream(); // Crea un ByteArrayOutputStream para almacenar los bytes leídos
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while((len = inputStream.read(buffer)) != -1){
            byteBuffer.write(buffer, 0, len); // Escribe los bytes leídos en el ByteArrayOutputStream
        }

        return byteBuffer.toByteArray(); // Devuelve el arreglo de bytes completo leído del InputStream
    }

    // Interfaz que define los métodos a implementar por los callbacks de carga de imágenes
    public interface ImageUploadCallback {
        void onSuccess(String imageUrl);  // Método llamado cuando la carga es exitosa, recibe la URL de la imagen
        void onFailure(Exception e);  // Método llamado cuando ocurre un error durante la carga, recibe la excepción correspondiente
    }

    // Método estático que obtiene la ruta real desde un URI dado en el contexto proporcionado
    public static String getRealPathFromUri(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };  // Proyección que especifica qué columnas se desean obtener (en este caso, la ruta del archivo)

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null );  // Realiza una consulta sobre el URI proporcionado

        if (cursor == null) return null;  // Si no hay cursor, devuelve nulo

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  // Obtiene el índice de la columna DATA

        cursor.moveToFirst();  // Mueve el cursor a la primera fila del resultado

        String path = cursor.getString(column_index);  // Obtiene la ruta real desde el cursor usando el índice obtenido

        cursor.close();  // Cierra el cursor después de usarlo

        return path;  // Devuelve la ruta real obtenida del URI
    }
}
