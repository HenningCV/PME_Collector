package de.pme.collector.view.fragments.items;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
                }
        );

        itemRecyclerView.setAdapter(itemAdapter);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));

        setItemListLiveData();

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


    private void setItemListLiveData() {

        assert getArguments() != null;
        int gameId = getArguments().getInt("gameId");

        itemLiveData = itemListViewModel.getItemsForGame(gameId);

        // observe live-data & update the adapter item-list when it changes
        itemLiveData.observe(this.requireActivity(), itemAdapter::setItems);
    }
}