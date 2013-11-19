package be.aurorethiaumont.artpeople;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class LoginDialog extends android.support.v4.app.DialogFragment {
	
	// Use this instance of the interface to deliver action events
	LoginDialogListener mListener;

	public interface LoginDialogListener {
        public void onDialogPositiveClick(LoginDialog dialog, String login, String password);
        public void onDialogNegativeClick(LoginDialog dialog);
    }
    

	public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.login_layout, null);
        
        builder.setView(v)
               .setMessage(R.string.login_ask)
               .setPositiveButton(R.string.login_ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   EditText teLogin = (EditText) v.findViewById(R.id.login);
                	   EditText tePasswd = (EditText) v.findViewById(R.id.passwd);
                	   mListener.onDialogPositiveClick(LoginDialog.this, teLogin.getText().toString(), tePasswd.getText().toString());
                   }
               })
               .setNegativeButton(R.string.login_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   mListener.onDialogNegativeClick(LoginDialog.this);
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
	
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (LoginDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	
}
