package de.pme.collector.view.fragments.games;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.pme.collector.R;
import de.pme.collector.adapter.GameRecyclerViewAdapter;
import de.pme.collector.view.fragments.core.BaseFragment;
import de.pme.collector.viewModel.GameListViewModel;


public class GameListFragment extends BaseFragment {

    // required empty constructor
    public GameListFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_game_list, container, false);

        GameListViewModel gameListViewModel = this.getViewModel(GameListViewModel.class);

        RecyclerView gameRecyclerView = root.findViewById(R.id.recycler_view_games);

        GameRecyclerViewAdapter gameAdapter = new GameRecyclerViewAdapter(
                getContext(),
                gameId -> {
                    Bundle args = new Bundle();
                    args.putInt("gameId", gameId);
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_game_list_to_item_list, args);
                }
        );

        gameRecyclerView.setAdapter(gameAdapter);
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));

        // observe live-data & update the adapter game-list when it changes
        gameListViewModel.getGames().observe(this.requireActivity(), gameAdapter::setGames);

        return root;
    }
}