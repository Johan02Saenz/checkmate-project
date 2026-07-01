package com.saenz.checkmate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saenz.checkmate.adapter.GameAdapter;
import com.saenz.checkmate.database.GameEntity;
import com.saenz.checkmate.notification.NotificationScheduler;
import com.saenz.checkmate.viewmodel.GameViewModel;

public class MainActivity extends AppCompatActivity {

    private GameViewModel viewModel;
    private GameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Conectar ViewModel
        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // 2. Configurar RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Conectar Adapter con sus listeners de click
        adapter = new GameAdapter(new GameAdapter.OnGameClickListener() {

            @Override
            public void onGameClick(GameEntity game) {
                // Por ahora solo muestra el diálogo de edición de notas
                showEditDialog(game);
            }

            @Override
            public void onGameLongClick(GameEntity game) {
                // Diálogo de confirmación de eliminación
                showDeleteDialog(game);
            }
        });

        recyclerView.setAdapter(adapter);

        // 4. Observar LiveData — se actualiza automáticamente
        viewModel.getAllGames().observe(this, games -> adapter.setGames(games));

        // 5. Botón para insertar partida de prueba
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> insertTestGame());

        NotificationScheduler.scheduleDailyNotification(this);
        NotificationScheduler.scheduleDailyNotification(this);
    }

    // Inserta una partida de ejemplo para probar el CRUD
    private void insertTestGame() {
        GameEntity game = new GameEntity();
        game.result = "VICTORY";
        game.botElo = 10;
        game.playerColor = "WHITE";
        game.durationSeconds = 342;
        game.datePlayed = System.currentTimeMillis();
        game.notes = "";
        viewModel.insert(game);
    }

    // Diálogo de confirmación de eliminación (Punto 3 del entregable)
    private void showDeleteDialog(GameEntity game) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar partida")
                .setMessage("Esta acción es permanente. ¿Deseas continuar?")
                .setPositiveButton("Eliminar", (dialog, which) -> viewModel.delete(game))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Diálogo simple de edición de notas (Punto 3 del entregable)
    private void showEditDialog(GameEntity game) {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setText(game.notes);
        input.setHint("Escribe tu análisis...");

        new MaterialAlertDialogBuilder(this)
                .setTitle("Editar nota")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    game.notes = input.getText().toString();
                    viewModel.update(game);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}