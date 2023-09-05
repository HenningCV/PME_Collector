package de.pme.collector.view.fragments.items;

import android.app.AlertDialog;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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

    private int itemId;


    // required empty constructor
    public ItemDetailsFragment() {}


    // =================================
    // LiveCycle
    // =================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View itemDetailsView = inflater.inflate(R.layout.fragment_item_details, container, false);

        // get the gameId that is passed in by the ItemListRecyclerView that the item belongs to
        itemId = requireArguments().getInt(ITEM_ID_KEY);

        itemDetailsViewModel = this.getViewModel(ItemDetailsViewModel.class);

        // set item-liveData & observe it
        itemDetailsLiveData = itemDetailsViewModel.getItemByIdLiveData(itemId);
        observeItemDetailsLiveData();

        setUpButtons(itemDetailsView);

        return itemDetailsView;
    }


    @Override
    public void onPause() {
        super.onPause();

        removeItemDetailsLiveDataObserver();
    }


    @Override
    public void onResume() {
        super.onResume();

        observeItemDetailsLiveData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        removeItemDetailsLiveDataObserver();
    }


    // =================================
    // Setups
    // =================================

    private void setItemDetailsFields(Item item) {

        TextView  itemObtainedStatus = requireView().findViewById(R.id.fragment_item_details_obtained_status);
        ImageView itemImage          = requireView().findViewById(R.id.fragment_item_details_image);
        TextView  itemName           = requireView().findViewById(R.id.fragment_item_details_name);
        TextView  itemDescription    = requireView().findViewById(R.id.fragment_item_details_description);
        TextView  itemLocation       = requireView().findViewById(R.id.fragment_item_details_location);
        TextView  itemPrerequisites  = requireView().findViewById(R.id.fragment_item_details_prerequisites);

        setObtainedStatus(item, itemObtainedStatus);

        setItemImage(item, itemImage);

        itemName.setText(item.getName());
        itemDescription.setText(item.getDescription());
        itemLocation.setText(item.getLocation());
        itemPrerequisites.setText(item.getPrerequisites());
    }


    private void setUpButtons(@NonNull View itemDetailsView) {

        // back button
        Button backButton = itemDetailsView.findViewById(R.id.item_details_back_button);
        backButton.setOnClickListener(b -> backToItemList());

        // options-menu button
        Button optionButton = itemDetailsView.findViewById(R.id.item_details_options_button);
        optionButton.setOnClickListener(this::showOptionMenu);
    }


    // =================================
    // Buttons
    // =================================

    // navigate back to the item-list
    private void backToItemList() {

        int gameId = requireArguments().getInt(GAME_ID_KEY);

        Bundle arguments = new Bundle();

        // pass the item-id back into the item-list
        arguments.putInt(GAME_ID_KEY, gameId);

        // navigate to item-list
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_item_details_to_item_list, arguments);
    }


    // display item details options
    private void showOptionMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(requireContext(), view);

        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.item_details_options_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {

            removeItemDetailsLiveDataObserver();

            if (item.getItemId() == R.id.action_edit_item) {
                editItem();
                return true;
            }

            // only display not obtained items
            if (item.getItemId() == R.id.action_delete_item) {
                showDeleteConfirmationDialog();
                return true;
            }

            return false;
        });

        popupMenu.show();
    }


    private void setObtainedStatus(@NonNull Item item, TextView itemObtainedStatus) {
        // green background
        if (item.isObtained()) {
            itemObtainedStatus.setText(requireContext().getString(R.string.item_obtained));
            itemObtainedStatus.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.item_obtained));
        }
        // red background
        else {
            itemObtainedStatus.setText(requireContext().getString(R.string.item_not_obtained));
            itemObtainedStatus.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.item_not_obtained));
        }
    }


    // =================================
    // Image
    // =================================

    private void setItemImage(@NonNull Item item, ImageView itemImage) {

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


    private void setDefaultImage(@NonNull ImageView itemImage) {
        itemImage.setImageResource(R.drawable.item_placeholder);
    }


    // =================================
    // Options
    // =================================

    // option: edit the item
    private void editItem() {

        int gameId = requireArguments().getInt(GAME_ID_KEY);

        Bundle arguments = new Bundle();

        // pass the item-id & game-id into the item-form
        arguments.putInt(ITEM_ID_KEY, itemId);
        arguments.putInt(GAME_ID_KEY, gameId);

        // navigate to item-form
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_item_details_to_item_form, arguments);
    }


    // option: delete the item
    private void deleteItem() {
        itemDetailsViewModel.deleteItemById(itemId);

        int gameId = requireArguments().getInt(GAME_ID_KEY);

        Bundle arguments = new Bundle();

        // pass the game-id back into the item-list
        arguments.putInt(GAME_ID_KEY, gameId);

        // navigate back to item-list
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_item_details_to_item_list, arguments);
    }


    // =================================
    // Confirm-Delete Dialog
    // =================================

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> deleteItem())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.show();
    }


    // =================================
    // LiveData
    // =================================

    private void observeItemDetailsLiveData() {
        // observe live-data & update the adapter item-list when it changes
        itemDetailsLiveData.observe(this.requireActivity(), this::setItemDetailsFields);
    }


    private void removeItemDetailsLiveDataObserver() {
        if (itemDetailsLiveData != null) {
            itemDetailsLiveData.removeObservers(this.requireActivity());
        }
    }
}