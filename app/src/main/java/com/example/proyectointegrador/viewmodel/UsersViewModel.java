package com.example.proyectointegrador.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectointegrador.providers.AuthProvider;
import com.parse.ParseUser;

import java.util.List;

public class UsersViewModel extends ViewModel {
    private final MutableLiveData<State> usersState = new MutableLiveData<>();
    private AuthProvider authProvider;

    public void init(AuthProvider authProvider) {
        this.authProvider = authProvider;
        loadUsers();
    }

    public LiveData<State> getUsersState() {
        return usersState;
    }

    private void loadUsers() {
        usersState.setValue(new State(Status.LOADING));

        String currentUserId = authProvider.getCurrentUserID();
        if (currentUserId == null) {
            usersState.setValue(new State(Status.ERROR, "No authenticated user"));
            return;
        }

        authProvider.getAllUsers().observeForever(users -> {
            if (users != null) {
                usersState.setValue(new State(Status.SUCCESS, users));
            } else {
                usersState.setValue(new State(Status.ERROR, "Failed to load users"));
            }
        });
    }

    // State class with public getters
    public static class State {
        private final Status status;
        private final List<ParseUser> users;
        private final String errorMessage;

        State(Status status) {
            this(status, null, null);
        }

        State(Status status, List<ParseUser> users) {
            this(status, users, null);
        }

        State(Status status, String errorMessage) {
            this(status, null, errorMessage);
        }

        State(Status status, List<ParseUser> users, String errorMessage) {
            this.status = status;
            this.users = users;
            this.errorMessage = errorMessage;
        }

        // Public getters
        public Status getStatus() {
            return status;
        }

        public List<ParseUser> getUsers() {
            return users;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public enum Status {
        LOADING, SUCCESS, ERROR
    }
}
