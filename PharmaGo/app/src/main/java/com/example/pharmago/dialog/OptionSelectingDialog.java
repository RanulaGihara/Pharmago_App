package com.example.pharmago.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.example.pharmago.R;

public class OptionSelectingDialog extends Dialog {

    private OptionSelectingDialog dialog;
    private Button buttonOk, buttonCancel;
    private onReasonDialogButtonClick onOkButtonClick, onCancelButtonClick;
    private RadioGroup radioGroupOption;
    private int option=1;

    public OptionSelectingDialog(@NonNull Context context) {
        super(context);
        dialog = this;
    }

    public onReasonDialogButtonClick getOnOkButtonClick() {
        return onOkButtonClick;
    }

    public void setOnOkButtonClick(onReasonDialogButtonClick onOkButtonClick) {
        this.onOkButtonClick = onOkButtonClick;
    }

    public onReasonDialogButtonClick getOnCancelButtonClick() {
        return onCancelButtonClick;
    }

    public void setOnCancelButtonClick(onReasonDialogButtonClick onCancelButtonClick) {
        this.onCancelButtonClick = onCancelButtonClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_option);
        buttonOk = findViewById(R.id.buttonOk);
        buttonCancel = findViewById(R.id.buttonCancel);
        radioGroupOption  = findViewById(R.id.radioGroupOption);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (radioGroupOption.getCheckedRadioButtonId() == R.id.radioSpc)
                {
                    option = 1;
                }else {
                    option = 2;
                }


                onOkButtonClick.onClick(dialog,option);
                dialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelButtonClick.onClick(dialog,0);
                dialog.dismiss();
            }
        });

    }

    public interface onReasonDialogButtonClick {
        void onClick(OptionSelectingDialog dialog, int option);
    }
}
