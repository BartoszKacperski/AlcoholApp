package com.rolnik.alcoholapp.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rolnik.alcoholapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResendEmailDialog extends Dialog {
    @BindView(R.id.okButton)
    Button okButton;
    @BindView(R.id.cancelButton)
    Button cancelButton;
    @BindView(R.id.errorImage)
    ImageView errorImage;
    @BindView(R.id.errorMessage)
    TextView errorMessage;
    @BindView(R.id.email)
    EditText email;

    public ResendEmailDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.resend_email_dialog);
        ButterKnife.bind(this);

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

    public String getEmail(){
        return email.getText().toString();
    }

    public void close(){
        dismiss();
    }
}
