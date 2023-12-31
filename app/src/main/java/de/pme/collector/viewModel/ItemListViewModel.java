package de.pme.collector.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.pme.collector.model.Item;
import de.pme.collector.storage.GameRepository;
import de.pme.collector.storage.ItemRepository;


public class ItemListViewModel extends AndroidViewModel {

    private final ItemRepository itemRepository;

    private final GameRepository gameRepository;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    // constructor
    public ItemListViewModel(@NonNull Application application) {
        super(application);

        this.itemRepository = ItemRepository.getRepository(application);
        this.gameRepository = GameRepository.getRepository(application);
    }


    public LiveData<List<Item>> getItemsForGame(int gameId) {
        return this.itemRepository.getItemsForGameLiveData(gameId);
    }


    public LiveData<List<Item>> getItemsSortedAlphabetically(int gameId) {
        return this.itemRepository.getItemsSortedByName(gameId);
    }


    public LiveData<List<Item>> getNotObtainedItemsSortedByName(int gameId) {
        return this.itemRepository.getNotObtainedItemsSortedByName(gameId);
    }


    public LiveData<List<Item>> getObtainedItemsSortedByName(int gameId) {
        return this.itemRepository.getObtainedItemsSortedByName(gameId);
    }


    public void setObtainedStatus(boolean obtained, int itemId) {
        executorService.submit(() -> this.itemRepository.setObtainedStatus(obtained, itemId));
    }


    public void deleteGameById(int gameId) {
        gameRepository.deleteGameById(gameId);
    }


    @Override
    protected void onCleared() {
        super.onCleared();

        // shut down executor when ItemListViewModel is no longer needed
        executorService.shutdown();
    }
}