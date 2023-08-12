package de.pme.collector.storage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.pme.collector.model.Game;
import de.pme.collector.model.Item;

@Database(entities = {Game.class, Item.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract GameDAO gameDAO();
    public abstract ItemDAO itemDAO();

    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool( NUMBER_OF_THREADS );

    private static volatile AppDatabase INSTANCE;

    // Create and open database
    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static <T> T query(Callable<T> task)
            throws ExecutionException, InterruptedException
    {
        return databaseWriteExecutor.invokeAny( Collections.singletonList( task ) );
    }

    public static void execute( Runnable runnable )
    {
        databaseWriteExecutor.execute( runnable );
    }





}
