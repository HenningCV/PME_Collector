package de.pme.collector.view.fragments.items;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;

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

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.pme.collector.R;
import de.pme.collector.model.Item;
import de.pme.collector.view.fragments.core.BaseFragment;
import de.pme.collector.viewModel.ItemFormViewModel;


public class ItemFormFragment extends BaseFragment {

    private EditText  editTextName;
    private EditText  editTextDescription;
    private EditText  editTextPrerequisites;
    private EditText  editTextLocation;
    private ImageView imagePreview;

    private ItemFormViewModel itemFormViewModel;

    private LiveData<Item> itemLiveData;


    // required empty constructor
    public ItemFormFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_form, container, false);

        itemFormViewModel = this.getViewModel(ItemFormViewModel.class);

        // get form fields
        editTextName          = view.findViewById(R.id.form_item_edit_text_name);
        editTextDescription   = view.findViewById(R.id.form_item_edit_text_description);
        editTextPrerequisites = view.findViewById(R.id.form_item_edit_text_prerequisites);
        editTextLocation      = view.findViewById(R.id.form_item_edit_text_location);
        imagePreview          = view.findViewById(R.id.form_item_image_preview);

        // select image button
        Button buttonSelectImage = view.findViewById(R.id.form_item_button_select_image);
        buttonSelectImage.setOnClickListener(v -> selectImage());

        // save button
        Button buttonSave = view.findViewById(R.id.form_item_button_save);
        buttonSave.setOnClickListener(v -> saveNewEntry());

        // fill form fields if it is used to edit an item entry
        if (getArguments() != null && getArguments().containsKey(ITEM_ID_KEY)) {
            setupFormFields();
        }

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (itemLiveData != null) {
            itemLiveData.removeObservers(this.requireActivity());
        }
    }


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


    private void saveNewEntry() {

        assert getArguments() != null;
        int gameId = getArguments().getInt(GAME_ID_KEY);

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
            insertNewItemAndReturnToItemList(item, gameId);
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


    // saving the image to the internal storage
    private String saveImageToInternalStorage(ImageView imageView, String imageTitle) {

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


    private void setupFormFields() {
        // get the data for the item to edit
        assert getArguments() != null;
        itemLiveData = itemFormViewModel.getItemByIdLiveData(getArguments().getInt(ITEM_ID_KEY));

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


    private void setDrawableAsImage(Item item, ImageView imagePreview) {
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


    private void setDefaultImage(ImageView imageView) {
        imageView.setImageResource(R.drawable.item_placeholder);
    }


    private void insertNewItemAndReturnToItemList(Item item, int gameId) {
        Log.d("SaveItem", "saved item:");

        itemFormViewModel.insertItem(item);

        Bundle arguments = new Bundle();
        arguments.putInt(GAME_ID_KEY, gameId);

        // navigate back to the item-list
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_item_form_to_item_list, arguments);
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

        // pass the item-id back into the item-details
        assert getArguments() != null;
        int itemId = getArguments().getInt(ITEM_ID_KEY);

        Bundle arguments = new Bundle();
        arguments.putInt(ITEM_ID_KEY, itemId);

        // navigate back to the item-details
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_item_form_to_item_details, arguments);
    }
}