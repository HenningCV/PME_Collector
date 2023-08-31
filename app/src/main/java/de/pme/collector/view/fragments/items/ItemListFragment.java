package de.pme.collector.view.fragments.items;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;

import java.util.List;

import de.pme.collector.R;
import de.pme.collector.adapter.ItemRecyclerViewAdapter;
import de.pme.collector.model.Item;
import de.pme.collector.view.fragments.core.BaseFragment;
import de.pme.collector.viewModel.ItemListViewModel;


public class ItemListFragment extends BaseFragment {

    private ItemListViewModel itemListViewModel;

    private LiveData<List<Item>> itemLiveData;

    private ItemRecyclerViewAdapter itemAdapter;

    private int gameId;


    // required empty constructor
    public ItemListFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_item_list, container, false);

        itemListViewModel = this.getViewModel(ItemListViewModel.class);

        RecyclerView itemRecyclerView = root.findViewById(R.id.item_list_recycler_view);

        // get the passed in gameId that the items belong to
        assert getArguments() != null;
        gameId = getArguments().getInt(GAME_ID_KEY);

        // create recycler-view adapter and setup onClickListener for each item
        itemAdapter = new ItemRecyclerViewAdapter(
                getContext(),
                itemId -> {
                    Bundle arguments = new Bundle();
                    arguments.putInt(ITEM_ID_KEY, itemId);
                    arguments.putInt(GAME_ID_KEY, gameId);

                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_item_list_to_item_details, arguments);
                },
                itemListViewModel
        );

        // set recycler-view adapter
        itemRecyclerView.setAdapter(itemAdapter);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));

        // set item-liveData & observe it
        itemLiveData = itemListViewModel.getItemsForGame(gameId);
        observeItemLiveData();

        // back button
        Button backButton = root.findViewById(R.id.item_list_back_button);
        backButton.setOnClickListener(b -> backToGameList());

        // options-menu button
        Button optionButton = root.findViewById(R.id.item_list_options_button);
        optionButton.setOnClickListener(this::showOptionMenu);

        // add-new-item button
        Button addItemButton = root.findViewById(R.id.item_list_add_new_item_button);

        addItemButton.setOnClickListener(v -> {
            Bundle arguments = new Bundle();
            arguments.putInt(GAME_ID_KEY, gameId);

            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_item_list_to_item_form, arguments);
        });

        return root;
    }


    @Override
    public void onPause() {
        super.onPause();

        itemLiveData.removeObservers(this.requireActivity());
    }


    @Override
    public void onResume() {
        super.onResume();

        observeItemLiveData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        itemLiveData.removeObservers(this.requireActivity());
    }


    // navigate back to the game-list
    private void backToGameList() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_item_list_to_game_list);
    }


    // display item list options
    private void showOptionMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(requireContext(), view);

        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.item_list_options_menu, popupMenu.getMenu());

        // set up onClickListener for the menu-button
        popupMenu.setOnMenuItemClickListener(item -> {

            // display all items
            if (item.getItemId() == R.id.action_filter_show_all_items) {
                showAllItems();
                return true;
            }

            // only display not obtained items
            if (item.getItemId() == R.id.action_filter_show_not_obtained) {
                filterNotObtained();
                return true;
            }

            // only display obtained items
            if (item.getItemId() == R.id.action_filter_show_obtained) {
                filterObtained();
                return true;
            }

            // sort item list alphabetically
            if (item.getItemId() == R.id.action_sort_alphabetically) {
                sortAlphabetically();
                return true;
            }

            // edit game
            if (item.getItemId() == R.id.action_edit_game) {
                editGame();
                return true;
            }

            // delete game
            if (item.getItemId() == R.id.action_delete_game) {
                deleteGame();
                return true;
            }

            return false;
        });

        popupMenu.show();
    }


    // display all items for a game
    private void showAllItems() {
        setNewLiveDataAndUpdateObserver(itemListViewModel.getItemsForGame(gameId));
    }


    // option: only display not obtained items
    private void filterNotObtained() {
        setNewLiveDataAndUpdateObserver(itemListViewModel.getNotObtainedItemsSortedByName(gameId));
    }


    // option: only display obtained items
    private void filterObtained() {
        setNewLiveDataAndUpdateObserver(itemListViewModel.getObtainedItemsSortedByName(gameId));
    }


    // option: sort item list alphabetically
    private void sortAlphabetically() {
        setNewLiveDataAndUpdateObserver(itemListViewModel.getItemsSortedAlphabetically(gameId));
    }


    // option: edit game
    private void editGame() {
        assert getArguments() != null;
        int gameId = getArguments().getInt(GAME_ID_KEY);

        Bundle arguments = new Bundle();

        // pass the game-id into the game-form
        arguments.putInt(GAME_ID_KEY, gameId);

        // navigate to the game-form
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_item_list_to_game_form, arguments);
    }


    // option: delete game for the associated items
    private void deleteGame() {
        itemListViewModel.deleteGameById(gameId);

        // navigate back to game-list
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_item_list_to_game_list);
    }


    private void setNewLiveDataAndUpdateObserver(LiveData<List<Item>> liveData) {
        // remove observers from the old liveData-instance
        itemLiveData.removeObservers(this.requireActivity());
        // set itemLiveData to a new liveData-instance
        itemLiveData = liveData;
        // observe that new liveData-instance
        observeItemLiveData();
    }


    private void observeItemLiveData() {
        // observe live-data & update the adapter item-list when it changes
        itemLiveData.observe(this.requireActivity(), itemAdapter::setItems);
    }
}