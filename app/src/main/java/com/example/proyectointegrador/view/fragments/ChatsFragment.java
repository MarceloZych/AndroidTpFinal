package com.example.proyectointegrador.view.fragments; // Paquete donde se encuentra la clase

import android.app.AppComponentFactory;
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

    private FragmentChatsBinding binding;
    private ChatViewModel chatViewModel;
    private MensajeAdapter adapter;
    private ParseUser otroUsuario;
    private boolean isObservingMessages = false;

    // Método llamado para crear la vista del fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (binding.toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        }

        if (binding.swipeRefreshLayout2 != null) {
            binding.swipeRefreshLayout2.setOnRefreshListener(() -> {
                if (chatViewModel != null) {
                    chatViewModel.refreshMessages();
                    binding.swipeRefreshLayout2.setRefreshing(false);
                }
            });
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        binding.recyclerMensajes2.setLayoutManager(layoutManager);
        adapter = new MensajeAdapter(new ArrayList<>(), ParseUser.getCurrentUser());

        chatViewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);

        otroUsuario = obtenerOtroUsuario();
        if (otroUsuario == null) {
            Log.d("ChatsFragment", "No hay usuario seleccionado");
            mostrarInterfazSinUsuario();
            return view;
        }
        if (otroUsuario != null/*binding.toolbar != null && ((AppCompatActivity) requireActivity()).getSupportActionBar() != null*/) {

            Objects.requireNonNull((AppCompatActivity) requireActivity()).getSupportActionBar()
                    .setTitle("Chat con " + otroUsuario.getUsername());
        } else {
            Log.e("ChatsFragment", "Error: otroUsuario es null");
            mostrarInterfazSinUsuario();
            return view;
        }

        mostrarInterfazConUsuario();
        observareMensajes();
        binding.fabEnviar2.setOnClickListener(v -> enviarMensaje());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (otroUsuario != null && !isObservingMessages) observareMensajes();

        if (chatViewModel != null) chatViewModel.resumePolling();
    }

    @Override
    public void onPause() {
        super.onPause();
        isObservingMessages = false;
        if (chatViewModel != null) chatViewModel.pausePolling();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void observareMensajes() {
        if (otroUsuario != null) {
            isObservingMessages = true;
            chatViewModel.getMensajes(otroUsuario).observe(getViewLifecycleOwner(), mensajes -> {
                        Log.d("ChatFragment", "Mensajes cargados: " + mensajes.size());
                        adapter.setMensajes(mensajes);
                        if (!mensajes.isEmpty())
                            binding.recyclerMensajes2.scrollToPosition(mensajes.size() - 1);
                    }
            );
        }
    }

    private ParseUser obtenerOtroUsuario() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("otroUsuarioId")) {
            String userId = bundle.getString("otroUsuarioId");
            if (userId == null) return null;

            ParseUser user = ParseUser.createWithoutData(ParseUser.class, userId);
            try {
                user.fetchIfNeeded();
                if (user.getUsername() == null) {
                    Log.e("ChatsFragment", "Error: Usuario no tiene nombre de usuario");
                    return null;
                }
                Log.d("ChatsFragment", "Usuario cargado: " + user.getUsername());
                return user;
            } catch (ParseException e) {
                Log.e("ChatFragment", "Error al cargar el usuario", e);
            }
        } else {
            Log.e("ChatsFragment", "No se encontró 'otroUsuarioId' en los argumentos");
        }
        return null;
    }


    public void updateUser(ParseUser newUser) {
        this.otroUsuario = newUser;

        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("Chat con: " + otroUsuario.getUsername());
        }
        if (otroUsuario == null) {
            mostrarInterfazSinUsuario();
            return;
        }
        mostrarInterfazConUsuario();
        isObservingMessages = false;
        observareMensajes();
    }

    private void enviarMensaje() {
        String texto = binding.etMensaje.getText().toString().trim();
        if (!texto.isEmpty()) {
            if (chatViewModel != null) {
                chatViewModel.enviarMensaje(texto, ParseUser.getCurrentUser(), otroUsuario);
                binding.etMensaje.setText("");
                binding.recyclerMensajes2.post(() ->
                        binding.recyclerMensajes2.scrollToPosition(adapter.getItemCount()));
            }
        }
    }

    private void mostrarInterfazSinUsuario() {
        if (getView() != null) {
            getView().post(() -> {
                binding.recyclerMensajes2.setVisibility(View.GONE);
                binding.etMensaje.setVisibility(View.GONE);
                binding.fabEnviar2.setVisibility(View.GONE);
                if (binding.swipeRefreshLayout2 != null) {
                    binding.swipeRefreshLayout2.setVisibility(View.GONE);
                }
                binding.tvNoUserSelected2.setVisibility(View.GONE);

                if (isAdded()) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    private void mostrarInterfazConUsuario() {
        binding.tvNoUserSelected2.setVisibility(View.GONE);
        binding.recyclerMensajes2.setVisibility(View.VISIBLE);
        binding.etMensaje.setVisibility(View.VISIBLE);
        binding.fabEnviar2.setVisibility(View.VISIBLE);
        if (binding.swipeRefreshLayout2 != null) {
            binding.swipeRefreshLayout2.setVisibility(View.VISIBLE);
        }
    }
}