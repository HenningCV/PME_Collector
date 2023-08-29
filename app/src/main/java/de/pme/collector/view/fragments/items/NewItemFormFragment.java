package de.pme.collector.view.fragments.items;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
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
import de.pme.collector.viewModel.NewItemFormViewModel;


public class NewItemFormFragment extends BaseFragment {

    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextPrerequisites;
    private EditText editTextLocation;
    private ImageView imagePreview;

    private NewItemFormViewModel newItemFormViewModel;


    // required empty constructor
    public NewItemFormFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_item_form, container, false);

        newItemFormViewModel = this.getViewModel(NewItemFormViewModel.class);

        editTextName          = view.findViewById(R.id.form_item_edit_text_name);
        editTextDescription   = view.findViewById(R.id.form_item_edit_text_description);
        editTextPrerequisites = view.findViewById(R.id.form_item_edit_text_prerequisites);
        editTextLocation      = view.findViewById(R.id.form_item_edit_text_location);
        imagePreview          = view.findViewById(R.id.form_game_image_preview);

        Button buttonSelectImage = view.findViewById(R.id.form_game_button_select_image);
        buttonSelectImage.setOnClickListener(v -> selectImage());

        Button buttonSave = view.findViewById(R.id.form_item_button_save);
        buttonSave.setOnClickListener(v -> saveNewEntry());

        return view;
    }


    private void selectImage() {
        ImagePicker.with(this)
                .crop()	                    // crop image
                .compress(1024)             // final image size will be less than 1 MB
                .maxResultSize(1080, 1080)  // final image resolution will be less than 1080 x 1080
                .start();
    }


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
        int gameId = getArguments().getInt("gameId");

        String name          = editTextName.getText().toString();
        String description   = editTextDescription.getText().toString();
        String prerequisites = editTextPrerequisites.getText().toString();
        String location      = editTextLocation.getText().toString();
        String imagePath     = saveImageToInternalStorage(imagePreview, name + gameId + ".jpg");

        Log.d("SaveItem", "saved item: \n"
                + "\n gameId: " + gameId
                + "\n name: " + name
                + "\n description: " + description
                + "\n prerequisites: " + prerequisites
                + "\n location: " + location
                + "\n imagePath: " + imagePath);

        Item item = new Item(gameId, imagePath, name, description, prerequisites, location);

        newItemFormViewModel.insertItem(item);

        Bundle arguments = new Bundle();
        arguments.putInt("gameId", gameId);

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_new_item_form_to_item_list, arguments);

        hideKeyboard(requireContext(), requireView());
    }


    private String saveImageToInternalStorage(ImageView imageView, String imageTitle) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File directory = requireContext().getDir("images", Context.MODE_PRIVATE);

        File file = new File(directory, imageTitle);

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
}