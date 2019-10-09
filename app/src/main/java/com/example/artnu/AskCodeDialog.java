package com.example.artnu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class AskCodeDialog extends DialogFragment {

    public static final String TAG = AskCodeDialog.class.getSimpleName();

    private EditText input;
    private DialogInterface.OnClickListener listener;

    public AskCodeDialog(DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("When was this made?");

        input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", listener);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public String getText() {
        return input.getText().toString();
    }
}
