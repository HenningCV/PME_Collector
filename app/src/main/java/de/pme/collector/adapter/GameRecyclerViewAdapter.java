package de.pme.collector.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import de.pme.collector.R;
import de.pme.collector.interfaces.RecyclerViewClickInterface;
import de.pme.collector.model.Game;


public class GameRecyclerViewAdapter extends RecyclerView.Adapter<GameRecyclerViewAdapter.GameViewHolder> {

    private static final short numberOfInitialGames = 7;


    private final RecyclerViewClickInterface recyclerViewClickInterface;

    private final Context context;

    // cached copy of games
    private List<Game> gameList;


    // constructor
    public GameRecyclerViewAdapter(Context context, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.context = context;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }


    // inflate layout -> "give a look to the rows"
    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a LayoutInflater-instance from the context
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // inflate layout for a single item view using the layoutInflater
        View gameView = layoutInflater.inflate(R.layout.recycler_view_row_game, parent, false);

        return new GameViewHolder(gameView, this.recyclerViewClickInterface);
    }


    // assign values to the "recycler_view_row_game.xml"-layout
    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {

        if (this.gameList != null && !this.gameList.isEmpty()) {
            Game currentGame = this.gameList.get(position);
            holder.currentGameId = currentGame.getId();

            // set game title & publisher
            holder.gameTitle.setText(currentGame.getTitle());
            holder.gamePublisher.setText(currentGame.getPublisher());

            // load game image
            File gameImageFile = new File(currentGame.getImagePath());

            // the if() is just to assign the initial games their images, after that images from the device are used
            if (currentGame.getId() < numberOfInitialGames) {
                // load images for initial games from the drawable-folder via the values-array of those images
                TypedArray gameImagesArray = holder.itemView.getResources().obtainTypedArray(R.array.initial_game_images);
                // get the id of the image
                int resourceId = gameImagesArray.getResourceId(Integer.parseInt(currentGame.getImagePath()), 0);
                // set image to the ImageView
                holder.gameImage.setImageResource(resourceId);
                // recycle gameImagesArray to avoid memory leaks
                gameImagesArray.recycle();
                return;
            }
            else {
                if (gameImageFile.exists()) {
                    // load image from device
                    Bitmap myBitmap = BitmapFactory.decodeFile(gameImageFile.getAbsolutePath());
                    // set image to the ImageView
                    holder.gameImage.setImageBitmap(myBitmap);
                    return;
                }
            }

            // set default image
            holder.gameImage.setImageResource(R.drawable.recycler_view_placeholder_game);
            // tint the default image white
            holder.gameImage.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        else {
            // if data is not ready yet
            holder.gameTitle.setText(R.string.fragment_game_list_empty);
        }
    }


    // number of items to be displayed
    @Override
    public int getItemCount() {
        if (this.gameList != null && !this.gameList.isEmpty()) {
            return this.gameList.size();
        }

        return 0;
    }


    // get views from "recycler_view_row_game.xml"-layout & assign them to variables
    public static class GameViewHolder extends RecyclerView.ViewHolder {

        ImageView gameImage;
        TextView  gameTitle;
        TextView  gamePublisher;

        int currentGameId = -1;

        public GameViewHolder(@NonNull View gameView, RecyclerViewClickInterface recyclerViewClickInterface) {
            super(gameView);

            gameImage     = gameView.findViewById(R.id.recycler_view_game_image);
            gameTitle     = gameView.findViewById(R.id.recycler_view_game_title);
            gamePublisher = gameView.findViewById(R.id.recycler_view_game_publisher);

            // handle click on a game-list element
            gameView.setOnClickListener(v ->
                    recyclerViewClickInterface.onElementClicked(currentGameId)
            );
        }
    }


    public void setGames(List<Game> gameList){
        this.gameList = gameList;
        // notify observers
        notifyDataSetChanged();
    }
}