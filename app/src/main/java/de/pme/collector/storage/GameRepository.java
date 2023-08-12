package de.pme.collector.storage;

import android.content.Context;

import de.pme.collector.model.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class GameRepository {

    private GameDAO gameDAO;

    public GameRepository( Context context ) {
        AppDatabase db = AppDatabase.getDatabase( context );
        this.gameDAO = db.gameDAO();
    }

    public List<Game> getGames()
    {
        return this.query( () -> this.gameDAO.getGames() );
    }
    public List<Game> getGamesForTitle(String search )
    {
        return this.query( () -> this.gameDAO.getGamesForTitle( search ));
    }

    public List<Game> getGamesSortedByTitle()
    {
        return this.query( () -> this.gameDAO.getGamesSortedByTitle());
    }

    private List<Game> query( Callable<List<Game>> query )
    {
        try {
            return AppDatabase.query( query );
        }
        catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public Game getLastGame() {
        try {
            return AppDatabase.query( this.gameDAO::getLastEntry );
        }
        catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return new Game("", "", "");
    }

    public void update(Game game) {

        AppDatabase.execute( () -> gameDAO.update( game ) );
    }

    public void insert(Game game) {
        AppDatabase.execute( () -> gameDAO.insert( game ) );
    }


}
