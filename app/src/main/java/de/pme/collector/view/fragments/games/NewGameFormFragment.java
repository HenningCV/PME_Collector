package de.pme.collector.view.fragments.games;

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
import de.pme.collector.model.Game;
import de.pme.collector.view.fragments.core.BaseFragment;
import de.pme.collector.viewModel.NewGameFormViewModel;


// class to add a new game
public class NewGameFormFragment extends BaseFragment {

    private EditText  editTextTitle;
    private EditText  editTextPublisher;
    private ImageView imagePreview;

    private NewGameFormViewModel newGameFormViewModel;

    // required empty constructor
    public NewGameFormFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflating layout
        View view = inflater.inflate(R.layout.fragment_new_game_form, container, false);
        newGameFormViewModel = this.getViewModel(NewGameFormViewModel.class);

        editTextTitle     = view.findViewById(R.id.form_game_edit_text_title);
        editTextPublisher = view.findViewById(R.id.form_game_edit_text_publisher);
        imagePreview      = view.findViewById(R.id.form_game_image_preview);

        // button for adding an image
        Button buttonSelectImage = view.findViewById(R.id.form_game_button_select_image);
        buttonSelectImage.setOnClickListener(v -> selectImage());

        // button for saving the form
        Button buttonSave = view.findViewById(R.id.form_game_button_save);
        buttonSave.setOnClickListener(v -> saveNewEntry());

        return view;
    }


    // selecting an image from gallery or camera
    private void selectImage() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
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


    // saving the new game and switching to games
    private void saveNewEntry() {
        String title     = editTextTitle.getText().toString();
        String publisher = editTextPublisher.getText().toString();
        String imagePath = saveImageToInternalStorage(imagePreview, title);

        Log.d("SaveGame", "New Game:\n" + "Title: " + title + "\n publisher: " + publisher + "\n imagePath: " + imagePath);

        Game game = new Game(title, publisher, imagePath);

        newGameFormViewModel.insertGame(game);

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_new_game_form_to_game_list);

        hideKeyboard(requireContext(), requireView());
    }


    // saving the image to the internal storage
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
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
}