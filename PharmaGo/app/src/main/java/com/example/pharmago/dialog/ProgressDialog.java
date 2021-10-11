package com.example.pharmago.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pharmago.R;

public class ProgressDialog extends Dialog {

    private TextView  txtTitle;
    private CharSequence title="";
    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText(title);
    }

    @Override
    public void setTitle(CharSequence title) {
       this.title = title;
    }


    @Override
    public void onBackPressed() {

    }
}
