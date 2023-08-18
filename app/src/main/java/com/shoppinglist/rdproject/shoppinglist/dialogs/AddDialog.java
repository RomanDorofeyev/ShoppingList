package com.shoppinglist.rdproject.shoppinglist.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.shoppinglist.rdproject.shoppinglist.R;

public class AddDialog extends DialogFragment {
    private EditText input, qty;
    private Button add, cancel;
    public OnTextInputListener onTextInputListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_product, null);
        qty = dialogView.findViewById(R.id.enter_quantity);
        input = dialogView.findViewById(R.id.enter_text);
        add = dialogView.findViewById(R.id.addButton);
        cancel = dialogView.findViewById(R.id.cancelButton);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputString = input.getText().toString().trim();
                String inputQty = qty.getText().toString();
                if (!inputString.equals("")) {
                    onTextInputListener.getUserInput(inputString, inputQty);
                    getDialog().dismiss();
                } else {
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
        builder.setView(dialogView);
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onTextInputListener = (OnTextInputListener)getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnTextInputListener{
        void getUserInput(String input, String qty);
    }
}
