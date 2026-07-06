package com.saenz.checkmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton;
import com.saenz.checkmate.notification.NotificationScheduler;
import com.saenz.checkmate.viewmodel.GameViewModel;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private GameViewModel viewModel;

    // Stats
    private TextView tvTotalPartidas;
    private TextView tvPartidasSemana;
    private TextView tvVictorias;
    private TextView tvRacha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vincular vistas
        tvTotalPartidas  = findViewById(R.id.tvTotalPartidas);
        tvPartidasSemana = findViewById(R.id.tvPartidasSemana);
        tvVictorias      = findViewById(R.id.tvVictorias);
        tvRacha          = findViewById(R.id.tvRacha);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // Observar LiveData y calcular estadísticas en tiempo real
        viewModel.getAllGames().observe(this, games -> {
            if (games == null || games.isEmpty()) {
                tvTotalPartidas.setText("0");
                tvPartidasSemana.setText("↑ 0 esta semana");
                tvVictorias.setText("0%");
                tvRacha.setText("0");
                return;
            }

            int total = games.size();
            tvTotalPartidas.setText(String.valueOf(total));

            // Partidas de los últimos 7 días
            long hace7dias = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
            long recientes = games.stream()
                    .filter(g -> g.datePlayed >= hace7dias)
                    .count();
            tvPartidasSemana.setText("↑ " + recientes + " esta semana");

            // Porcentaje de victorias
            long victorias = games.stream()
                    .filter(g -> "VICTORY".equals(g.result))
                    .count();
            int porcentaje = (int) ((victorias * 100) / total);
            tvVictorias.setText(porcentaje + "%");

            // Racha actual de victorias consecutivas
            AtomicInteger racha = new AtomicInteger(0);
            for (int i = 0; i < games.size(); i++) {
                if ("VICTORY".equals(games.get(i).result)) {
                    racha.incrementAndGet();
                } else {
                    break;
                }
            }
            tvRacha.setText(String.valueOf(racha.get()));
        });

        // Botones de acción
        MaterialButton btnNuevaPartida = findViewById(R.id.btnNuevaPartida);
        MaterialButton btnContinuar    = findViewById(R.id.btnContinuar);

        btnNuevaPartida.setOnClickListener(v -> {
            startActivity(new Intent(this, OpponentActivity.class));
        });

        btnContinuar.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });



        // Notificación diaria
        NotificationScheduler.scheduleDailyNotification(this);
    }
}