package de.pme.collector.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.pme.collector.R;
import de.pme.collector.model.GameModelTemp;


public class GameRecyclerViewAdapter extends RecyclerView.Adapter<GameRecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<GameModelTemp> gameModels;


    // constructor
    public GameRecyclerViewAdapter(Context context, ArrayList<GameModelTemp> gameModels) {
        this.context = context;
        this.gameModels = gameModels;
    }


    @NonNull
    @Override
    // inflate layout -> give a look to the rows
    public GameRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_view_row_game, parent, false);

        return new GameRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    // assign values to the "recycler_view_row_game.xml"-layout
    public void onBindViewHolder(@NonNull GameRecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.textViewGameTitle.setText(gameModels.get(position).getGameTitle());
        holder.textViewGamePublisher.setText(gameModels.get(position).getGamePublisher());
        holder.imageView.setImageResource(gameModels.get(position).getImage());
    }

    @Override
    // number of items to be displayed
    public int getItemCount() {
        return gameModels.size();
    }


    // get views from "recycler_view_row_game.xml"-layout & assign them to variables
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textViewGameTitle;
        TextView textViewGamePublisher;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recycler_view_game_image);
            textViewGameTitle = itemView.findViewById(R.id.recycler_view_game_title);
            textViewGamePublisher = itemView.findViewById(R.id.recycler_view_game_publisher);
        }
    }
}