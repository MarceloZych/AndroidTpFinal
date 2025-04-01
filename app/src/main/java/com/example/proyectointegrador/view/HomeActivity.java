package com.example.proyectointegrador.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyectointegrador.R;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.proyectointegrador.databinding.ActivityHomeBinding;
import com.example.proyectointegrador.view.fragments.FiltrosFragment;
import com.example.proyectointegrador.view.fragments.HomeFragment;
import com.example.proyectointegrador.view.fragments.PerfilFragment;
import com.example.proyectointegrador.view.fragments.UserFragment;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding; // Declara una variable para el binding de la actividad

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Llama al método onCreate de la clase padre

        // Infla el layout de la actividad utilizando View Binding
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Establece el contenido de la actividad

        // Agrega un ProgressBar que se mostrará mientras se cargan datos
        LayoutInflater inflater = LayoutInflater.from(this);
        View progressBarLayout = inflater.inflate(R.layout.progress_layout, binding.mainCont, false);
        binding.mainCont.addView(progressBarLayout); // Añade el ProgressBar al contenedor principal
        // Configura el listener para manejar los elementos seleccionados en la barra de navegación inferior
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Verifica qué elemento fue seleccionado y abre el fragmento correspondiente
                if (item.getItemId() == R.id.itemHome) {
                    openFragment(HomeFragment.newInstance()); // Abre HomeFragment
                } else if (item.getItemId() == R.id.itemChats) {
                    openFragment(new UserFragment()); // Abre ChatsFragment
                } else if (item.getItemId() == R.id.itemPerfil) {
                    openFragment(new PerfilFragment()); // Abre PerfilFragment
                } else if (item.getItemId() == R.id.itemFiltros) {
                    openFragment(new FiltrosFragment()); // Abre FiltrosFragment
                }
                return true; // Indica que el evento fue manejado correctamente
            }
        });

        // Abre el fragmento inicial al cargar la actividad (HomeFragment)
        openFragment(HomeFragment.newInstance());
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager(); // Obtiene el FragmentManager para gestionar los fragmentos
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // Inicia una nueva transacción de fragmentos
        fragmentTransaction.replace(R.id.container, fragment); // Reemplaza el contenido del contenedor con el nuevo fragmento
        fragmentTransaction.commit(); // Confirma la transacción, aplicando los cambios
    }

    public void hideProgressBar() {
        View progressBarLayout = findViewById(R.id.progress_layout); // Busca el ProgressBar por su ID
        if (progressBarLayout != null) {
            progressBarLayout.setVisibility(View.GONE); // Oculta el ProgressBar si se encuentra en la vista
        }
    }
}