package de.pme.collector.view.fragments.items;

import android.os.Bundle;

import androidx.lifecycle.LiveData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.pme.collector.R;
import de.pme.collector.model.Item;
import de.pme.collector.view.fragments.core.BaseFragment;
import de.pme.collector.viewModel.ItemDetailsViewModel;


public class ItemDetailsFragment extends BaseFragment {


    private ItemDetailsViewModel itemDetailsViewModel;

    private LiveData<Item> itemDetailsLiveData;


    // required empty constructor
    public ItemDetailsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_item_details, container, false);

        itemDetailsViewModel = this.getViewModel(ItemDetailsViewModel.class);

        setItemDetailsLiveData();

        return root;
    }


    @Override
    public void onPause() {
        super.onPause();

        itemDetailsLiveData.removeObservers(this.requireActivity());
    }


    @Override
    public void onResume() {
        super.onResume();

        setItemDetailsLiveData();
    }


    private void setItemDetailsLiveData() {

        assert getArguments() != null;
        int itemId = getArguments().getInt("itemId");

        itemDetailsLiveData = itemDetailsViewModel.getItemsForGame(itemId);

        // observe live-data & update the adapter item-list when it changes
        itemDetailsLiveData.observe(this.requireActivity(), this::updateItemDetailsView);
    }


    private void updateItemDetailsView(Item item) {

        assert getView() != null;
        ImageView itemImage         = getView().findViewById(R.id.fragment_item_details_image);
        TextView  itemName          = getView().findViewById(R.id.fragment_item_details_name);
        TextView  itemDescription   = getView().findViewById(R.id.fragment_item_details_description);
        TextView  itemLocation      = getView().findViewById(R.id.fragment_item_details_location);
        TextView  itemPrerequisites = getView().findViewById(R.id.fragment_item_details_prerequisites);

        // TODO: TEMP
        itemImage.setImageResource(R.drawable.recycler_view_placeholder_item);

        itemName.setText(item.getName());
        itemDescription.setText(item.getDescription());
        itemLocation.setText(item.getLocation());
        itemPrerequisites.setText(item.getPrerequisites());
    }
}