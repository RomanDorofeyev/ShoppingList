package com.shoppinglist.rdproject.shoppinglist.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import com.shoppinglist.rdproject.shoppinglist.MainScreen;
import com.shoppinglist.rdproject.shoppinglist.R;

import java.util.List;

public class ChooseListDialog extends DialogFragment {
    public ChooseListDialog.OnChooseListListener onChooseListListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MainScreen activity = (MainScreen)getActivity();
        List<String> listOfListsToDisplay = activity.getListOfTablesToDisplay();
        final CharSequence[] cs = listOfListsToDisplay.toArray(new CharSequence[listOfListsToDisplay.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.choose_list)
                .setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String choice = cs[i].toString();
                        onChooseListListener.getUserChoice(choice);
                    }
                });

        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onChooseListListener = (ChooseListDialog.OnChooseListListener)getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnChooseListListener{
        void getUserChoice(String listNameToDisplay);
    }
}
