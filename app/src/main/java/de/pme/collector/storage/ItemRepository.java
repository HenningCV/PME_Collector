package de.pme.collector.storage;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import de.pme.collector.model.Item;


public class ItemRepository {

    private final ItemDAO itemDAO;

    private LiveData<List<Item>> allItems;

    // singleton
    private static volatile ItemRepository instance;


    // constructor
    public ItemRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.itemDAO = db.itemDAO();
    }


    // singleton
    public static ItemRepository getRepository(Application application) {
        if (instance == null) {
            synchronized(ItemRepository.class) {
                if (instance == null) {
                    instance = new ItemRepository(application);
                }
            }
        }
        return instance;
    }


    // getter
    public LiveData<List<Item>> getItemsForGameLiveData(int gameId) {
            return this.queryLiveData(() -> this.itemDAO.getItemsByGameId(gameId));
    }


    public  LiveData<Item> getItemByIdLiveData(int itemId) {
        return this.queryLiveData(() -> this.itemDAO.getItemById(itemId));
    }


    public LiveData<List<Item>> getItemsForName(String search) {
        return this.queryLiveData(() -> this.itemDAO.getItemsForName(search));
    }


    public LiveData<List<Item>> getItemsSortedByName(int gameId) {
        return this.queryLiveData(() -> this.itemDAO.getItemsSortedByName(gameId));
    }


    public LiveData<List<Item>> getNotObtainedItemsSortedByName(int gameId) {
        return this.queryLiveData(() -> this.itemDAO.getNotObtainedItemsSortedByName(gameId));
    }


    public LiveData<List<Item>> getObtainedItemsSortedByName(int gameId) {
        return this.queryLiveData(() -> this.itemDAO.getObtainedItemsSortedByName(gameId));
    }


    public Item getLastItem() {
        try {
            return AppDatabase.query(this.itemDAO::getLastEntry);
        }
        catch (ExecutionException | InterruptedException exception) {
            exception.printStackTrace();
        }

        return new Item(0, "", "", "", "", "");
    }


    public void setObtainedStatus(boolean obtained, int itemId) {
        this.itemDAO.setObtainedStatus(obtained, itemId);
    }


    // CRUD
    private <T> LiveData<T> queryLiveData(Callable<LiveData<T>> query) {
        try {
            return AppDatabase.query(query);
        }
        catch (ExecutionException | InterruptedException exception) {
            exception.printStackTrace();
        }

        return new MutableLiveData<>();
    }


    public void update(Item item) {
        AppDatabase.execute(() -> itemDAO.update(item));
    }

    public void insert(Item item) {
        AppDatabase.execute(() -> itemDAO.insert(item));
    }
}