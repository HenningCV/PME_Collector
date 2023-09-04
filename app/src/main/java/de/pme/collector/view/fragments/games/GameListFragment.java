package de.pme.collector.view.fragments.games;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import de.pme.collector.R;
import de.pme.collector.adapter.GameRecyclerViewAdapter;
import de.pme.collector.model.Game;
import de.pme.collector.view.fragments.core.BaseFragment;
import de.pme.collector.viewModel.GameListViewModel;


public class GameListFragment extends BaseFragment {

    private GameListViewModel gameListViewModel;

    private LiveData<List<Game>> gameListLiveData;

    private GameRecyclerViewAdapter gameAdapter;


    // required empty constructor
    public GameListFragment() {}


    // =================================
    // LiveCycle
    // =================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View gameListView = inflater.inflate(R.layout.fragment_game_list, container, false);

        gameListViewModel = this.getViewModel(GameListViewModel.class);

        setUpGameListRecyclerView(gameListView);

        // set item-liveData & observe it
        gameListLiveData = gameListViewModel.getGames();
        observeGameLiveData();

        setUpButtons(gameListView);

        return gameListView;
    }


    @Override
    public void onPause() {
        super.onPause();

        removeGameListLiveDataObserver();
    }


    @Override
    public void onResume() {
        super.onResume();

        observeGameLiveData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        removeGameListLiveDataObserver();
    }


    // =================================
    // Setups
    // =================================

    private void setUpGameListRecyclerView(@NonNull View gameListView) {

        RecyclerView gameRecyclerView = gameListView.findViewById(R.id.game_list_recycler_view);

        // create recycler-view adapter and setup onClickListener for each game
        gameAdapter = new GameRecyclerViewAdapter(
                getContext(),
                gameId -> {
                    Bundle args = new Bundle();
                    args.putInt(GAME_ID_KEY, gameId);
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_game_list_to_item_list, args);
                },
                gameListViewModel,
                this
        );

        // set recycler-view adapter
        gameRecyclerView.setAdapter(gameAdapter);
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));
    }


    private void setUpButtons(@NonNull View gameListView) {

        // add-new-game button
        Button addButton = gameListView.findViewById(R.id.game_list_add_new_game_button);
        addButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_game_list_to_game_form);
        });
    }


    // =================================
    // LiveData
    // =================================

    private void observeGameLiveData() {
        // observe live-data & update the adapter game-list when it changes
        gameListLiveData.observe(this.requireActivity(), gameAdapter::setGames);
    }


    private void removeGameListLiveDataObserver() {
        if (gameListLiveData != null) {
            gameListLiveData.removeObservers(this.requireActivity());
        }
    }
}