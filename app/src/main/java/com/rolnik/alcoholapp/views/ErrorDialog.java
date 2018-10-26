package com.rolnik.alcoholapp.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rolnik.alcoholapp.R;

public class ErrorDialog extends Dialog {
    private Button okButton;
    private Button cancelButton;
    private ImageView errorImage;
    private TextView errorMessage;

    public ErrorDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.error_dialog);

        okButton = findViewById(R.id.okButton);
        cancelButton = findViewById(R.id.cancelButton);
        errorImage = findViewById(R.id.errorImage);
        errorMessage = findViewById(R.id.errorMessage);

        initializeCancelButton();
    }

    private void initializeCancelButton(){
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    public void setOkButtonClickListener(View.OnClickListener okButtonClickListener){
        okButton.setOnClickListener(okButtonClickListener);
    }

    public void setOkButtonText(String text){
        okButton.setText(text);
    }

    public void setCancelButtonText(String text){
        cancelButton.setText(text);
    }

    public void setErrorMessage(String text, @Nullable Typeface typeface){
        errorMessage.setText(text);

        if(typeface != null){
            errorMessage.setTypeface(typeface);
        }
    }

    public void close(){
        dismiss();
    }
}
