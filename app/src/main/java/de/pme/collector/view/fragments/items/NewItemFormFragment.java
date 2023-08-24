package de.pme.collector.view.fragments.items;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

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

    private static final int REQUEST_PERMISSION = 2;

    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextPrerequisites;
    private EditText editTextLocation;
    private ImageView imagePreview;
    private Bitmap selectedBitmap;

    private NewItemFormViewModel newItemFormViewModel;


    public NewItemFormFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_item_form, container, false);
        newItemFormViewModel = this.getViewModel(NewItemFormViewModel.class);


        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextPrerequisites = view.findViewById(R.id.editTextPrerequisites);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        imagePreview = view.findViewById(R.id.imagePreview);

        Button buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        buttonSelectImage.setOnClickListener(v -> selectImage());

        Button buttonSave = view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> saveNewEntry());

        return view;
    }

    private void selectImage() {
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        imagePreview.setImageURI(uri);
        imagePreview.setVisibility(View.VISIBLE);
    }


    private void saveNewEntry() {
        int gameId = getArguments().getInt("gameId");
        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();
        String prerequisites = editTextPrerequisites.getText().toString();
        String location = editTextLocation.getText().toString();
        String imagePath = saveImageToInternalStorage(imagePreview, name + gameId + ".jpg");

        Log.d("SaveItem", "saved item: \n"
                + "\n gameId: " + gameId
                + "\n name: " + name
                + "\n description: " + description
                + "\n prerequisites: " + prerequisites
                + "\n location: " + location
                + "\n imagePath: " + imagePath);

        Item item = new Item(gameId, imagePath, name, description, prerequisites, location);

        newItemFormViewModel.insertItem(item);

    }

    private String saveImageToInternalStorage(ImageView imageView, String imageTitle) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File directory = requireContext().getDir("images", Context.MODE_PRIVATE);
        String filename = imageTitle + ".jpg";
        File file = new File(directory, filename);

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imagePath = file.getAbsolutePath();
        return imagePath;
    }


}