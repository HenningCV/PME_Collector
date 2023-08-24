package de.pme.collector.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.pme.collector.model.Game;
import de.pme.collector.model.Item;
import de.pme.collector.storage.GameRepository;
import de.pme.collector.storage.ItemRepository;


public class NewItemFormViewModel extends AndroidViewModel {

    private final ItemRepository itemRepository;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();



    // constructor
    public NewItemFormViewModel(@NonNull Application application) {
        super(application);

        this.itemRepository = ItemRepository.getRepository(application);
    }


    public void insertItem(Item item) {
        executorService.submit(() -> this.itemRepository.insert(item));
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        // shut down executor when ItemListViewModel is no longer needed
        executorService.shutdown();
    }
}