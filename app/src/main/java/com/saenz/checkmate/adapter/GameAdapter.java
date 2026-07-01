package com.saenz.checkmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saenz.checkmate.R;
import com.saenz.checkmate.database.GameEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<GameEntity> games = new ArrayList<>();
    private final OnGameClickListener listener;

    // Interfaz para comunicar clicks al Activity
    public interface OnGameClickListener {
        void onGameClick(GameEntity game);
        void onGameLongClick(GameEntity game);
    }

    public GameAdapter(OnGameClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        GameEntity current = games.get(position);

        holder.textResult.setText(current.result);
        holder.textBotElo.setText("ELO Bot: " + current.botElo);
        holder.textPlayerColor.setText("Color: " + current.playerColor);
        holder.textDuration.setText(formatDuration(current.durationSeconds));
        holder.textDate.setText(formatDate(current.datePlayed));

        holder.itemView.setOnClickListener(v -> listener.onGameClick(current));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onGameLongClick(current);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    // Llamado cuando LiveData emite nuevos datos
    public void setGames(List<GameEntity> games) {
        this.games = games;
        notifyDataSetChanged();
    }

    private String formatDuration(long seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // ── ViewHolder ──────────────────────────────────────────
    // Guarda referencias a las vistas para no buscarlas en cada scroll
    static class GameViewHolder extends RecyclerView.ViewHolder {

        TextView textResult;
        TextView textBotElo;
        TextView textPlayerColor;
        TextView textDuration;
        TextView textDate;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            textResult      = itemView.findViewById(R.id.text_result);
            textBotElo      = itemView.findViewById(R.id.text_bot_elo);
            textPlayerColor = itemView.findViewById(R.id.text_player_color);
            textDuration    = itemView.findViewById(R.id.text_duration);
            textDate        = itemView.findViewById(R.id.text_date);
        }
    }
}