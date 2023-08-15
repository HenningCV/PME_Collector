package de.pme.collector.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import de.pme.collector.R;
import de.pme.collector.model.Game;
import de.pme.collector.storage.GameRepository;

public class NewGameFormFragment extends Fragment {

    private EditText editTextTitle;
    private EditText editTextPublisher;
    private final  GameRepository gameRepository;

    public NewGameFormFragment(GameRepository gameRepository){
        this.gameRepository = gameRepository;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_game_form, container, false);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextPublisher = view.findViewById(R.id.editTextPublisher);
        Button buttonSave = view.findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v -> saveNewEntry());

        return view;
    }

    private void saveNewEntry() {
        String title = editTextTitle.getText().toString();
        String publisher = editTextPublisher.getText().toString();

        Game game = new Game("", title, publisher);

        gameRepository.insert(game);
    }
}