package com.rolnik.alcoholapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.rolnik.alcoholapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CreditsActivity extends AppCompatActivity {

    @BindView(R.id.credits)
    TextView credits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        ButterKnife.bind(this);

        credits.setText(parseAuthorsToHtml());
        credits.setClickable(true);
        credits.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private Spanned parseAuthorsToHtml(){
        String [] authors = getResources().getStringArray(R.array.authors);
        StringBuilder builder = new StringBuilder();

        for(String author : authors){
            Log.i("Author", author);
            builder.append(author);
            builder.append("<br>");
        }

        return Html.fromHtml(builder.toString());
    }
}
