package de.pme.collector.view.fragments.items;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.pme.collector.R;
import de.pme.collector.model.Item;
import de.pme.collector.view.fragments.core.BaseFragment;
import de.pme.collector.viewModel.ItemFormViewModel;


public class ItemFormFragment extends BaseFragment {

    private TextView  itemFormHeader;
    private EditText  editTextName;
    private EditText  editTextDescription;
    private EditText  editTextPrerequisites;
    private EditText  editTextLocation;
    private ImageView imagePreview;

    private ItemFormViewModel itemFormViewModel;

    private LiveData<Item> itemLiveData;

    private int gameId;


    // required empty constructor
    public ItemFormFragment() {}


    // =================================
    // LiveCycle
    // =================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View itemFormView = inflater.inflate(R.layout.fragment_item_form, container, false);

        itemFormViewModel = this.getViewModel(ItemFormViewModel.class);

        setItemFormFields(itemFormView);

        setUpButtons(itemFormView);

        gameId = requireArguments().getInt(GAME_ID_KEY);

        // fill form fields if it is used to edit an item entry
        if (requireArguments().containsKey(ITEM_ID_KEY)) {
            itemFormHeader.setText(requireContext().getString(R.string.form_item_header_edit_item));
            setUpItemFormFields();
        }
        else {
            itemFormHeader.setText(requireContext().getString(R.string.form_item_header_new_item));
        }

