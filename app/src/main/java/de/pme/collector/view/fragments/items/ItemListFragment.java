package de.pme.collector.view.fragments.items;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

        assert getArguments() != null;
        gameId = getArguments().getInt("gameId");

        itemAdapter = new ItemRecyclerViewAdapter(
                getContext(),
                itemId -> {
                    Bundle arguments = new Bundle();
                    arguments.putInt("itemId", itemId);
                    arguments.putInt("gameId", gameId);

                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_item_list_to_item_details, arguments);
                },
                itemListViewModel
        );

        itemLiveData = itemListViewModel.getItemsForGame(gameId);
        observeItemLiveData();

        itemRecyclerView.setAdapter(itemAdapter);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));

        // button to add a new item
        Button addItemButton = root.findViewById(R.id.item_list_add_new_item_button);

        addItemButton.setOnClickListener(v -> {
            Bundle arguments = new Bundle();
            arguments.putInt("gameId", gameId);

            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_item_list_to_new_item_form, arguments);
        });

        // button for an options menu
        Button optionButton = root.findViewById(R.id.item_list_options_button);
        optionButton.setOnClickListener(this::showOptionMenu);

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


    // display item list options
    private void showOptionMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(requireContext(), view);

        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.item_filter_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_filter_show_all_items) {
                setItemListLiveData();
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
    private void setItemListLiveData() {
        itemLiveData.removeObservers(this.requireActivity());
        itemLiveData = itemListViewModel.getItemsForGame(gameId);
        observeItemLiveData();
    }


    // option: only display not obtained items
    private void filterNotObtained() {
        itemLiveData.removeObservers(this.requireActivity());
        itemLiveData = itemListViewModel.getNotObtainedItemsSortedByName(gameId);
        observeItemLiveData();
    }


    // option: only display obtained items
    private void filterObtained() {
        itemLiveData.removeObservers(this.requireActivity());
        itemLiveData = itemListViewModel.getObtainedItemsSortedByName(gameId);
        observeItemLiveData();
    }


    // option: sort item list alphabetically
    private void sortAlphabetically() {
        itemLiveData.removeObservers(this.requireActivity());
        itemLiveData = itemListViewModel.getItemsSortedAlphabetically(gameId);
        observeItemLiveData();
    }


    private void observeItemLiveData() {
        // observe live-data & update the adapter item-list when it changes
        itemLiveData.observe(this.requireActivity(), itemAdapter::setItems);
    }


    // option: only display obtained items
    private void deleteGame() {
        itemListViewModel.deleteGameById(gameId);

        // navigate back to game-list
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_item_list_to_game_list);
    }
}