package de.pme.collector.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.pme.collector.model.Item;
import de.pme.collector.storage.ItemRepository;


public class ItemFormViewModel extends AndroidViewModel {

    private final ItemRepository itemRepository;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    // constructor
    public ItemFormViewModel(@NonNull Application application) {
        super(application);

        this.itemRepository = ItemRepository.getRepository(application);
    }


    public LiveData<Item> getItemByIdLiveData(int itemId) {
        return this.itemRepository.getItemByIdLiveData(itemId);
    }


    public void insertItem(Item item) {
        executorService.submit(() -> this.itemRepository.insert(item));
    }


    public void updateItem(Item item) {
        executorService.submit(() -> this.itemRepository.update(item));
    }


    @Override
    protected void onCleared() {
        super.onCleared();

        // shut down executor when ItemFormViewModel is no longer needed
        executorService.shutdown();
    }
}