package com.rdproject.shoppinglist.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import com.google.firebase.database.FirebaseDatabase;
import com.rdproject.shoppinglist.MainScreen;
import com.shoppinglist.rdproject.shoppinglist.R;
import com.rdproject.shoppinglist.SharedList;

import java.util.ArrayList;
import java.util.List;

public class ChooseSharedListDialog extends DialogFragment {
    public ChooseSharedListDialog.OnChooseSharedListListener onChooseSharedListListener;

    private List<String> sharedListsNames;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sharedListsNames = new ArrayList<>();
        final MainScreen activity = (MainScreen)getActivity();
        final List<SharedList> listOfSharedLists = activity.getSharedLists();

        for (SharedList list : listOfSharedLists){
                sharedListsNames.add(list.getSharedListName() + "  (" + list.getFromUserName() + ") ");
        }
        final CharSequence[] cs = sharedListsNames.toArray(new CharSequence[sharedListsNames.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.choose_list)
                .setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String choice = cs[i].toString();
                        String sharedUserID = listOfSharedLists.get(i).getFromUserId();
                       // choice = choice.substring(0, choice.indexOf('(')).trim();
                        onChooseSharedListListener.getUserSharedListChoice(choice, sharedUserID);
                    }
                }).setNegativeButton(R.string.clear_shared, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference("Users").child(activity.userId).child("sharedlists").removeValue();
                getDialog().dismiss();
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
            onChooseSharedListListener = (ChooseSharedListDialog.OnChooseSharedListListener)getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnChooseSharedListListener{
        void getUserSharedListChoice(String listNameToDisplay, String sharedUserID);
    }
}
