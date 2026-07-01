package com.saenz.checkmate.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GameDao {

    @Insert
    void insert(GameEntity game);

    @Update
    void update(GameEntity game);

    @Delete
    void delete(GameEntity game);

    @Query("SELECT * FROM games ORDER BY datePlayed DESC")
    LiveData<List<GameEntity>> getAllGames();
}