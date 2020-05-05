package com.example.stormy.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.stormy.R;

public class AlertDialogFragment extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.error_title).setMessage(R.string.error_message)
                .setPositiveButton(R.string.error_button_ok_text, null);

                return builder.create();

        // Create the AlertDialog object and return it

    }
}
