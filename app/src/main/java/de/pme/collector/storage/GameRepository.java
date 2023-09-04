package de.pme.collector.storage;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import de.pme.collector.model.Game;


public class GameRepository {

    private final GameDAO gameDAO;

    private LiveData<List<Game>> allGames;

    // singleton
    private static volatile GameRepository instance;


    // constructor
    public GameRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.gameDAO = db.gameDAO();
    }


    // singleton
    public static GameRepository getRepository(Application application) {
        if (instance == null) {
            synchronized(GameRepository.class) {
                if (instance == null) {
                    instance = new GameRepository(application);
                }
            }
        }
        return instance;
    }


    // =================================
    // Getter
    // =================================

    public LiveData<List<Game>> getGamesLiveData() {
        if (this.allGames == null) {
            this.allGames = (this.queryLiveData(this.gameDAO::getGames));
        }

        return this.allGames;
    }


    public LiveData<Game> getGameByIdLiveData(int gameId) {
        return this.queryLiveData(() -> this.gameDAO.getGameById(gameId));
    }


    public LiveData<List<Game>> getGamesForTitle(String search) {
        return this.queryLiveData(() -> this.gameDAO.getGamesForTitle(search));
    }


    public LiveData<List<Game>> getGamesSortedByTitle() {
        return this.queryLiveData(this.gameDAO::getGamesSortedByTitle);
    }


    public Game getLastGame() {
        try {
            return AppDatabase.query(this.gameDAO::getLastEntry);
        }
        catch (ExecutionException | InterruptedException exception) {
            exception.printStackTrace();
        }

        return new Game("", "", "");
    }


    // =================================
    // Query
    // =================================

    private <T> LiveData<T> queryLiveData(Callable<LiveData<T>> query) {
        try {
            return AppDatabase.query(query);
        }
        catch (ExecutionException | InterruptedException exception) {
            exception.printStackTrace();
        }

        return new MutableLiveData<>();
    }


    // =================================
    // CRUD
    // =================================

    public void deleteGameById(int gameId) {
        AppDatabase.execute(() -> gameDAO.deleteGameById(gameId));
    }


    public void update(Game game) {
        AppDatabase.execute(() -> gameDAO.update(game));
    }


    public void insert(Game game) {
        AppDatabase.execute(() -> gameDAO.insert(game));
    }
}