package com.example.proyectointegrador.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectointegrador.R;
import com.parse.ParseUser;

import java.util.List;

public class UserAdapter extends ListAdapter<ParseUser, UserAdapter.UserViewHolder> {
    private final OnUserClickListener listener;
    public interface OnUserClickListener {
        void onUserClick(ParseUser user);
    }
    // Improved DiffUtil callback with better null handling
    private static final DiffUtil.ItemCallback<ParseUser> DIFF_CALLBACK = new DiffUtil.ItemCallback<ParseUser>() {
        @Override
        public boolean areItemsTheSame(@NonNull ParseUser oldItem, @NonNull ParseUser newItem) {
            // Check if items represent the same user based on ObjectId
            try {
                return oldItem.getObjectId() != null &&
                        newItem.getObjectId() != null &&
                        oldItem.getObjectId().equals(newItem.getObjectId());
            } catch (Exception e) {
                // Fallback in case of Parse-related errors
                return false;
            }
        }

        @Override
        public boolean areContentsTheSame(@NonNull ParseUser oldItem, @NonNull ParseUser newItem) {
            // Check if the content (username) is the same
            String oldUsername = oldItem.getUsername();
            String newUsername = newItem.getUsername();

            if (oldUsername == null && newUsername == null) {
                return true;
            }
            if (oldUsername == null || newUsername == null) {
                return false;
            }
            return oldUsername.equals(newUsername);
        }
    };
    public UserAdapter(OnUserClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        ParseUser user = getItem(position);
        if (user != null) {
            holder.bind(user);
        }
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUsername;  // TextView para mostrar el nombre de usuario

        UserViewHolder(@NonNull View itemView, OnUserClickListener listener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    ParseUser user = (ParseUser) itemView.getTag();
                    if (user != null) {
                        listener.onUserClick(user);
                    }
                }
            });
        }
        void bind(ParseUser user) {
            itemView.setTag(user);
            tvUsername.setText(user.getUsername() != null ? user.getUsername() : "Unnamed User");
        }
    }

}
