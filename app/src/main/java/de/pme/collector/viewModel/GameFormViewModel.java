package de.pme.collector.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

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


    public void insertGame(Game game) {
        executorService.submit(() -> this.gameRepository.insert(game));
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        // shut down executor when ItemListViewModel is no longer needed
        executorService.shutdown();
    }
}