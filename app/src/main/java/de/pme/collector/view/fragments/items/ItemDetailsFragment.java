package de.pme.collector.view.fragments.items;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.lifecycle.LiveData;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.File;

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

        // button for an options menu
        Button optionButton = root.findViewById(R.id.item_details_options_button);
        optionButton.setOnClickListener(this::showOptionMenu);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        itemDetailsLiveData.removeObservers(this.requireActivity());
    }

    // display item list options
    private void showOptionMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(requireContext(), view);

        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.item_edit_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_edit_item) {
                editItem();
                return true;
            }

            // only display not obtained items
            if (item.getItemId() == R.id.action_delete_item) {
                deleteItem();
                return true;
            }

            return false;
        });

        popupMenu.show();
    }


    private void setItemDetailsLiveData() {

        assert getArguments() != null;
        // get the gameId that is passed in by the ItemListRecyclerView that the item belongs to
        int itemId = getArguments().getInt("itemId");

        // get the database-data for that item
        itemDetailsLiveData = itemDetailsViewModel.getItemsForGame(itemId);

        // observe live-data & update the adapter item-list when it changes
        itemDetailsLiveData.observe(this.requireActivity(), this::updateItemDetailsView);
    }


    private void updateItemDetailsView(Item item) {

        if (getView() == null) {
            return;
        }

        ImageView itemImage         = getView().findViewById(R.id.fragment_item_details_image);
        TextView  itemName          = getView().findViewById(R.id.fragment_item_details_name);
        TextView  itemDescription   = getView().findViewById(R.id.fragment_item_details_description);
        TextView  itemLocation      = getView().findViewById(R.id.fragment_item_details_location);
        TextView  itemPrerequisites = getView().findViewById(R.id.fragment_item_details_prerequisites);

        setItemImage(item, itemImage);

        itemName.setText(item.getName());
        itemDescription.setText(item.getDescription());
        itemLocation.setText(item.getLocation());
        itemPrerequisites.setText(item.getPrerequisites());
    }


    private void setItemImage(Item item, ImageView itemImage) {

        String imagePath = item.getImagePath();

        // load item image
        File itemImageFile = new File(imagePath);

        // the if() is just to assign the initial items their images, after that images from the device are used
        if (imagePath.contains("@drawable/")) {

            // split the image-path after the '@drawable/' to get the index of the image
            String[] splitImagePath = imagePath.split("@drawable/");

            // load images for initial items from the drawable-folder via the values-array of those images
            TypedArray itemImagesArray = getResources().obtainTypedArray(R.array.initial_item_images);

            // get the id for the corresponding image
            int imageResourceId = itemImagesArray.getResourceId(Integer.parseInt(splitImagePath[1]), 0);

            // if the resource was not found use the default image
            if (imageResourceId == 0) {
                setDefaultImage(itemImage);
            }
            else {
                // set image to the ImageView
                itemImage.setImageResource(imageResourceId);
            }

            // recycle itemImagesArray to avoid memory leaks
            itemImagesArray.recycle();
            return;
        }
        else {
            if (itemImageFile.exists()) {
                // load image from device
                Bitmap myBitmap = BitmapFactory.decodeFile(itemImageFile.getAbsolutePath());

                // set image to the ImageView
                itemImage.setImageBitmap(myBitmap);
                return;
            }
        }

        // set default image if the system image doesn't exist anymore
        setDefaultImage(itemImage);
    }


    private void setDefaultImage(ImageView itemImage) {
        itemImage.setImageResource(R.drawable.item_placeholder);
    }

    // editing the item
    private void editItem() {
        Log.d("ItemDetails", "Editing item...");
    }

    // editing the item
    private void deleteItem() {
        Log.d("ItemDetails", "Deleting item...");
    }
}