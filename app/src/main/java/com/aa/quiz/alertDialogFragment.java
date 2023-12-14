package com.aa.quiz;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.logging.MemoryHandler;

public class alertDialogFragment extends DialogFragment {
    ImageButton closeBtn, replayButton, menuButton;
    private String title, result;
    TextView titleView, resultView;

    public void setTitle(String title) {
        this.title = title;

    }

    public void setResult(String result) {
        this.result = result;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.gameover_dialog,null);
        builder.setView(dialogView);
        closeBtn=dialogView.findViewById(R.id.closeBtn);
        titleView=dialogView.findViewById(R.id.titleView);
        resultView=dialogView.findViewById(R.id.resultView);
        builder.setCancelable(false);
        replayButton=dialogView.findViewById(R.id.replayButton);
        menuButton=dialogView.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogFragment.this.dismiss();
            }
        });

        if(title!=null && result!=null){
            titleView.setText(title);
            resultView.setText(result);
        }

        return builder.create();
    }

   /* @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);

    }*/
}
