package com.example.proyectointegrador.view.fragments;

import static com.example.proyectointegrador.viewmodel.ChatViewModel.Status.*;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectointegrador.R;
import com.example.proyectointegrador.adapters.UserAdapter;
import com.example.proyectointegrador.databinding.FragmentUserBinding;
import com.example.proyectointegrador.providers.AuthProvider;
import com.example.proyectointegrador.viewmodel.UsersViewModel;
import com.parse.ParseUser;

public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    private UserAdapter usersAdapter;
    private AuthProvider authProvider;
    private UsersViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserBinding.inflate(inflater, container, false);

        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        return binding.getRoot();
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(binding.toolbar);
        activity.getSupportActionBar().setTitle("Select User");
    }

    private void setupRecyclerView() {
        binding.recyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        usersAdapter = new UserAdapter(this::navigateToChat);
        binding.recyclerUsers.setAdapter(usersAdapter);
    }

    private void setupViewModel() {
        authProvider = new AuthProvider();
        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        viewModel.init(authProvider);

        viewModel.getUsersState().observe(getViewLifecycleOwner(), state -> {
            switch (state.getStatus()) {
                case LOADING:
                    showLoading();
                    break;
                case SUCCESS:
                    usersAdapter.submitList(state.getUsers());
                    showUsers(state.getUsers().isEmpty());
                    break;
                case ERROR:
                    showError(state.getErrorMessage());
                    break;
            }
        });
    }

    private void navigateToChat(ParseUser user) {
        ChatsFragment existingFragment = (ChatsFragment) getParentFragmentManager()
                .findFragmentByTag("CHATS_FRAGMENT");

        if (existingFragment == null) {
            ChatsFragment chatFragment = new ChatsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("otroUsuarioId", user.getObjectId());
            chatFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, chatFragment, "CHATS_FRAGMENT")
                    .addToBackStack(null)
                    .commit();
        } else {
            existingFragment.updateUser(user);
        }
    }

    private void showLoading() {
        binding.tvNoUsers.setText("Loading users...");
        binding.tvNoUsers.setVisibility(View.VISIBLE);
        binding.recyclerUsers.setVisibility(View.GONE);
    }

    private void showUsers(boolean isEmpty) {
        binding.recyclerUsers.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.tvNoUsers.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (isEmpty) binding.tvNoUsers.setText("No users available");
    }

    private void showError(String message) {
        binding.recyclerUsers.setVisibility(View.GONE);
        binding.tvNoUsers.setVisibility(View.VISIBLE);
        binding.tvNoUsers.setText(message != null ? message : "Error loading users");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}