package com.example.proyectointegrador.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectointegrador.R;
import com.parse.ParseUser;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private final List<ParseUser> users;  // Lista de usuarios a mostrar
    private final OnUserClickListener listener;  // Listener para manejar clics en usuarios

    public interface OnUserClickListener {
        void onUserClick(ParseUser user);
    }
    public UserAdapter(List<ParseUser> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
        Log.d("UsersAdapter", "Adapter creado con " + (users != null ? users.size() : 0) + " usuarios");
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout del item de usuario
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        ParseUser user = users.get(position);
        Log.d("UsersAdapter", "Binding usuario en posición " + position + ": " + user.getUsername());
        holder.bind(user);  // Vincular los datos del usuario al ViewHolder
    }
    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }
    class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUsername;  // TextView para mostrar el nombre de usuario

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);  // Encontrar el TextView en el layout
        }
        void bind(ParseUser user) {
            String username = user.getUsername();
            // Mostrar el nombre de usuario o un texto por defecto si es nulo
            tvUsername.setText(username != null ? username : "Usuario sin nombre");

            // Configurar el listener de clic para el elemento completo
            itemView.setOnClickListener(v -> {
                Log.d("UsersAdapter", "Click en usuario: " + user.getUsername());
                if (listener != null) {
                    listener.onUserClick(user);  // Notificar al listener del clic
                }
            });
        }
    }

}
