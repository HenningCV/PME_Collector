package de.pme.collector.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.pme.collector.R;
import de.pme.collector.adapter.GameRecyclerViewAdapter;
import de.pme.collector.model.GameModelTemp;


public class HomeFragment extends Fragment {

    ArrayList<GameModelTemp> gameModels = new ArrayList<>();

    int[] gameImages = { R.drawable.game_temp_1, R.drawable.game_temp_2, R.drawable.game_temp_3,
                         R.drawable.game_temp_4, R.drawable.game_temp_5, R.drawable.game_temp_6,
                         R.drawable.game_temp_7, R.drawable.game_temp_8 };


    // required empty constructor
    public HomeFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView gameRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view_games);

        setUpGameModels();

        GameRecyclerViewAdapter gameAdapter = new GameRecyclerViewAdapter(getContext(), gameModels);

        gameRecyclerView.setAdapter(gameAdapter);
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    private void setUpGameModels() {
        String[] gameTitles = getResources().getStringArray(R.array.games_txt);
        String[] gamePublishers = getResources().getStringArray(R.array.publishers_txt);

        for (int i = 0; i < gameTitles.length; i++) {
            gameModels.add(new GameModelTemp(gameTitles[i], gamePublishers[i], gameImages[i]));
        }
    }
}