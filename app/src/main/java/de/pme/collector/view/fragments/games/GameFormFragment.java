package de.pme.collector.view.fragments.games;

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
import de.pme.collector.model.Game;
import de.pme.collector.view.fragments.core.BaseFragment;
import de.pme.collector.viewModel.GameFormViewModel;


// class to add a new game
public class GameFormFragment extends BaseFragment {

    private TextView  gameFormHeader;
    private EditText  editTextTitle;
    private EditText  editTextPublisher;
    private ImageView imagePreview;

    private GameFormViewModel gameFormViewModel;

    private LiveData<Game> gameLiveData;


    // required empty constructor
    public GameFormFragment() {}


    // =================================
    // LiveCycle
    // =================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View gameFormView = inflater.inflate(R.layout.fragment_game_form, container, false);

        gameFormViewModel = this.getViewModel(GameFormViewModel.class);

        setGameFormFields(gameFormView);

        setUpButtons(gameFormView);

        // fill form fields if it is used to edit a game entry
        if (requireArguments().containsKey(GAME_ID_KEY)) {
            gameFormHeader.setText(requireContext().getString(R.string.form_game_header_edit_game));
            setUpGameFormFields();
        }
        else {
            gameFormHeader.setText(requireContext().getString(R.string.form_game_header_new_game));
        }

        return gameFormView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (gameLiveData != null) {
            gameLiveData.removeObservers(this.requireActivity());
        }
    }


    // =================================
    // Setups
    // =================================

    private void setGameFormFields(@NonNull View gameFormView) {

        // get form header
        gameFormHeader = gameFormView.findViewById(R.id.form_game_header);

        // get form fields
        editTextTitle     = gameFormView.findViewById(R.id.form_game_edit_text_title);
        editTextPublisher = gameFormView.findViewById(R.id.form_game_edit_text_publisher);
        imagePreview      = gameFormView.findViewById(R.id.form_game_image_preview);
    }


    private void setUpGameFormFields() {
        // get the data for the game to edit
        gameLiveData = gameFormViewModel.getGameByIdLiveData(requireArguments().getInt(GAME_ID_KEY));

        gameLiveData.observe(getViewLifecycleOwner(), game -> {
            editTextTitle    .setText(game.getTitle());
            editTextPublisher.setText(game.getPublisher());

            // the if() is used to handle the initial games that have drawables as their images
            if (game.getImagePath().contains("@drawable/")) {
                setDrawableAsImage(game, imagePreview);
            }
            else {
                imagePreview.setImageURI(Uri.parse(game.getImagePath()));
                imagePreview.setVisibility(View.VISIBLE);
            }
        });
    }


    private void setUpButtons(@NonNull View gameFormView) {

        // select image button
        Button buttonSelectImage = gameFormView.findViewById(R.id.form_game_button_select_image);
        buttonSelectImage.setOnClickListener(v -> selectImage());

        // save button
        Button buttonSave = gameFormView.findViewById(R.id.form_game_button_save);
        buttonSave.setOnClickListener(v -> saveNewEntry());

        // close button
        Button buttonClose = gameFormView.findViewById(R.id.form_game_close_button);
        buttonClose.setOnClickListener(v -> navigateBackToGameList());
    }


    // =================================
    // Image
    // =================================

    // selecting an image from gallery or camera
    private void selectImage() {
        ImagePicker.with(this)
                .crop()                     // crop image
                .compress(1024)             // final image size will be less than 1 MB
                .maxResultSize(1080, 1080)  // final image resolution will be less than 1080 x 1080
                .start();
    }


    // getting the image and previewing it
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        assert data != null;
        Uri uri = data.getData();

        imagePreview.setImageURI(uri);
        imagePreview.setVisibility(View.VISIBLE);
    }


    private void setDefaultImage(@NonNull ImageView imageView) {
        imageView.setImageResource(R.drawable.game_placeholder);
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
    private void setDrawableAsImage(@NonNull Game game, ImageView imagePreview) {
        // split the image-path after the '@drawable/' to get the index of the image
        String[] splitImagePath = game.getImagePath().split("@drawable/");

        // load images for initial games from the drawable-folder via the values-array of those images
        TypedArray gameImagesArray = getResources().obtainTypedArray(R.array.initial_game_images);

        // get the id for the corresponding image
        int imageResourceId = gameImagesArray.getResourceId(Integer.parseInt(splitImagePath[1]), 0);

        // if the resource was not found use the default image
        if (imageResourceId == 0) {
            setDefaultImage(imagePreview);
        }
        else {
            // set image to the ImageView
            imagePreview.setImageResource(imageResourceId);
        }

        imagePreview.setVisibility(View.VISIBLE);

        // recycle gameImagesArray to avoid memory leaks
        gameImagesArray.recycle();
    }


    // =================================
    // Save Form
    // =================================

    // saving the new game and switching to game-list
    private void saveNewEntry() {

        // set a default "-" value if the text field is left empty, otherwise use the content inside
        String title     = editTextTitle    .getText().toString().trim().isEmpty() ? "-" : editTextTitle    .getText().toString();
        String publisher = editTextPublisher.getText().toString().trim().isEmpty() ? "-" : editTextPublisher.getText().toString();
        String imagePath;

        // set placeholder drawable if no image was selected or if the image is the placeholder-drawable, otherwise use the custom picture
        if (imagePreview.getVisibility() == View.INVISIBLE || imagePreview.getDrawable() instanceof VectorDrawable) {
            imagePath = "@drawable/10";
        }
        else {
            imagePath = saveImageToInternalStorage(imagePreview, title);
        }

        // if a new game is created -> insert a new game into the database
        if (gameLiveData == null) {
            Game game = new Game(title, publisher, imagePath);
            insertNewGame(game);
        }
        // if a game was edited -> update the existing game in the database
        else {
            updateExistingGame(title, publisher, imagePath);
        }

        Log.d("SaveGame",
                  "\n    title: "     + title
                + "\n    publisher: " + publisher
                + "\n    imagePath: " + imagePath);

        navigateBackToGameList();
    }


    private void insertNewGame(Game game) {
        Log.d("SaveGame", "saved game:");

        gameFormViewModel.insertGame(game);
    }


    private void updateExistingGame(String title, String  publisher, String  imagePath) {
        Log.d("SaveGame", "updated game:");

        gameLiveData.observe(getViewLifecycleOwner(), game -> {
            game.setTitle(title);
            game.setPublisher(publisher);
            game.setImagePath(imagePath);

            gameFormViewModel.updateGame(game);
        });
    }


    // =================================
    // Navigation
    // =================================

    private void navigateBackToGameList() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_game_form_to_game_list);

        hideKeyboard(requireContext(), requireView());
    }
}