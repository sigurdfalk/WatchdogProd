package no.ntnu.idi.watchdogprod.recommender;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import no.ntnu.idi.watchdogprod.R;

/**
 * Created by Wschive on 28/04/15.
 */
public class NoReplacementsDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.noReplacements)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
