package de.pme.collector.view.fragments.games;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
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
import de.pme.collector.model.Game;
import de.pme.collector.storage.GameRepository;

public class NewGameFormFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSION = 2;
    private static final int REQUEST_IMAGE_PICK = 3;

    private EditText editTextTitle;
    private EditText editTextPublisher;
    private ImageView imagePreview;
    private Bitmap selectedBitmap;

    private final GameRepository gameRepository;

    public NewGameFormFragment(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_game_form, container, false);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextPublisher = view.findViewById(R.id.editTextPublisher);
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
        String title = editTextTitle.getText().toString();
        String publisher = editTextPublisher.getText().toString();

        if (selectedBitmap != null) {
            String imagePath = saveImageToExternalStorage(selectedBitmap, title + ".jpg");


            Game game = new Game(title, publisher, imagePath);

            gameRepository.insert(game);
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