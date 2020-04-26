package com.rdproject.shoppinglist.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.shoppinglist.rdproject.shoppinglist.R;

public class ModifyListDialog extends DialogFragment {
    public OnModifyListListener onModifyListListener;
    private String option;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(option)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onModifyListListener.getUserConfirm(option);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getDialog().dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onModifyListListener = (ModifyListDialog.OnModifyListListener)getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOption(String option) {
        this.option = option;
    }

    public interface OnModifyListListener {
        void getUserConfirm(String option);
    }
}
