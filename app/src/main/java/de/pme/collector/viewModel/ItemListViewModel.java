package de.pme.collector.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.pme.collector.model.Item;
import de.pme.collector.storage.ItemRepository;


public class ItemListViewModel extends AndroidViewModel {

    private final ItemRepository itemRepository;


    // constructor
    public ItemListViewModel(@NonNull Application application) {
        super(application);

        this.itemRepository = ItemRepository.getRepository(application);
    }


    public LiveData<List<Item>> getItemsForGame(int gameId) {
        return this.itemRepository.getItemsForGameLiveData(gameId);
    }
}