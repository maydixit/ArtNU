package com.example.artnu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.fragment.app.DialogFragment;

public class ChooseModelDialog extends DialogFragment {

    public static final String TAG = ChooseModelDialog.class.getSimpleName();

    private int choice = 0;
    private DialogInterface.OnClickListener listener;

    public ChooseModelDialog(DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Available models ? ");


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);

        arrayAdapter.add("Unlocked");
        arrayAdapter.add("Partially unlocked");
        arrayAdapter.add("Locked");

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setAdapter(arrayAdapter, listener);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public int getChoice() {
        return choice;
    }
}