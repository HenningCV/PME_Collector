package de.pme.collector.view.fragments.items;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import de.pme.collector.R;
import de.pme.collector.model.Item;
import de.pme.collector.storage.ItemRepository;

public class NewItemFormFragment extends Fragment {

    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextPrerequisites;
    private EditText editTextLocation;

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
        Button buttonSave = view.findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v -> saveNewEntry());

        return view;
    }

    private void saveNewEntry() {
        int gameId = this.gameId;
        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();
        String prerequisites = editTextPrerequisites.getText().toString();
        String location = editTextLocation.getText().toString();

        Item item = new Item(gameId, "", name, description, prerequisites, location);

        itemRepository.insert(item);
    }
}