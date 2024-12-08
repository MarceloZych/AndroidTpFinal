package com.example.proyectointegrador.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.example.proyectointegrador.R;
import com.example.proyectointegrador.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment {
    private FragmentPerfilBinding binding;
    private ActivityResultLauncher<Intent> galleryLauncher;

    public PerfilFragment() {}

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstantState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        setupMenu();
        setupToolbar();
        displayUserInfo();
        setupGalleryLauncher();
        setupProfileImageClick();

        return binding.getRoot();
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.perfil_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.) {
                    return false;
                }
            }
        });
    }
    private void setupToolbar() {
    }
    private void displayUserInfo() {
    }
    private void setupGalleryLauncher() {
    }
    private void setupProfileImageClick() {
    }
}