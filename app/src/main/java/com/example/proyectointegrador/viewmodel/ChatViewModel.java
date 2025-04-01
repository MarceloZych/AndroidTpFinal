package com.example.proyectointegrador.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectointegrador.model.Mensaje;
import com.example.proyectointegrador.providers.ChatProvider;
import com.parse.ParseUser;

import java.util.List;

public class ChatViewModel extends ViewModel {
    private static final String TAG = "ChatViewModel";

    private final ChatProvider chatProvider;
    private final MutableLiveData<ChatState> chatState = new MutableLiveData<>(ChatState.initial());

    public ChatViewModel() {
        this.chatProvider = new ChatProvider();
    }

    public LiveData<ChatState> getChatState() {
        return chatState;
    }

    public void loadMessages(ParseUser otherUser) {
        if (otherUser == null) {
            chatState.setValue(ChatState.error("No chat user provided"));
            return;
        }
        chatState.setValue(ChatState.loading());
        chatProvider.cargarMensajes(otherUser).observeForever(messages -> {
            if (messages != null) {
                chatState.setValue(ChatState.success(messages));
            } else {
                chatState.setValue(ChatState.error("Failed to load messages"));
            }
        });
    }

    public void sendMessage(String text, ParseUser sender, ParseUser recipient) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        chatProvider.enviarMensaje(text, sender, recipient);
    }

    public void refreshMessages() {
        chatProvider.pollForNewMessages();
    }

    public void resumePolling() {
        chatProvider.startPolling();
    }

    public void pausePolling() {
        chatProvider.stopPolling();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        chatProvider.cleanup();
    }

    public static class ChatState {
        private final Status status;
        private final List<Mensaje> messages;
        private final String errorMessage;

        private ChatState(Status status, List<Mensaje> messages, String errorMessage) {
            this.status = status;
            this.messages = messages;
            this.errorMessage = errorMessage;
        }

        public static ChatState initial() {
            return new ChatState(Status.INITIAL, null, null);
        }

        public static ChatState loading() {
            return new ChatState(Status.LOADING, null, null);
        }

        public static ChatState success(List<Mensaje> messages) {
            return new ChatState(Status.SUCCESS, messages, null);
        }

        public static ChatState error(String errorMessage) {
            return new ChatState(Status.ERROR, null, errorMessage);
        }

        public Status getStatus() { return status; }
        public List<Mensaje> getMessages() { return messages; }
        public String getErrorMessage() { return errorMessage; }
    }

    public enum Status {
        INITIAL, LOADING, SUCCESS, ERROR
    }
}