        return itemFormView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (itemLiveData != null) {
            itemLiveData.removeObservers(this.requireActivity());
        }
    }


    // =================================
    // Setups
    // ================================

    private void setItemFormFields(@NonNull View itemFormView) {

        // get form header
        itemFormHeader = itemFormView.findViewById(R.id.form_item_header);

        // get form fields
        editTextName          = itemFormView.findViewById(R.id.form_item_edit_text_name);
        editTextDescription   = itemFormView.findViewById(R.id.form_item_edit_text_description);
        editTextPrerequisites = itemFormView.findViewById(R.id.form_item_edit_text_prerequisites);
        editTextLocation      = itemFormView.findViewById(R.id.form_item_edit_text_location);
        imagePreview          = itemFormView.findViewById(R.id.form_item_image_preview);
    }


    private void setUpItemFormFields() {
        // get the data for the item to edit
        itemLiveData = itemFormViewModel.getItemByIdLiveData(requireArguments().getInt(ITEM_ID_KEY));

        itemLiveData.observe(getViewLifecycleOwner(), item -> {
            editTextName         .setText(item.getName());
            editTextDescription  .setText(item.getDescription());
            editTextPrerequisites.setText(item.getPrerequisites());
            editTextLocation     .setText(item.getLocation());

            // the if() is used to handle the initial items that have drawables as their images
            if (item.getImagePath().contains("@drawable/")) {
                setDrawableAsImage(item, imagePreview);
            }
            else {
                imagePreview.setImageURI(Uri.parse(item.getImagePath()));
                imagePreview.setVisibility(View.VISIBLE);
            }
        });
    }


    private void setUpButtons(@NonNull View itemFormView) {

        // select image button
        Button buttonSelectImage = itemFormView.findViewById(R.id.form_item_button_select_image);
        buttonSelectImage.setOnClickListener(v -> selectImage());

        // save button
        Button buttonSave = itemFormView.findViewById(R.id.form_item_button_save);
        buttonSave.setOnClickListener(v -> saveNewEntry());

        // close button
        Button buttonClose = itemFormView.findViewById(R.id.form_item_close_button);
        buttonClose.setOnClickListener(v -> handleClose());
    }


    // =================================
    // Buttons
    // =================================

    private void handleClose() {
        // return to item-list if form was called to create a new item
        if (itemLiveData == null) {
            navigateToItemList();
        }
        // return to item-details if form was called to edit an item
        else {
            navigateToItemDetails();
        }
    }


    // =================================
    // Image
    // =================================

    // selecting an image from gallery or camera
    private void selectImage() {
        ImagePicker.with(this)
                .crop()	                    // crop image
                .compress(1024)             // final image size will be less than 1 MB
                .maxResultSize(1080, 1080)  // final image resolution will be less than 1080 x 1080
                .start();
    }


    // getting the an image and previewing it
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        assert data != null;
        Uri uri = data.getData();

        imagePreview.setImageURI(uri);
        imagePreview.setVisibility(View.VISIBLE);
    }


    private void setDefaultImage(@NonNull ImageView imageView) {
        imageView.setImageResource(R.drawable.item_placeholder);
    }


    // saving the image to the internal storage
    @NonNull
    private String saveImageToInternalStorage(@NonNull ImageView imageView, String imageTitle) {

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File directory = requireContext().getDir("images", Context.MODE_PRIVATE);
        String filename = imageTitle + ".png";

        File file = new File(directory, filename);

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }


    // only used for the initial images and the placeholder-drawable
    private void setDrawableAsImage(@NonNull Item item, ImageView imagePreview) {
        // split the image-path after the '@drawable/' to get the index of the image
        String[] splitImagePath = item.getImagePath().split("@drawable/");

        // load images for initial items from the drawable-folder via the values-array of those images
        TypedArray itemImagesArray = getResources().obtainTypedArray(R.array.initial_item_images);

        // get the id for the corresponding image
        int imageResourceId = itemImagesArray.getResourceId(Integer.parseInt(splitImagePath[1]), 0);

        // if the resource was not found use the default image
        if (imageResourceId == 0) {
            setDefaultImage(imagePreview);
        }
        else {
            // set image to the ImageView
            imagePreview.setImageResource(imageResourceId);
        }

        imagePreview.setVisibility(View.VISIBLE);

        // recycle itemImagesArray to avoid memory leaks
        itemImagesArray.recycle();
    }


    // =================================
    // Save Form
    // =================================

    // saving the new item and switching to item-list
    private void saveNewEntry() {

        // set a default "-" value if the text field is left empty, otherwise use the content inside
        String name          = editTextName         .getText().toString().trim().isEmpty() ? "-" : editTextName         .getText().toString();
        String description   = editTextDescription  .getText().toString().trim().isEmpty() ? "-" : editTextDescription  .getText().toString();
        String prerequisites = editTextPrerequisites.getText().toString().trim().isEmpty() ? "-" : editTextPrerequisites.getText().toString();
        String location      = editTextLocation     .getText().toString().trim().isEmpty() ? "-" : editTextLocation     .getText().toString();
        String imagePath;

        // set placeholder drawable if no image was selected or if the image is the placeholder-drawable, otherwise use the custom picture
        if (imagePreview.getVisibility() == View.INVISIBLE || imagePreview.getDrawable() instanceof VectorDrawable) {
            imagePath = "@drawable/10";
        }
        else {
            imagePath = saveImageToInternalStorage(imagePreview, name + gameId);
        }

        // if a new item is created -> insert a new item into the database
        if (itemLiveData == null) {
            Item item = new Item(gameId, imagePath, name, description, prerequisites, location);
            insertNewItemAndReturnToItemList(item);
        }
        // if an item was edited -> update the existing item in the database
        else {
            updateExistingItemAndReturnToItemDetails(name, description, prerequisites, location, imagePath);
        }

        Log.d("SaveItem",
                  "\n    gameId: "        + gameId
                + "\n    name: "          + name
                + "\n    description: "   + description
                + "\n    prerequisites: " + prerequisites
                + "\n    location: "      + location
                + "\n    imagePath: "     + imagePath);

        hideKeyboard(requireContext(), requireView());
    }


    private void insertNewItemAndReturnToItemList(Item item) {
        Log.d("SaveItem", "saved item:");

        itemFormViewModel.insertItem(item);

        navigateToItemList();
    }


    private void updateExistingItemAndReturnToItemDetails(String name, String description, String prerequisites, String location, String imagePath) {
        Log.d("SaveItem", "updated item:");

        itemLiveData.observe(getViewLifecycleOwner(), item -> {
            item.setName(name);
            item.setDescription(description);
            item.setPrerequisites(prerequisites);
            item.setLocation(location);
            item.setImagePath(imagePath);

            itemFormViewModel.updateItem(item);
        });

        navigateToItemDetails();
    }


    // =================================
    // Navigation
    // =================================

    private void navigateToItemDetails() {
        // pass the item-id back into the item-details
        int itemId = requireArguments().getInt(ITEM_ID_KEY);

        Bundle arguments = new Bundle();
        arguments.putInt(ITEM_ID_KEY, itemId);
        arguments.putInt(GAME_ID_KEY, gameId);

        // navigate back to the item-details
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_item_form_to_item_details, arguments);
    }


    private void navigateToItemList() {
        Bundle arguments = new Bundle();
        arguments.putInt(GAME_ID_KEY, gameId);

        // navigate back to the item-list
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_item_form_to_item_list, arguments);
    }
}