package com.saenz.checkmate.chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChessBoard {

    // Resultado de un toque en el tablero, para que la UI sepa qué pasó
    public enum TapResult {
        SELECTED,    // se seleccionó una pieza propia
        DESELECTED,  // se deseleccionó (toque inválido o repetido)
        MOVED        // se realizó un movimiento válido
    }

    // Representa un movimiento (origen -> destino)
    public static class Move {
        public final int fromRow, fromCol, toRow, toCol;
        public final String piece;
        public final boolean capture;

        Move(int fromRow, int fromCol, int toRow, int toCol, String piece, boolean capture) {
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
            this.piece = piece;
            this.capture = capture;
        }
    }

    // Estado del tablero: null = casilla vacía
    private String[][] board = new String[8][8];
    private boolean whiteTurn = true;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Move lastMove = null;
    private final Random random = new Random();

    public ChessBoard() {
        initBoard();
    }

    // Posición inicial estándar de ajedrez
    private void initBoard() {
        board[0] = new String[]{"bR","bN","bB","bQ","bK","bB","bN","bR"};
        board[1] = new String[]{"bP","bP","bP","bP","bP","bP","bP","bP"};
        board[2] = new String[]{"","","","","","","",""};
        board[3] = new String[]{"","","","","","","",""};
        board[4] = new String[]{"","","","","","","",""};
        board[5] = new String[]{"","","","","","","",""};
        board[6] = new String[]{"wP","wP","wP","wP","wP","wP","wP","wP"};
        board[7] = new String[]{"wR","wN","wB","wQ","wK","wB","wN","wR"};
    }

    // Convierte código de pieza a símbolo Unicode
    public String getSymbol(int row, int col) {
        String piece = board[row][col];
        if (piece == null || piece.isEmpty()) return "";
        switch (piece) {
            case "wK": return "♔"; case "wQ": return "♕";
            case "wR": return "♖"; case "wB": return "♗";
            case "wN": return "♘"; case "wP": return "♙";
            case "bK": return "♚"; case "bQ": return "♛";
            case "bR": return "♜"; case "bB": return "♝";
            case "bN": return "♞"; case "bP": return "♟";
            default: return "";
        }
    }

    // Intenta seleccionar o mover una pieza
    // Devuelve el tipo de resultado para que la UI sepa cómo reaccionar
    public TapResult handleTap(int row, int col) {
        String piece = board[row][col];
        boolean isWhitePiece = piece != null && piece.startsWith("w");
        boolean isBlackPiece = piece != null && piece.startsWith("b");

        // Nada seleccionado aún
        if (selectedRow == -1) {
            if (whiteTurn && isWhitePiece) {
                selectedRow = row;
                selectedCol = col;
                return TapResult.SELECTED;
            } else if (!whiteTurn && isBlackPiece) {
                selectedRow = row;
                selectedCol = col;
                return TapResult.SELECTED;
            }
            return TapResult.DESELECTED;
        }

        // Toque sobre la misma casilla ya seleccionada -> deseleccionar
        if (selectedRow == row && selectedCol == col) {
            selectedRow = -1;
            selectedCol = -1;
            return TapResult.DESELECTED;
        }

        // Ya hay una pieza seleccionada — intentar mover
        if (isValidMove(selectedRow, selectedCol, row, col)) {
            applyMove(selectedRow, selectedCol, row, col);
            return TapResult.MOVED;
        }

        // Seleccionar otra pieza propia
        if ((whiteTurn && isWhitePiece) || (!whiteTurn && isBlackPiece)) {
            selectedRow = row;
            selectedCol = col;
            return TapResult.SELECTED;
        }

        // Tap inválido — deseleccionar
        selectedRow = -1;
        selectedCol = -1;
        return TapResult.DESELECTED;
    }

    // Aplica físicamente un movimiento ya validado y actualiza el estado
    private void applyMove(int fromRow, int fromCol, int toRow, int toCol) {
        String piece = board[fromRow][fromCol];
        String target = board[toRow][toCol];
        boolean wasCapture = target != null && !target.isEmpty();

        board[toRow][toCol] = piece;
        board[fromRow][fromCol] = "";

        lastMove = new Move(fromRow, fromCol, toRow, toCol, piece, wasCapture);
        whiteTurn = !whiteTurn;
        selectedRow = -1;
        selectedCol = -1;
    }

    // Validación básica de movimientos por tipo de pieza
    private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow == toRow && fromCol == toCol) return false;

        String piece = board[fromRow][fromCol];
        String target = board[toRow][toCol];

        // No puede capturar pieza propia
        if (target != null && !target.isEmpty()) {
            if (piece.startsWith("w") && target.startsWith("w")) return false;
            if (piece.startsWith("b") && target.startsWith("b")) return false;
        }

        int dr = toRow - fromRow;
        int dc = toCol - fromCol;

        switch (piece) {
            case "wP": return isValidWhitePawn(fromRow, fromCol, toRow, toCol, target);
            case "bP": return isValidBlackPawn(fromRow, fromCol, toRow, toCol, target);
            case "wR": case "bR": return isValidRook(fromRow, fromCol, toRow, toCol);
            case "wN": case "bN": return (Math.abs(dr)==2 && Math.abs(dc)==1) || (Math.abs(dr)==1 && Math.abs(dc)==2);
            case "wB": case "bB": return isValidBishop(fromRow, fromCol, toRow, toCol);
            case "wQ": case "bQ": return isValidRook(fromRow, fromCol, toRow, toCol) || isValidBishop(fromRow, fromCol, toRow, toCol);
            case "wK": case "bK": return Math.abs(dr) <= 1 && Math.abs(dc) <= 1;
            default: return false;
        }
    }

    private boolean isValidWhitePawn(int fr, int fc, int tr, int tc, String target) {
        int dr = tr - fr;
        int dc = Math.abs(tc - fc);
        boolean targetEmpty = target == null || target.isEmpty();

        if (dc == 0 && dr == -1 && targetEmpty) return true;
        if (dc == 0 && dr == -2 && fr == 6 && targetEmpty && (board[fr-1][fc] == null || board[fr-1][fc].isEmpty())) return true;
        if (dc == 1 && dr == -1 && !targetEmpty) return true;
        return false;
    }

    private boolean isValidBlackPawn(int fr, int fc, int tr, int tc, String target) {
        int dr = tr - fr;
        int dc = Math.abs(tc - fc);
        boolean targetEmpty = target == null || target.isEmpty();

        if (dc == 0 && dr == 1 && targetEmpty) return true;
        if (dc == 0 && dr == 2 && fr == 1 && targetEmpty && (board[fr+1][fc] == null || board[fr+1][fc].isEmpty())) return true;
        if (dc == 1 && dr == 1 && !targetEmpty) return true;
        return false;
    }

    private boolean isValidRook(int fr, int fc, int tr, int tc) {
        if (fr != tr && fc != tc) return false;
        if (fr == tr) {
            int step = fc < tc ? 1 : -1;
            for (int c = fc + step; c != tc; c += step) {
                if (board[fr][c] != null && !board[fr][c].isEmpty()) return false;
            }
        } else {
            int step = fr < tr ? 1 : -1;
            for (int r = fr + step; r != tr; r += step) {
                if (board[r][fc] != null && !board[r][fc].isEmpty()) return false;
            }
        }
        return true;
    }

    private boolean isValidBishop(int fr, int fc, int tr, int tc) {
        if (Math.abs(tr - fr) != Math.abs(tc - fc)) return false;
        int rStep = tr > fr ? 1 : -1;
        int cStep = tc > fc ? 1 : -1;
        int r = fr + rStep, c = fc + cStep;
        while (r != tr) {
            if (board[r][c] != null && !board[r][c].isEmpty()) return false;
            r += rStep; c += cStep;
        }
        return true;
    }

    // Getters
    public boolean isWhiteTurn() { return whiteTurn; }
    public int getSelectedRow()  { return selectedRow; }
    public int getSelectedCol()  { return selectedCol; }
    public String getPiece(int row, int col) { return board[row][col]; }
    public Move getLastMove() { return lastMove; }

    // Verifica si el rey de un color está en el tablero (jaque mate simple)
    public boolean isKingAlive(boolean white) {
        String king = white ? "wK" : "bK";
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (king.equals(board[r][c])) return true;
        return false;
    }
    // Devuelve la posición del rey de un color, o null si ya fue capturado
    private int[] findKing(boolean white) {
        String king = white ? "wK" : "bK";
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (king.equals(board[r][c])) return new int[]{r, c};
        return null;
    }

    // ¿El rey de este color está siendo atacado ahora mismo?
    public boolean isInCheck(boolean white) {
        int[] kingPos = findKing(white);
        if (kingPos == null) return false; // ya fue capturado

        String enemyPrefix = white ? "b" : "w";
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = board[r][c];
                if (piece != null && piece.startsWith(enemyPrefix)) {
                    if (isLegalMove(r, c, kingPos[0], kingPos[1])) return true;
                }
            }
        }
        return false;
    }

    // Un movimiento es LEGAL si además de moverse "a su manera" (isValidMove)
