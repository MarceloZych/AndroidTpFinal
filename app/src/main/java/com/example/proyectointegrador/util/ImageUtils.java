package com.example.proyectointegrador.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    public static void pedirPermisos(Activity activity, String[] permisos, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisos = new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permisos = new String[] {
                      Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }
        ActivityCompat.requestPermissions(activity, permisos, requestCode);
    }

    public static void openGallery(Context context, ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    public static void subirImagenParse (Context context, Uri imageUri, ImageUploadCallback callback) {

        InputStream inputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(imageUri);
            byte[] bytes = getBytesFromInputStream(inputStream);

            if (bytes == null) {
                callback.onFailure(new Exception("El arreglo de bytes es nulo"));
                return;
            }
            ParseFile file = new ParseFile("imagen.jpg", bytes);

            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        String imageUrl = file.getUrl();
                        callback.onSuccess(imageUrl);
                    } else {
                        callback.onFailure(e);
                    }
                }
            });
        } catch (IOException e) {
            callback.onFailure(e);
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;

        while((len = inputStream.read(buffer)) != -1){
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    public interface ImageUploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }
}
