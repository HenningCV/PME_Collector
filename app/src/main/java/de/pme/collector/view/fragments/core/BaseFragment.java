package de.pme.collector.view.fragments.core;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class BaseFragment extends Fragment {

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
    protected void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}