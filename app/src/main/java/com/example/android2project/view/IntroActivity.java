package com.example.android2project.view;

import androidx.annotation.FloatRange;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android2project.R;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.MessageButtonBehaviour;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;
import io.github.dreierf.materialintroscreen.animations.IViewTranslation;

public class IntroActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideBackButton();

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.transparent)
                .image(R.drawable.ic_cat)
                .title("Welcome To PetClan!")
                .description("You are about to have a great time in here and find some new friends whom share the same love to their pets!")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimaryDark)
                .buttonsColor(R.color.transparent)
                .image(R.drawable.ic_clown_fish)
                .title("Our Feed")
                .description("Share your thoughts,pets,questions and whatever you like in our feed")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.transparent)
                .image(R.drawable.ic_golden_retriever)
                .title("Our ChatClan")
                .description("Talk with everyone you like")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorAccent)
                .buttonsColor(R.color.transparent)
                .image(R.drawable.ic_parrot)
                .title("Our Marketplace")
                .description("Sell and buy pets,products...")
                .build());


        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.transparent)
                .image(R.drawable.ic_rabbit)
                .title("The Time Has Come")
                .description("Just enjoy and have fun!")
                .build(), new MessageButtonBehaviour(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, "Move To App"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}