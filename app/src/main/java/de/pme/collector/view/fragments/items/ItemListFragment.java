package de.pme.collector.view.fragments.items;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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


    // required empty constructor
    public ItemListFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_item_list, container, false);

        itemListViewModel = this.getViewModel(ItemListViewModel.class);

        RecyclerView itemRecyclerView = root.findViewById(R.id.recycler_view_items);

        itemAdapter = new ItemRecyclerViewAdapter(
                getContext(),
                itemId -> {
                    Bundle arguments = new Bundle();
                    arguments.putInt("itemId", itemId);
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_item_list_to_item_details, arguments);
                },
                itemListViewModel
        );

        itemRecyclerView.setAdapter(itemAdapter);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));

        setItemListLiveData();

        Button addButton = root.findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> {
            int gameId = getArguments().getInt("gameId");
            Bundle arguments = new Bundle();
            arguments.putInt("gameId", gameId);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_item_list_to_new_item_form, arguments);
        });

        Button optionButton = root.findViewById(R.id.option_button);
        optionButton.setOnClickListener(v -> {
            showOptionMenu(v);
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

        setItemListLiveData();
    }

    private void showOptionMenu(View view){
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.item_filter_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_filter_not_acquired:
                        filterNotAcquired();
                        return true;
                    case R.id.action_sort_alphabetical:
                        sortAlphabetical();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    private void setItemListLiveData() {

        assert getArguments() != null;
        int gameId = getArguments().getInt("gameId");

        itemLiveData = itemListViewModel.getItemsForGame(gameId);

        // observe live-data & update the adapter item-list when it changes
        itemLiveData.observe(this.requireActivity(), itemAdapter::setItems);
    }

    private void filterNotAcquired(){
        Log.d("OptionsMenu", "selected not acquired filter");
    }

    private void sortAlphabetical(){
        Log.d("OptionsMenu", "selected not acquired filter");
    }
}