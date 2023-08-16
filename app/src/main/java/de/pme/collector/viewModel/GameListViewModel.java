package de.pme.collector.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.pme.collector.model.Game;
import de.pme.collector.storage.GameRepository;


public class GameListViewModel extends AndroidViewModel {

    private final GameRepository gameRepository;


    // constructor
    public GameListViewModel(@NonNull Application application) {
        super(application);

        this.gameRepository = GameRepository.getRepository(application);
    }


    public LiveData<List<Game>> getGames() {
        return this.gameRepository.getGamesLiveData();
    }
}