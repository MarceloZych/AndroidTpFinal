package com.example.proyectointegrador.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectointegrador.R;
import com.example.proyectointegrador.model.Mensaje;
import com.example.proyectointegrador.util.MensajeDiffCallback;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MensajeAdapter extends ListAdapter<Mensaje, MensajeAdapter.ViewHolder> {
    private final ParseUser currentUser;

    private static final DiffUtil.ItemCallback<Mensaje> DIFF_CALLBACK = new DiffUtil.ItemCallback<Mensaje>() {
        @Override
        public boolean areItemsTheSame(@NonNull Mensaje oldItem, @NonNull Mensaje newItem) {
            return oldItem.getObjectId().equals(newItem.getObjectId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Mensaje oldItem, @NonNull Mensaje newItem) {
            // Handle null cases for texto and fecha
            String oldText = oldItem.getTexto() != null ? oldItem.getTexto() : "";
            String newText = newItem.getTexto() != null ? newItem.getTexto() : "";
            Date oldDate = oldItem.getFecha() != null ? oldItem.getFecha() : new Date(0);
            Date newDate = newItem.getFecha() != null ? newItem.getFecha() : new Date(0);
            return oldText.equals(newText) && oldDate.equals(newDate);
        }
    };

    public MensajeAdapter(ParseUser currentUser) {
        super(DIFF_CALLBACK);
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mensaje, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mensaje mensaje = getItem(position);
        boolean isSender = mensaje.getRemitente() != null &&
                mensaje.getRemitente().getObjectId().equals(currentUser.getObjectId());
        holder.bind(mensaje, isSender);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMensajeEnviado;
        private final TextView tvMensajeRecibido;
        private final TextView tvFechaEnviado;
        private final TextView tvFechaRecibido;
        private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensajeEnviado = itemView.findViewById(R.id.tvMensajeEnviado);
            tvMensajeRecibido = itemView.findViewById(R.id.tvMensajeRecibido);
            tvFechaEnviado = itemView.findViewById(R.id.tvFechaEnviado);
            tvFechaRecibido = itemView.findViewById(R.id.tvFechaRecibido);
        }
        void bind(Mensaje mensaje, boolean isSender) {
            tvMensajeEnviado.setVisibility(isSender ? View.VISIBLE : View.GONE);
            tvMensajeRecibido.setVisibility(isSender ? View.GONE : View.VISIBLE);
            tvFechaEnviado.setVisibility(isSender ? View.VISIBLE : View.GONE);
            tvFechaRecibido.setVisibility(isSender ? View.GONE : View.VISIBLE);

            String texto = mensaje.getTexto() != null ? mensaje.getTexto() : "";
            String fechaHora = mensaje.getFecha() != null ? sdf.format(mensaje.getFecha()) : "Unknown";

            if (isSender) {
                tvMensajeEnviado.setText(texto);
                tvFechaEnviado.setText(fechaHora);
            } else {
                tvMensajeRecibido.setText(texto);
                tvFechaRecibido.setText(fechaHora);
            }
        }
    }
}
