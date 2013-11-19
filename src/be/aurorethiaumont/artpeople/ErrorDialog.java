package be.aurorethiaumont.artpeople;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ErrorDialog extends DialogFragment{

	public String message;

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
        	   .setTitle(R.string.err_error_title)
               .setNeutralButton(R.string.err_error_ack_button, null);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
