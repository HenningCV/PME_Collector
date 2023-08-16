package de.pme.collector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.pme.collector.R;
import de.pme.collector.interfaces.GameRecyclerViewInterface;
import de.pme.collector.model.Game;


public class GameRecyclerViewAdapter extends RecyclerView.Adapter<GameRecyclerViewAdapter.GameViewHolder> {

    private final GameRecyclerViewInterface gameRecyclerViewInterface;

    private final Context context;

    // cached copy of games
    private List<Game> gameList;


    // TODO: TEMP
    int[] gameImages = { R.drawable.game_temp_1, R.drawable.game_temp_2, R.drawable.game_temp_3,
                         R.drawable.game_temp_4, R.drawable.game_temp_5, R.drawable.game_temp_6,
                         R.drawable.game_temp_7, R.drawable.game_temp_8 };


    //
    // constructor
    public GameRecyclerViewAdapter(Context context, GameRecyclerViewInterface gameRecyclerViewInterface) {
        this.context = context;
        this.gameRecyclerViewInterface = gameRecyclerViewInterface;
    }


    //
    // inflate layout -> give a look to the rows
    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View gameView = layoutInflater.inflate(R.layout.recycler_view_row_game, parent, false);

        return new GameViewHolder(gameView, this.gameRecyclerViewInterface);
    }


    //
    // assign values to the "recycler_view_row_game.xml"-layout
    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {

        if (this.gameList != null && !this.gameList.isEmpty()) {
            Game currentGame = this.gameList.get(position);
            holder.currentGameId = currentGame.getId();

            holder.gameTitle.setText(currentGame.getTitle());
            holder.gamePublisher.setText(currentGame.getPublisher());
            // TODO: TEMP
            holder.gameImage.setImageResource(gameImages[Integer.parseInt(currentGame.getImagePath())]);
        }
        else {
            // if data is not ready yet
            holder.gameTitle.setText(R.string.fragment_game_list_empty);
        }
    }


    //
    // number of items to be displayed
    @Override
    public int getItemCount() {
        if (this.gameList != null && !this.gameList.isEmpty()) {
            return this.gameList.size();
        }

        return 0;
    }


    //
    // get views from "recycler_view_row_game.xml"-layout & assign them to variables
    public static class GameViewHolder extends RecyclerView.ViewHolder {

        ImageView gameImage;
        TextView  gameTitle;
        TextView  gamePublisher;

        int currentGameId = -1;

        public GameViewHolder(@NonNull View gameView, GameRecyclerViewInterface gameRecyclerViewInterface) {
            super(gameView);

            gameImage     = gameView.findViewById(R.id.recycler_view_game_image);
            gameTitle     = gameView.findViewById(R.id.recycler_view_game_title);
            gamePublisher = gameView.findViewById(R.id.recycler_view_game_publisher);

            // handle click on a game-list element
            gameView.setOnClickListener(v ->
                    gameRecyclerViewInterface.onGameClicked(currentGameId)
            );
        }
    }


    public void setGames(List<Game> gameList){
        this.gameList = gameList;
        // notify observers
        notifyDataSetChanged();
    }
}