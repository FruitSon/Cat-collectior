package com.example.carlos.assignment_one;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class ConfirmDialog extends DialogFragment {


    private boolean res = false;
    private String pwd_first, pwd_second;

    public boolean getRes() {
        return res;
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    DialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialog_view = inflater.inflate(R.layout.fragment_confirm_dialog, null);
        final EditText mPwd = dialog_view.findViewById(R.id.pwd_second_time);
        final EditText mPPwd = getActivity().findViewById(R.id.editTextPW);

        mPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                pwd_first = mPPwd.getText().toString();
                pwd_second = mPwd.getText().toString();
                verifyPwd(pwd_first,pwd_second,dialog_view.getContext());

                return true;
            }
        });

        builder.setView(dialog_view)
                // Add action buttons
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("DIALOG", "positive clicked");

                        //close keyboard first to avoid warming of inactive InputConnection
                        InputMethodManager mImm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mImm.hideSoftInputFromWindow(dialog_view.getWindowToken(), 0);

                        pwd_first = mPPwd.getText().toString();
                        pwd_second = mPwd.getText().toString();
                        verifyPwd(pwd_first,pwd_second,dialog_view.getContext());

                        getActivity().findViewById(R.id.buttonSave).setClickable(res);
                        getActivity().findViewById(R.id.buttonSave).setBackgroundColor(getResources().getColor(res?R.color.btn_enable:R.color.btn_disable));
                        mListener.onDialogPositiveClick(ConfirmDialog.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("DIALOG", "negative clicked");
                        res = false;
                        getActivity().findViewById(R.id.buttonSave).setClickable(res);
                        getActivity().findViewById(R.id.buttonSave).setBackgroundColor(getResources().getColor(res?R.color.btn_enable:R.color.btn_disable));
                        mListener.onDialogNegativeClick(ConfirmDialog.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity){
            activity= (Activity) context;
            try {
                // Instantiate the NoticeDialogListener so we can send events to the host
                mListener = (DialogListener) activity;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString()
                        + " must implement ConfirmDialog");
            }
            Log.d("DIALOG", "attached");
        }
    }

    public void verifyPwd(String pwd_first, String pwd_second, Context context){

        if(pwd_second.length()!=0 && pwd_first.equals(pwd_second)){
            res = true;
            dismiss();
            Toast toast = Toast.makeText(context,"Password confirmed",Toast.LENGTH_SHORT);
            toast.show();
        }else{
            res = false;
            dismiss();
            new ConfirmDialog().show(getActivity().getFragmentManager(),"ConfirmDialog");
        }
        mListener.onDialogPositiveClick(ConfirmDialog.this);
    }
}