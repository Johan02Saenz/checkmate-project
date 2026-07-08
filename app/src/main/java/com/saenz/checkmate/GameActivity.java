package com.saenz.checkmate;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.saenz.checkmate.chess.ChessBoard;
import com.saenz.checkmate.database.GameEntity;
import com.saenz.checkmate.viewmodel.GameViewModel;

public class GameActivity extends AppCompatActivity {

    // Colores del tablero
    private static final int LIGHT_COLOR    = 0xFFF0D9B5;
    private static final int DARK_COLOR     = 0xFFB58863;
    private static final int WHITE_PIECE_COLOR = 0xFFFFFFFF; // color de las piezas blancas
    private static final int BLACK_PIECE_COLOR = 0xFF000000; // color de las piezas negras
    private static final int SELECTED_COLOR = 0xFF3D5AFE; // casilla seleccionada
    private static final int LAST_MOVE_COLOR = 0xFF8D6E63; // resalta el último movimiento

    private TextView tvTimer;
    private TextView tvTurn;
    private TextView tvBotName;
    private TextView tvBotElo;
    private TextView tvMoves;

    private GameViewModel viewModel;
    private final Handler handler = new Handler();
    private int secondsElapsed = 0;
    private boolean running = true;

    private String botName;
    private int botElo;
    private static final int VALID_MOVE_COLOR    = 0xFF4A7A5A; // destino vacío
    private static final int VALID_CAPTURE_COLOR = 0xFF8B3A3A; // destino con captura
    private java.util.List<int[]> validDestinations = new java.util.ArrayList<>();

    // Motor simple de ajedrez (sin Stockfish)
    private ChessBoard chessBoard;
    private final TextView[][] cellViews = new TextView[8][8];
    private final StringBuilder moveHistory = new StringBuilder();
    private int moveNumber = 1;
    private boolean boardLocked = false; // bloquea toques mientras "piensa" el bot
    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Recibir datos del bot seleccionado
        botName = getIntent().getStringExtra("botName");
        botElo  = getIntent().getIntExtra("botElo", 1200);

        // Vincular vistas
        tvBotName = findViewById(R.id.tvBotName);
        tvBotElo  = findViewById(R.id.tvBotElo);
        tvTimer   = findViewById(R.id.tvTimer);
        tvTurn    = findViewById(R.id.tvTurn);
        tvMoves   = findViewById(R.id.tvMoves);

        tvBotName.setText(botName);
        tvBotElo.setText("ELO " + botElo);

        // ViewModel para guardar partida al finalizar
        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // Motor del tablero
        chessBoard = new ChessBoard();

        // Iniciar cronómetro
        startTimer();

        // Construir tablero visual y engancharlo con la lógica
        buildBoard();
        refreshBoard();
        updateTurnLabel();

        // Botón pausar
        findViewById(R.id.btnPause).setOnClickListener(v -> {
            running = !running;
            TextView btnPause = findViewById(R.id.btnPause);
            btnPause.setText(running ? "⏸ Pausar" : "▶ Reanudar");
        });

        // Botón rendirse
        findViewById(R.id.btnResign).setOnClickListener(v -> showResignDialog());
    }

    // Cronómetro que actualiza cada segundo
    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    secondsElapsed++;
                    int min = secondsElapsed / 60;
                    int sec = secondsElapsed % 60;
                    tvTimer.setText(String.format("%02d:%02d", min, sec));
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    // Construir las 64 casillas del tablero y enganchar los clicks a ChessBoard
    private void buildBoard() {
        GridLayout board = findViewById(R.id.chessBoard);
        board.removeAllViews();
        board.setColumnCount(8);
        board.setRowCount(8);

        int cellSize = calculateCellSize();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                TextView cell = new TextView(this);
                cell.setTextSize(cellSize / getResources().getDisplayMetrics().density * 0.5f);
                cell.setGravity(Gravity.CENTER);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width  = cellSize;
                params.height = cellSize;
                params.rowSpec    = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                cell.setLayoutParams(params);

                final int r = row;
                final int c = col;
                cell.setOnClickListener(v -> onCellTapped(r, c));

                cellViews[row][col] = cell;
                board.addView(cell);
            }
        }
    }

    // Calcula el tamaño de cada casilla según el ancho real de la pantalla,
