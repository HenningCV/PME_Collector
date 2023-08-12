package de.pme.collector.storage;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import de.pme.collector.model.Item;

public class ItemRepository {

    private ItemDAO itemDAO;

    public ItemRepository( Context context ) {
        AppDatabase db = AppDatabase.getDatabase( context );
        this.itemDAO = db.itemDAO();
    }

    public List<Item> getItems()
    {
        return this.query( () -> this.itemDAO.getItems() );
    }
    public List<Item> getItemsForName(String search )
    {
        return this.query( () -> this.itemDAO.getItemsForName( search ));
    }

    public List<Item> getItemsSortedByName()
    {
        return this.query( () -> this.itemDAO.getItemsSortedByName());
    }

    private List<Item> query( Callable<List<Item>> query )
    {
        try {
            return AppDatabase.query( query );
        }
        catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public Item getLastItem() {
        try {
            return AppDatabase.query( this.itemDAO::getLastEntry );
        }
        catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return new Item(0, "", "", "", "", "");
    }

    public void update(Item item) {

        AppDatabase.execute( () -> itemDAO.update( item ) );
    }

    public void insert(Item item) {
        AppDatabase.execute( () -> itemDAO.insert( item ) );
    }


}
