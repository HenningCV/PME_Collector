package de.pme.collector.view.fragments;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;


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
}