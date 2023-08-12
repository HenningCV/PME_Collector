package de.pme.collector.storage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.pme.collector.model.Game;


@Dao
public interface GameDAO {
    @Insert
    void insert(Game... games);

    @Update
    void update(Game... games);

    @Delete
    void delete(Game... games);

    @Query("DELETE FROM Game")
    void deleteAll();

    @Query("SELECT count(*) FROM Game")
    int count();

    @Query("SELECT * from Game")
    List<Game> getGames();

    @Query("SELECT * from Game ORDER BY title ASC")
    List<Game> getGamesSortedByTitle();

    @Query("SELECT * from Game ORDER BY id DESC LIMIT 1")
    Game getLastEntry();

    @Query("SELECT * FROM Game WHERE title LIKE :search")
    List<Game> getGamesForTitle(String search);
}
