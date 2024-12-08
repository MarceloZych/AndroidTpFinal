package com.example.proyectointegrador.view;

import android.os.Bundle;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyectointegrador.databinding.ActivityRegisterBinding;
import com.example.proyectointegrador.model.User;
import com.example.proyectointegrador.util.Validaciones;
import com.example.proyectointegrador.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        viewModel.getRegisterResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                showToast(result);
            }
        });
        manejarEventos();
    }

    private void manejarEventos() {
        binding.circuloBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarRegistro();
            }
        });
    }

    private void realizarRegistro() {
        String usuario = binding.tietUsuario.getText().toString().trim();
        String email = binding.tietMail.getText().toString().trim();
        String password1 = binding.etPassword1.getText().toString().trim();
        String password2 = binding.etPassword2.getText().toString().trim();

        if (!Validaciones.validarUsuario(usuario)) {
            showToast("Usuario incorrecto");
            return;
        }
        if (!Validaciones.validarCorreo(email)) {
            showToast("Email incorrecto");
            return;
        }

        String passError = Validaciones.ValidarContrasena(password1, password2);
        if (passError != null) {
            showToast(passError);
            return;
        }

        User user = new User(usuario, email, password1);
        viewModel.register(user);
    }

    private void showToast (String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}