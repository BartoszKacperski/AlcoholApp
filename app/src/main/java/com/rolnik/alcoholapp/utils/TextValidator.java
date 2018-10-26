package com.rolnik.alcoholapp.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class TextValidator implements TextWatcher {

    private EditText editText;

    public TextValidator(EditText editText){
        this.editText = editText;
    }

    public abstract void validate(String s);

    public void setEditTextErrorMessage(String message){
        editText.setError(message);
    }

    public void setEditTextText(String text){
        editText.setText(text);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //TODO
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        validate(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
        //TODO
    }
}
