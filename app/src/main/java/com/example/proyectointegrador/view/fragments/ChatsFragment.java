package com.example.proyectointegrador.view.fragments; // Paquete donde se encuentra la clase

import android.util.Log;
import android.view.LayoutInflater; // Importa LayoutInflater para inflar layouts
import android.view.View; // Importa View para manejar vistas
import android.view.ViewGroup; // Importa ViewGroup para manejar grupos de vistas
import android.os.Bundle; // Importa Bundle para manejar el estado del fragmento

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment; // Importa Fragment para crear un fragmento
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyectointegrador.adapters.MensajeAdapter;
import com.example.proyectointegrador.databinding.FragmentChatsBinding;
import com.example.proyectointegrador.viewmodel.ChatViewModel;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Objects;

// Clase que representa un fragmento para mostrar chats
public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";
    private static final String ARG_USER_ID = "otroUsuarioId";
    private FragmentChatsBinding binding;
    private ChatViewModel viewModel;
    private MensajeAdapter adapter;
    private ParseUser otherUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        setupUI();
        setupViewModel();
        loadChatUser();
        return binding.getRoot();
    }

    private void setupUI() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(binding.toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        binding.recyclerMensajes.setLayoutManager(layoutManager);
        adapter = new MensajeAdapter(ParseUser.getCurrentUser());
        binding.recyclerMensajes.setAdapter(adapter);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshMessages();
            binding.swipeRefreshLayout.setRefreshing(false);
        });
        binding.fabEnviar.setOnClickListener(v -> sendMessage());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);
    }

    private void loadChatUser() {
        otherUser = getUserFromArguments();
        if (otherUser == null) {
            showNoUserUI();
            return;
        }
        updateToolbarTitle();
        showChatUI();
        observeChatState();
    }

    private ParseUser getUserFromArguments() {
        Bundle args = getArguments();
        if (args == null || !args.containsKey(ARG_USER_ID)) {
            Log.e(TAG, "No user ID in arguments");
            return null;
        }
        String userId = args.getString(ARG_USER_ID);
        if (userId == null) return null;
        try {
            ParseUser user = ParseUser.createWithoutData(ParseUser.class, userId);
            user.fetchIfNeeded();
            return user.getUsername() != null ? user : null;
        } catch (ParseException e) {
            Log.e(TAG, "Error fetching user", e);
            return null;
        }
    }

    private void updateToolbarTitle() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Chat with " + otherUser.getUsername());
        }
    }

    private void observeChatState() {
        viewModel.getChatState().observe(getViewLifecycleOwner(), state -> {
            switch (state.getStatus()) {
                case INITIAL:
                case LOADING:
                    binding.swipeRefreshLayout.setRefreshing(true);
                    break;
                case SUCCESS:
                    binding.swipeRefreshLayout.setRefreshing(false);
                    adapter.submitList(state.getMessages());
                    if (!state.getMessages().isEmpty()) {
                        binding.recyclerMensajes.scrollToPosition(state.getMessages().size() - 1);
                    }
                    break;
                case ERROR:
                    binding.swipeRefreshLayout.setRefreshing(false);
                    binding.tvNoUserSelected.setText(state.getErrorMessage());
                    binding.tvNoUserSelected.setVisibility(View.VISIBLE);
                    break;
            }
        });
        viewModel.loadMessages(otherUser);
    }

    private void sendMessage() {
        String text = binding.etMensaje.getText().toString().trim();
        if (!text.isEmpty()) {
            viewModel.sendMessage(text, ParseUser.getCurrentUser(), otherUser);
            binding.etMensaje.setText("");
            binding.recyclerMensajes.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void showChatUI() {
        binding.tvNoUserSelected.setVisibility(View.GONE);
        binding.recyclerMensajes.setVisibility(View.VISIBLE);
        binding.etMensaje.setVisibility(View.VISIBLE);
        binding.fabEnviar.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    private void showNoUserUI() {
        binding.tvNoUserSelected.setVisibility(View.VISIBLE);
        binding.recyclerMensajes.setVisibility(View.GONE);
        binding.etMensaje.setVisibility(View.GONE);
        binding.fabEnviar.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setVisibility(View.GONE);
        if (isAdded()) {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    public void updateUser(ParseUser newUser) {
        otherUser = newUser;
        if (otherUser == null) {
            showNoUserUI();
        } else {
            updateToolbarTitle();
            showChatUI();
            observeChatState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (otherUser != null) {
            viewModel.resumePolling();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.pausePolling();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}