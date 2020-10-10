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
                .title(getString(R.string.intro_slide1_title))
                .description(getString(R.string.intro_slide1_description))
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimaryDark)
                .buttonsColor(R.color.transparent)
                .image(R.drawable.ic_clown_fish)
                .title(getString(R.string.intro_slide2_title))
                .description(getString(R.string.intro_slide2_description))
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.transparent)
                .image(R.drawable.ic_golden_retriever)
                .title(getString(R.string.intro_slide3_title))
                .description(getString(R.string.intro_slide3_description))
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorAccent)
                .buttonsColor(R.color.transparent)
                .image(R.drawable.ic_parrot)
                .title(getString(R.string.intro_slide4_title))
                .description(getString(R.string.intro_slide4_description))
                .build());


        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.transparent)
                .image(R.drawable.ic_rabbit)
                .title(getString(R.string.intro_slide5_title))
                .description(getString(R.string.intro_slide5_description))
                .build(), new MessageButtonBehaviour(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, getString(R.string.continur_to_app)));

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