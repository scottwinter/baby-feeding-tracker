package com.example.babyfeedingtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.babyfeedingtracker.model.ActivityItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DeleteActivityDialog extends DialogFragment {
    private DeleteActivityDialogListener deleteActivityDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you really want to delete this Activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        deleteActivityDialogListener.deleteActivity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface DeleteActivityDialogListener {
        void deleteActivity(ActivityItem activityItem);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            deleteActivityDialogListener = (DeleteActivityDialog.DeleteActivityDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Must implement ToDoItemListener");
        }
    }
}
