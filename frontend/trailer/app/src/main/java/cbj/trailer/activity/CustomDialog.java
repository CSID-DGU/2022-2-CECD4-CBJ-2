package cbj.trailer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cbj.trailer.R;

public class CustomDialog extends Dialog {
    private TextView txt_contents;
    private Button shutdownClick;

    public CustomDialog(@NonNull Context context, String contents) {
        super(context);
        setContentView(R.layout.activity_custom_dialog);

        txt_contents = findViewById(R.id.txt_contents);
        txt_contents.setText(contents);
        shutdownClick = findViewById(R.id.btn_shutdown);
        shutdownClick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}