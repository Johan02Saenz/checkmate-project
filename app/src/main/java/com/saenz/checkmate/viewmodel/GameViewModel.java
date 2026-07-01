package com.saenz.checkmate.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.saenz.checkmate.database.AppDatabase;
import com.saenz.checkmate.database.GameDao;
import com.saenz.checkmate.database.GameEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameViewModel extends AndroidViewModel {

    private final GameDao gameDao;
    private final LiveData<List<GameEntity>> allGames;
    private final ExecutorService executor;

    public GameViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        gameDao = db.gameDao();
        allGames = gameDao.getAllGames();
        executor = Executors.newSingleThreadExecutor();
    }

    // READ — la UI observa este LiveData
    public LiveData<List<GameEntity>> getAllGames() {
        return allGames;
    }

    // CREATE
    public void insert(GameEntity game) {
        executor.execute(() -> gameDao.insert(game));
    }

    // UPDATE
    public void update(GameEntity game) {
        executor.execute(() -> gameDao.update(game));
    }

    // DELETE
    public void delete(GameEntity game) {
        executor.execute(() -> gameDao.delete(game));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}