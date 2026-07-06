package com.saenz.checkmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class OpponentActivity extends AppCompatActivity {

    private int selectedBotElo = 920;
    private String selectedBotName = "Bot 1";

    // Referencias a las 4 cards
    private CardView card1, card2, card3, card4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opponent);

        // Vincular cards
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);

        // Botón volver
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Botón jugar
        Button btnJugar = findViewById(R.id.btnJugar);
        btnJugar.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("botElo", selectedBotElo);
            intent.putExtra("botName", selectedBotName);
            startActivity(intent);
        });
        // Listeners de selección
        card1.setOnClickListener(v -> selectBot(1, 920,  "Bot 1"));
        card2.setOnClickListener(v -> selectBot(2, 1450, "Bot 2"));
        card3.setOnClickListener(v -> selectBot(3, 1780, "Bot 3"));
        card4.setOnClickListener(v -> selectBot(4, 2020, "Bot 4"));

        // Selección inicial
        selectBot(1, 920, "Bot 1");
    }

    private void selectBot(int botNum, int elo, String name) {
        selectedBotElo  = elo;
        selectedBotName = name;

        // Resetear todas las cards al color surface
        int colorNormal   = 0xFF1E1E1E;
        int colorSelected = 0xFF1A237E;
        int strokeNormal   = 0xFF546E7A;
        int strokeSelected = 0xFF3D5AFE;

        resetCard(card1, colorNormal, strokeNormal);
        resetCard(card2, colorNormal, strokeNormal);
        resetCard(card3, colorNormal, strokeNormal);
        resetCard(card4, colorNormal, strokeNormal);

        // Destacar card seleccionada
        CardView selected = botNum == 1 ? card1 :
                botNum == 2 ? card2 :
                        botNum == 3 ? card3 : card4;
        selected.setCardBackgroundColor(colorSelected);

        // Actualizar texto del botón
        TextView btnJugar = findViewById(R.id.btnJugar);
        btnJugar.setText("⚔️ Jugar contra " + name);
    }

    private void resetCard(CardView card, int bgColor, int strokeColor) {
        card.setCardBackgroundColor(bgColor);
    }
}