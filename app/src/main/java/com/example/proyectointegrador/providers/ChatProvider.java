package com.example.proyectointegrador.providers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectointegrador.model.Mensaje;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.SubscriptionHandling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatProvider {
    private static final String CLASS_NAME = "Mensaje";

    private static final String TAG = "ChatProvider";

    private static final long POLLING_INTERVAL = 5000;
    private final ParseLiveQueryClient liveQueryClient;

    private final MutableLiveData<List<Mensaje>> messagesLiveData;

    private ParseUser currentChatUser;

    private SubscriptionHandling<Mensaje> subscription;

    private ParseQuery<Mensaje> query;

    private final Handler pollingHandler = new Handler(Looper.getMainLooper());

    private final AtomicBoolean isPolling = new AtomicBoolean(false);

    private Date lastMessageTimestamp = null;
    private final Set<String> processedMessageIds = new HashSet<>();

    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPolling.get() && currentChatUser != null) {
                pollForNewMessages();
                pollingHandler.postDelayed(this, POLLING_INTERVAL);
            }
        }
    };

    public ChatProvider() {
        liveQueryClient = ParseLiveQueryClient.Factory.getClient();
        messagesLiveData = new MutableLiveData<>(new ArrayList<>());
    }
    public void enviarMensaje(String texto, ParseUser remitente, ParseUser destinatario) {

        Mensaje mensaje = new Mensaje();
        mensaje.setTexto(texto);
        mensaje.setRemitente(remitente);
        mensaje.setDestinatario(destinatario);
        mensaje.put("fecha", new Date());

        mensaje.saveInBackground(e -> {
            if (e == null) {
                Log.d(TAG, "Mensaje enviado correctamente");
                lastMessageTimestamp = new Date();
                pollForNewMessages();

            } else {
                Log.e(TAG, "Error al enviar mensaje: " + e.getMessage());
            }
        });
    }
    public LiveData<List<Mensaje>> cargarMensajes(ParseUser otroUsuario) {
        cleanupPreviousSession();
        currentChatUser = otroUsuario;
        if (currentChatUser == null) {
            Log.e(TAG, "Cannot load messages: chat user is null");
            messagesLiveData.setValue(new ArrayList<>());
            return messagesLiveData;
        }
        loadInitialMessages();
        setupLiveQuery();
        startPolling();
        return messagesLiveData;
    }

    private void loadInitialMessages() {
        ParseQuery<Mensaje> query = createChatQuery();
        query.setLimit(1000);
        query.findInBackground((mensajes, e) -> {
            if (e == null) {
                mensajes.sort(Comparator.comparing(Mensaje::getFecha));
                processedMessageIds.clear();
                for (Mensaje m : mensajes) {
                    processedMessageIds.add(m.getObjectId());
                }

                messagesLiveData.postValue(mensajes);
                if (!mensajes.isEmpty()) {
                    lastMessageTimestamp = mensajes.get(mensajes.size() - 1).getFecha();
                }
            } else {
                Log.e(TAG, "Error loading initial messages", e);
                messagesLiveData.postValue(new ArrayList<>());
            }
        });
    }
    public void pollForNewMessages() {
        if (currentChatUser == null) {
            Log.d(TAG, "No chat user set for polling");
            return;
        }
        ParseQuery<Mensaje> query = createChatQuery();
        if (lastMessageTimestamp != null) {
            query.whereGreaterThan("fecha", lastMessageTimestamp);
        }
        query.findInBackground((newMessages, e) -> {
            if (e == null && !newMessages.isEmpty()) {
                updateMessages(newMessages);
            }
        });
    }

    private void setupLiveQuery() {
        if (currentChatUser == null) {
            Log.e(TAG, "Cannot setup LiveQuery: chat user is null");
            return;
        }
        query = createChatQuery();
        subscription = liveQueryClient.subscribe(query);
        subscription.handleEvent(SubscriptionHandling.Event.CREATE, (q, mensaje) -> {
            if (!processedMessageIds.contains(mensaje.getObjectId())) {
                updateMessages(List.of(mensaje));
            }
        });
    }

    private ParseQuery<Mensaje> createChatQuery() {
        if (currentChatUser == null || ParseUser.getCurrentUser() == null) {
            Log.e(TAG, "Cannot create query: currentChatUser or currentUser is null");
            return ParseQuery.getQuery(CLASS_NAME); // Return an empty query to avoid crashes
        }
        ParseQuery<Mensaje> sent = ParseQuery.getQuery(Mensaje.class)
                .whereEqualTo("remitente", ParseUser.getCurrentUser())
                .whereEqualTo("destinatario", currentChatUser);
        ParseQuery<Mensaje> received = ParseQuery.getQuery(Mensaje.class)
                .whereEqualTo("remitente", currentChatUser)
                .whereEqualTo("destinatario", ParseUser.getCurrentUser());
        return ParseQuery.or(List.of(sent, received))
                .include("remitente")
                .include("destinatario")
                .addAscendingOrder("fecha");
    }

    private void updateMessages(List<Mensaje> newMessages) {
        List<Mensaje> currentList = new ArrayList<>(messagesLiveData.getValue() != null ? messagesLiveData.getValue() : new ArrayList<>());
        for (Mensaje m : newMessages) {
            if (processedMessageIds.add(m.getObjectId())) {
                currentList.add(m);
            }
    }
         currentList.sort(Comparator.comparing(Mensaje::getFecha));
         messagesLiveData.postValue(currentList);
         if (!currentList.isEmpty()) {
        lastMessageTimestamp = currentList.get(currentList.size() - 1).getFecha();
        }
    }
    public void startPolling() {
        if (isPolling.compareAndSet(false, true)) {
            pollingHandler.post(pollingRunnable);
        }
    }

    public void stopPolling() {
        if (isPolling.compareAndSet(true, false)) {
            Log.d(TAG, "Deteniendo polling de mensajes");
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }

    private void cleanupPreviousSession() {
        if (subscription != null && query != null) {
            liveQueryClient.unsubscribe(query);
            subscription = null;
            query = null;
        }
        stopPolling();
        currentChatUser = null;
        lastMessageTimestamp = null;
        processedMessageIds.clear();
        messagesLiveData.setValue(new ArrayList<>());
    }

    public void cleanup() {
        cleanupPreviousSession();
    }
}
