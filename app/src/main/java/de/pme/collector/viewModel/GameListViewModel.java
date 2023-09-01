package de.pme.collector.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.pme.collector.model.Game;
import de.pme.collector.storage.GameRepository;
import de.pme.collector.storage.ItemRepository;


public class GameListViewModel extends AndroidViewModel {

    private final GameRepository gameRepository;

    private final ItemRepository itemRepository;


    // constructor
    public GameListViewModel(@NonNull Application application) {
        super(application);

        this.gameRepository = GameRepository.getRepository(application);
        this.itemRepository = ItemRepository.getRepository(application);
    }


    public LiveData<List<Game>> getGames() {
        return this.gameRepository.getGamesLiveData();
    }


    public LiveData<Integer> getItemsCountForGame(int gameId) {
        return this.itemRepository.getItemsCountForGame(gameId);
    }


    public LiveData<Integer> getObtainedItemsCountForGame(int gameId) {
        return this.itemRepository.getObtainedItemsCountForGame(gameId);
    }
}