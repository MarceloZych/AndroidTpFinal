package com.example.proyectointegrador.view.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectointegrador.R;
import com.example.proyectointegrador.adapters.PostAdapter;
import com.example.proyectointegrador.databinding.FragmentHomeBinding;
import com.example.proyectointegrador.view.HomeActivity;
import com.example.proyectointegrador.view.MainActivity;
import com.example.proyectointegrador.view.PostActivity;
import com.example.proyectointegrador.viewmodel.AuthViewModel;
import com.example.proyectointegrador.viewmodel.PostViewModel;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private PostViewModel postViewModel;
    private AuthViewModel authViewModel;

    public HomeFragment() {
    }
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.tools);

        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PostActivity.class);
            startActivity(intent);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postViewModel.getPosts().observe(getViewLifecycleOwner(), post -> {
            if (post != null && !post.isEmpty()) {
                Log.d("HomeFragment", "Posts obtenidos: " + post.size());
                PostAdapter adapter = new PostAdapter(post);
                binding.recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                ((HomeActivity) requireActivity()).hideProgressBar();
            } else {
                Log.d("HomeFragment", "No se encontraron posts");
                ((HomeActivity) requireActivity()).hideProgressBar();
            }
        });
        setupMenu();
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.home_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.itemHome) {
                    onLogout();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void onLogout() {
        authViewModel.logout().observe(getViewLifecycleOwner(), logoutResult -> {
            if (logoutResult != null && logoutResult) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Log.e("HomeFragment", "Error durante el logout");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}