// no deja al propio rey en jaque
    private boolean isLegalMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidMove(fromRow, fromCol, toRow, toCol)) return false;

        String piece = board[fromRow][fromCol];
        boolean white = piece.startsWith("w");

        // Simulamos el movimiento
        String captured = board[toRow][toCol];
        board[toRow][toCol] = piece;
        board[fromRow][fromCol] = "";

        boolean stillInCheck = isInCheck(white);

        // Deshacemos la simulación
        board[fromRow][fromCol] = piece;
        board[toRow][toCol] = captured;

        return !stillInCheck;
    }

    // Devuelve todas las casillas destino legales para la pieza en (fromRow, fromCol)
    public List<int[]> getLegalDestinations(int fromRow, int fromCol) {
        List<int[]> destinations = new ArrayList<>();
        String piece = board[fromRow][fromCol];
        if (piece == null || piece.isEmpty()) return destinations;

        for (int tr = 0; tr < 8; tr++) {
            for (int tc = 0; tc < 8; tc++) {
                if (isLegalMove(fromRow, fromCol, tr, tc)) {
                    destinations.add(new int[]{tr, tc});
                }
            }
        }
        return destinations;
    }

    // ---------------------------------------------------------------
    // "IA" simple sin motor externo (sin Stockfish):
    // junta todos los movimientos legales del color pedido, prioriza
    // capturas (la de mayor valor) y si no hay ninguna, elige al azar.
    // ---------------------------------------------------------------

    // Genera todos los movimientos válidos para un color
    private List<Move> generateAllMoves(boolean white) {
        List<Move> moves = new ArrayList<>();
        String prefix = white ? "w" : "b";

        for (int fr = 0; fr < 8; fr++) {
            for (int fc = 0; fc < 8; fc++) {
                String piece = board[fr][fc];
                if (piece == null || piece.isEmpty() || !piece.startsWith(prefix)) continue;

                for (int tr = 0; tr < 8; tr++) {
                    for (int tc = 0; tc < 8; tc++) {
                        if (isValidMove(fr, fc, tr, tc)) {
                            String target = board[tr][tc];
                            boolean capture = target != null && !target.isEmpty();
                            moves.add(new Move(fr, fc, tr, tc, piece, capture));
                        }
                    }
                }
            }
        }
        return moves;
    }

    // Valor aproximado de cada tipo de pieza, para que el bot prefiera
    // la mejor captura disponible en vez de una al azar
    private int pieceValue(String piece) {
        if (piece == null || piece.isEmpty()) return 0;
        switch (piece.substring(1)) {
            case "P": return 1;
            case "N": case "B": return 3;
            case "R": return 5;
            case "Q": return 9;
            case "K": return 100;
            default: return 0;
        }
    }

    // Elige y ejecuta un movimiento para el color indicado.
    // Devuelve el movimiento realizado, o null si no hay movimientos legales
    // (el bot se quedó sin jugadas).
    public Move makeSimpleMove(boolean white) {
        List<Move> moves = generateAllMoves(white);
        if (moves.isEmpty()) return null;

        List<Move> bestCaptures = new ArrayList<>();
        int bestValue = -1;
        for (Move m : moves) {
            if (!m.capture) continue;
            int value = pieceValue(board[m.toRow][m.toCol]);
            if (value > bestValue) {
                bestValue = value;
                bestCaptures.clear();
                bestCaptures.add(m);
            } else if (value == bestValue) {
                bestCaptures.add(m);
            }
        }

        Move chosen = !bestCaptures.isEmpty()
                ? bestCaptures.get(random.nextInt(bestCaptures.size()))
                : moves.get(random.nextInt(moves.size()));

        applyMove(chosen.fromRow, chosen.fromCol, chosen.toRow, chosen.toCol);
        return chosen;
    }
}