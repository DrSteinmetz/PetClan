package com.example.android2project.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android2project.R;

import java.util.Random;

public class DeleteDialog extends Dialog {

    private Button mYesBt;
    private Button mNoBtn;
    private TextView mPromptTv;
    private ImageView mPetIv;
    int[] photos = {R.drawable.ic_cat1, R.drawable.ic_dog1, R.drawable.ic_dog2, R.drawable.ic_bear1};

    public interface DeleteDialogActionListener {
        void onYesBtnClicked();

        void onNoBtnClicked();
    }

    private DeleteDialogActionListener listener;

    public void setOnActionListener(DeleteDialogActionListener dialogActionListener) {
        this.listener = dialogActionListener;
    }

    public DeleteDialog(Context context) {
        super(context);

        initialize();
    }

    private void initialize() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.delete_dialog, null);
        mPromptTv = view.findViewById(R.id.prompt_tv);
        mPetIv = view.findViewById(R.id.image_iv);
        mYesBt = view.findViewById(R.id.yes_btn);
        mNoBtn = view.findViewById(R.id.no_btn);

        setRandomImage();

        mYesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onYesBtnClicked();
                }
            }
        });
        mNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNoBtnClicked();
            }
        });

        setCancelable(false);
        setContentView(view);
    }

    @Override
    public void show() {
        super.show();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
    }

    public void setPromptText(String prompt) {
        mPromptTv.setText(prompt);
    }

    private void setRandomImage() {
        Random ran = new Random();
        int i = ran.nextInt(4);
        mPetIv.setImageResource(photos[i]);
    }
}
