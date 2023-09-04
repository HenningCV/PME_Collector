package de.pme.collector.viewModel;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

import java.io.Serializable;


// needed because if switching the theme in the settings "requireActivity().recreate()" is called
// which resets the destinationId and arguments if they would be saved directly in the MainActivity
public class SharedViewModel extends ViewModel implements Serializable {

    private int currentDestinationId = -1;

    private Bundle arguments;


    public int getLastDestinationId() {
        return currentDestinationId;
    }


    public void setCurrentDestinationId(int currentDestinationId) {
        this.currentDestinationId = currentDestinationId;
    }


    public Bundle getLastDestinationArguments() {
        return arguments;
    }

    public void setArguments(Bundle arguments) {
        this.arguments = arguments;
    }
}