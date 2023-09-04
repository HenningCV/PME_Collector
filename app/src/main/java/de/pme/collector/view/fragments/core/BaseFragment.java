package de.pme.collector.view.fragments.core;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class BaseFragment extends Fragment {

    protected final static String ITEM_ID_KEY = "itemId";
    protected final static String GAME_ID_KEY = "gameId";


    // get ViewModel & AndroidViewModel with access to context
    protected <T extends ViewModel> T getViewModel(Class<T> tClass) {
        return new ViewModelProvider(
                this,
                (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(
                        requireActivity().getApplication()
                )
        ).get(tClass);
    }


    // hide keyboard
    protected void hideKeyboard(@NonNull Context context, @NonNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}