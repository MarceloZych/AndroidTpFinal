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
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectointegrador.R;
import com.example.proyectointegrador.model.Mensaje;
import com.example.proyectointegrador.util.MensajeDiffCallback;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.ViewHolder> {

    private final List<Mensaje> mensajes;
    private final ParseUser currentUser;

    public MensajeAdapter(List<Mensaje> mensajes, ParseUser currentUser) {
        this.mensajes = mensajes;
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
    public int getItemCount() {
        return mensajes.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMensajes (List<Mensaje> nuevosMensajes) {
        MensajeDiffCallback diffCallback = new MensajeDiffCallback(mensajes, nuevosMensajes);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.mensajes.clear();
        this.mensajes.addAll(nuevosMensajes);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        boolean esRemitente = mensaje.getRemitente().getObjectId().equals(currentUser.getObjectId());
        holder.bind(mensaje, esRemitente);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMensajeEnviado;
        private final TextView tvMensajeRecibido;
        private final TextView tvFechaEnviado;
        private final TextView tvFechaRecibido;
        private final Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensajeEnviado = itemView.findViewById(R.id.tvMensajeEnviado);
            tvMensajeRecibido = itemView.findViewById(R.id.tvMensajeRecibido);
            tvFechaEnviado = itemView.findViewById(R.id.tvFechaEnviado);
            tvFechaRecibido = itemView.findViewById(R.id.tvFechaRecibido);
            ConstraintLayout layoutMensaje = itemView.findViewById(R.id.mensajeContainer);
            context = itemView.getContext();
        }
        @SuppressLint("ResourceAsColor")
        public void bind(Mensaje mensaje, boolean esRemitente) {
            tvMensajeEnviado.setVisibility(View.GONE);
            tvMensajeRecibido.setVisibility(View.GONE);
            tvFechaEnviado.setVisibility(View.GONE);
            tvFechaRecibido.setVisibility(View.GONE);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/mm/yyyy", Locale.getDefault());
            String fechaHora = sdf.format(mensaje.getFecha());

            boolean isDarkMode = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
            int textColor = isDarkMode ? R.color.white : R.color.black;

            if (esRemitente) {
                tvMensajeEnviado.setVisibility(View.VISIBLE);
                tvFechaEnviado.setVisibility(View.VISIBLE);
                tvMensajeEnviado.setText(mensaje.getTexto());
                tvFechaEnviado.setText(fechaHora);
                tvFechaEnviado.setTextColor(textColor);
            } else {
                tvMensajeRecibido.setVisibility(View.VISIBLE);
                tvFechaRecibido.setVisibility(View.VISIBLE);
                tvMensajeRecibido.setText(mensaje.getTexto());
                tvFechaRecibido.setText(fechaHora);
                tvFechaRecibido.setTextColor(textColor);
            }
        }
    }
}
