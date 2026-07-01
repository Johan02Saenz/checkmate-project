package com.saenz.checkmate.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

    @Entity(tableName = "games")
    public class GameEntity {

        @PrimaryKey(autoGenerate = true)
        public int id;

        public String result;
        public int botElo;
        public long durationSeconds;
        public long datePlayed;
        public String playerColor;
        public String notes;
    }

