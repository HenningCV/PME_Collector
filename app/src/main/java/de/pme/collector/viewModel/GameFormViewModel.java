package de.pme.collector.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.pme.collector.model.Game;
import de.pme.collector.storage.GameRepository;


public class GameFormViewModel extends AndroidViewModel {

    private final GameRepository gameRepository;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    // constructor
    public GameFormViewModel(@NonNull Application application) {
        super(application);

        this.gameRepository = GameRepository.getRepository(application);
    }


    public LiveData<Game> getGameByIdLiveData(int gameId) {
        return this.gameRepository.getGameByIdLiveData(gameId);
    }


    public void insertGame(Game game) {
        executorService.submit(() -> this.gameRepository.insert(game));
    }


    public void updateGame(Game game) {
        executorService.submit(() -> this.gameRepository.update(game));
    }


    @Override
    protected void onCleared() {
        super.onCleared();

        // shut down executor when GameFormViewModel is no longer needed
        executorService.shutdown();
    }
}