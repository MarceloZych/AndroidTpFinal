package com.example.proyectointegrador.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectointegrador.R;
import com.squareup.picasso.Picasso;

import java.util.*;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> imageUrls;
    private Context context;

    public ImageAdapter(List<String> imageUrls, Context context) {
        this.imageUrls = imageUrls;
        this.context = context;
    }

    @Override
    public ImageViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Picasso.get().load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
