package de.pme.collector.storage;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.pme.collector.model.Item;


@Dao
public interface ItemDAO {
    @Insert
    void insert(Item... items);

    @Update
    void update(Item... items);

    @Delete
    void delete(Item... items);

    @Query("DELETE FROM Item")
    void deleteAll();

    @Query("SELECT count(*) FROM Item")
    int count();


    @Query("SELECT * from Item")
    LiveData<List<Item>> getItems();

    @Query("SELECT * from Item ORDER BY name ASC")
    LiveData<List<Item>> getItemsSortedByName();

    @Query("SELECT * from Item ORDER BY id DESC LIMIT 1")
    Item getLastEntry();

    @Query("SELECT * FROM Item WHERE name LIKE :search")
    LiveData<List<Item>> getItemsForName(String search);

    @Query("SELECT * FROM Item WHERE gameId = :gameId")
    LiveData<List<Item>> getItemsByGameId(int gameId);

    @Query("SELECT * FROM Item WHERE id = :itemId")
    LiveData<Item> getItemById(int itemId);
}