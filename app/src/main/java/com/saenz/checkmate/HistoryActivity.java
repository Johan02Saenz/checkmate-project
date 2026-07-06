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
import com.saenz.checkmate.viewmodel.GameViewModel;

public class HistoryActivity extends AppCompatActivity {

    private GameViewModel viewModel;
    private GameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter
        adapter = new GameAdapter(new GameAdapter.OnGameClickListener() {
            @Override
            public void onGameClick(GameEntity game) {
                showEditDialog(game);
            }

            @Override
            public void onGameLongClick(GameEntity game) {
                showDeleteDialog(game);
            }
        });

        recyclerView.setAdapter(adapter);

        // Observar LiveData
        viewModel.getAllGames().observe(this, adapter::setGames);

        // Botón volver
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void showDeleteDialog(GameEntity game) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar partida")
                .setMessage("Esta acción es permanente. ¿Deseas continuar?")
                .setPositiveButton("Eliminar", (dialog, which) -> viewModel.delete(game))
                .setNegativeButton("Cancelar", null)
                .show();
    }

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
