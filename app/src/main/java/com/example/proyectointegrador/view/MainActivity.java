package com.example.proyectointegrador.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyectointegrador.R;
import com.example.proyectointegrador.databinding.ActivityMainBinding;
import com.example.proyectointegrador.util.Validaciones;
import com.example.proyectointegrador.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        manejarEventos();
    }

    private void manejarEventos() {
        binding.tvRegistrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        binding.btLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = binding.etUsuario.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();

                if(!Validaciones.validarCorreo(email))
                {
                    showToast("Email incorrecto");
                    return;
                }

                if (!Validaciones.controlarPassword(password))
                {
                    showToast("Contraseña incorrecta");
                    return;
                }
                //Observa el resultado del login
                viewModel.login(email, password).observe(MainActivity.this, loginSuccessful -> {
                    if (loginSuccessful) {
                        Intent intent = new Intent(MainActivity.this, PostActivity.class);
                        startActivity(intent);
                    } else {
                        showToast("Login fallido");
                    }
                });
            }
        });
    }
    private void showToast(String message)
    {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        limpiarCampos();
    }
    private void limpiarCampos()
    {
        binding.etUsuario.setText("");
        binding.etPassword.setText("");
    }

    public static class HomeActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_home);
        }
    }
}