// dejando un pequeño margen a los lados
    private int calculateCellSize() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int margin = dpToPx(16); // 8dp de padding a cada lado del contenedor
        return (screenWidth - margin) / 8;
    }

    // Se llama cuando el jugador toca una casilla
    private void onCellTapped(int row, int col) {
        if (gameOver || boardLocked) return;
        if (!chessBoard.isWhiteTurn()) return;

        ChessBoard.TapResult result = chessBoard.handleTap(row, col);

        // Actualiza qué casillas se resaltan como "movimiento válido"
        if (result == ChessBoard.TapResult.SELECTED) {
            validDestinations = chessBoard.getLegalDestinations(chessBoard.getSelectedRow(), chessBoard.getSelectedCol());
        } else {
            validDestinations.clear();
        }

        refreshBoard();

        if (result == ChessBoard.TapResult.MOVED) {
            registerMove(chessBoard.getLastMove(), true);
            if (checkGameOver()) return;
            updateTurnLabel();
            boardLocked = true;
            handler.postDelayed(this::playBotMove, 500);
        }
    }
    // Movimiento del bot (negras) — motor simple, sin Stockfish
    private void playBotMove() {
        if (gameOver) return;
        validDestinations.clear();

        ChessBoard.Move move = chessBoard.makeSimpleMove(false);
        refreshBoard();

        if (move != null) {
            registerMove(move, false);
        }

        boardLocked = false;

        if (checkGameOver()) return;
        updateTurnLabel();
    }

    // Redibuja las 64 casillas según el estado actual de ChessBoard
    private static final int CHECK_COLOR = 0xFFD32F2F; // rojo de alerta

    private void refreshBoard() {
        ChessBoard.Move last = chessBoard.getLastMove();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                TextView cell = cellViews[row][col];
                cell.setText(chessBoard.getSymbol(row, col));
                String pieceCode = chessBoard.getPiece(row, col);
                if (pieceCode != null && pieceCode.startsWith("w")) {
                    cell.setTextColor(WHITE_PIECE_COLOR);
                } else if (pieceCode != null && pieceCode.startsWith("b")) {
                    cell.setTextColor(BLACK_PIECE_COLOR);
                }
                boolean isSelected = row == chessBoard.getSelectedRow() && col == chessBoard.getSelectedCol();
                boolean isLastMove = last != null &&
                        ((row == last.fromRow && col == last.fromCol) ||
                                (row == last.toRow && col == last.toCol));
                boolean isLight = (row + col) % 2 == 0;

                String piece = chessBoard.getPiece(row, col);
                boolean isCheckedKing =
                        ("wK".equals(piece) && chessBoard.isInCheck(true)) ||
                                ("bK".equals(piece) && chessBoard.isInCheck(false));

                // ¿Es esta casilla un destino válido de la pieza seleccionada?
                boolean isValidDestination = false;
                for (int[] dest : validDestinations) {
                    if (dest[0] == row && dest[1] == col) {
                        isValidDestination = true;
                        break;
                    }
                }
                boolean isCapture = piece != null && !piece.isEmpty(); // si hay pieza enemiga ahí

                if (isCheckedKing) {
                    cell.setBackgroundColor(CHECK_COLOR);
                } else if (isSelected) {
                    cell.setBackgroundColor(SELECTED_COLOR);
                } else if (isValidDestination) {
                    cell.setBackgroundColor(isCapture ? VALID_CAPTURE_COLOR : VALID_MOVE_COLOR);
                } else if (isLastMove) {
                    cell.setBackgroundColor(LAST_MOVE_COLOR);
                } else {
                    cell.setBackgroundColor(isLight ? LIGHT_COLOR : DARK_COLOR);
                }
            }
        }
    }

    // Actualiza el texto "Tu turno" / "Turno del rival"
    private void updateTurnLabel() {
        boolean whiteToMove = chessBoard.isWhiteTurn();
        boolean inCheck = chessBoard.isInCheck(whiteToMove);

        if (whiteToMove) {
            tvTurn.setText(inCheck ? "👤 Tu turno — ¡JAQUE!" : "👤 Tu turno");
        } else {
            tvTurn.setText(inCheck ? "🤖 " + botName + " en jaque" : "🤖 Turno de " + botName);
        }
    }

    // Agrega el movimiento a la notación mostrada en "ÚLTIMAS JUGADAS"
    private void registerMove(ChessBoard.Move move, boolean whiteMoved) {
        if (move == null) return;

        String notation = toAlgebraic(move);

        if (whiteMoved) {
            moveHistory.append(moveNumber).append(". ").append(notation).append("  ");
        } else {
            moveHistory.append(notation).append("   ");
            moveNumber++;
        }
        tvMoves.setText(moveHistory.toString());
    }

    // Notación simple: letra de pieza (vacío para peón) + casilla destino + 'x' si captura
    private String toAlgebraic(ChessBoard.Move move) {
        String letter;
        switch (move.piece.substring(1)) {
            case "N": letter = "N"; break;
            case "B": letter = "B"; break;
            case "R": letter = "R"; break;
            case "Q": letter = "Q"; break;
            case "K": letter = "K"; break;
            default:  letter = ""; break; // peón
        }
        char file = (char) ('a' + move.toCol);
        int rank = 8 - move.toRow;
        return letter + (move.capture ? "x" : "") + file + rank;
    }

    // Revisa si algún rey fue capturado y termina la partida
    private boolean checkGameOver() {
        boolean whiteAlive = chessBoard.isKingAlive(true);
        boolean blackAlive = chessBoard.isKingAlive(false);

        if (whiteAlive && blackAlive) return false;

        gameOver = true;
        running = false;

        if (whiteAlive) {
            showEndDialog("🏆 ¡Ganaste!", "Capturaste al rey rival.", "WIN");
        } else {
            showEndDialog("💀 Derrota", "Tu rey fue capturado.", "DEFEAT");
        }
        return true;
    }

    private void showEndDialog(String title, String message, String result) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    saveGame(result);
                    finish();
                })
                .show();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // Diálogo de confirmación para rendirse
    private void showResignDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("🏳 ¿Rendirse?")
                .setMessage("¿Seguro que quieres abandonar la partida? Esta acción no se puede deshacer.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Sí, rendirse", (dialog, which) -> {
                    saveGame("DEFEAT");
                    finish();
                })
                .show();
    }

    // Guardar partida en Room al finalizar
    private void saveGame(String result) {
        GameEntity game = new GameEntity();
        game.result          = result;
        game.botElo          = botElo;
        game.playerColor     = "WHITE";
        game.durationSeconds = secondsElapsed;
        game.datePlayed      = System.currentTimeMillis();
        game.notes           = "";
        viewModel.insert(game);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
