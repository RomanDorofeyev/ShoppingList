package com.rdproject.shoppinglist.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.shoppinglist.rdproject.shoppinglist.R;


public class AddListDialog extends DialogFragment {
    private EditText inputListName;
    private Button add, cancel;
    public OnListNameInputListener onListNameInputListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogAddList = inflater.inflate(R.layout.add_list, null);
        inputListName = dialogAddList.findViewById(R.id.enter_list_name);
        add = dialogAddList.findViewById(R.id.add_list_button);
        cancel = dialogAddList.findViewById(R.id.cancel_add_list_button);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputString = inputListName.getText().toString();
                if (inputString.isEmpty()){
                    inputListName.setError("Enter as least one symbol");
                }else {
                    onListNameInputListener.getListNameInput(inputString.trim());
                    getDialog().dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        builder.setView(dialogAddList);
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onListNameInputListener = (AddListDialog.OnListNameInputListener)getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnListNameInputListener{
        void getListNameInput(String input);
    }
}


