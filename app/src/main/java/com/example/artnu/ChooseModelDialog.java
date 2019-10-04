package com.example.artnu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.Map;

public class ChooseModelDialog extends DialogFragment {

    public static final String TAG = ChooseModelDialog.class.getSimpleName();

    private int choice = 0;
    private Map<Integer, Integer> indexToPaintingId = new HashMap<>();
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

        int locked = 0;
        int index = 0;
        for (Painting painting: PaintingUtil.getPaintings()) {
            if (PaintingUtil.isUnlocked(painting.getId())) {
                arrayAdapter.add(painting.getPaintingName());
                indexToPaintingId.put(index, painting.getId());
                index++;
            }
            else {
                locked++;
            }
        }

        if (locked > 0) {
            arrayAdapter.add("?");
        }

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

    public int getPaintingId(int index) {
        if (indexToPaintingId.containsKey(index))
        return indexToPaintingId.get(index);
        return -1;
    }
}