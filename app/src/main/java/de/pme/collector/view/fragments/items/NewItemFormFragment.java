package de.pme.collector.view.fragments.items;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.pme.collector.R;
import de.pme.collector.model.Item;
import de.pme.collector.storage.ItemRepository;

public class NewItemFormFragment extends Fragment {

    private static final int REQUEST_PERMISSION = 2;

    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextPrerequisites;
    private EditText editTextLocation;
    private ImageView imagePreview;
    private Bitmap selectedBitmap;

    private final ItemRepository itemRepository;
    private final int gameId;
    public NewItemFormFragment(ItemRepository itemRepository, int gameId){
        this.itemRepository = itemRepository;
        this.gameId = gameId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_item_form, container, false);

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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    try {
                        selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), result);
                        imagePreview.setImageBitmap(selectedBitmap);
                        imagePreview.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            galleryLauncher.launch("image/*");
        }
    }


    private void saveNewEntry() {
        int gameId = this.gameId;
        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();
        String prerequisites = editTextPrerequisites.getText().toString();
        String location = editTextLocation.getText().toString();

        if (selectedBitmap != null) {
            String imagePath = saveImageToExternalStorage(selectedBitmap, name + ".jpg");
            Item item = new Item(gameId, imagePath, name, description, prerequisites, location);

            itemRepository.insert(item);
        }
    }

    private String saveImageToExternalStorage(Bitmap imageBitmap, String fileName) {
        String imagePath = null;

        try {
            File storageDir = new File(Environment.getExternalStorageDirectory(), "ImageStorage");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            File imageFile = new File(storageDir, fileName);
            imagePath = imageFile.getAbsolutePath();

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imagePath;
    }
}