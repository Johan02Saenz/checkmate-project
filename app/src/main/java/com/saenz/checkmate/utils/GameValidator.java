package com.saenz.checkmate.utils;

public class GameValidator {


    public static boolean isValidElo(int elo) {
        return elo >= 400 && elo <= 2000;
    }

    public static boolean isValidDuration(long seconds) {
        return seconds > 0;
    }

    public static boolean isValidResult(String result) {
        if (result == null || result.isEmpty()) return false;
        return result.equals("VICTORY") || result.equals("DEFEAT") || result.equals("DRAW");
    }
}