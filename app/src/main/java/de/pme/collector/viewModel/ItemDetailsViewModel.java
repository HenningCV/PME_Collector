package de.pme.collector.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import de.pme.collector.model.Item;
import de.pme.collector.storage.ItemRepository;


public class ItemDetailsViewModel extends AndroidViewModel {

    private final ItemRepository itemRepository;


    // constructor
    public ItemDetailsViewModel(@NonNull Application application) {
        super(application);

        this.itemRepository = ItemRepository.getRepository(application);
    }


    public LiveData<Item> getItemByIdLiveData(int itemId) {
        return this.itemRepository.getItemByIdLiveData(itemId);
    }


    public void deleteItemById(int itemId) {
        this.itemRepository.deleteItemById(itemId);
    }
}