package com.aa.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.aa.quiz.models.*;

import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class quizActivity extends AppCompatActivity {
    //DECLERATIONS
    TextView questionTv, awnserOne,
            awnserTwo, awnserThree,
            awnserFour, scoreView,
            remainingQuestionsView;
    OkHttpClient client;
    List questionViews;
    String url;
    Root core;
    int clickCount, score;
    private MediaPlayer correctSound, incorrectSound,
            okResultSoundPlayer, badResultSoundPlayer,
             perfectScoreSoundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //REFERENCE GRABBERS
        //NETWORK
        url = getIntent().getStringExtra("category");
        client = new OkHttpClient();
        //TEXTVIEWS
        questionTv = (TextView) findViewById(R.id.questionView);
        awnserOne = (TextView) findViewById(R.id.awnserOne);
        awnserTwo = (TextView) findViewById(R.id.awnserTwo);
        awnserThree = (TextView) findViewById(R.id.awnserThree);
        awnserFour = (TextView) findViewById(R.id.awnserFour);
        scoreView = (TextView) findViewById(R.id.scoreView);
        remainingQuestionsView = (TextView) findViewById(R.id.remainingQuestipnsView);
        //MEDIAPLAYERS
        //PLAYS ON CORRECT CHOICE
        correctSound = MediaPlayer.create(this, R.raw.correctchoice);
        //PLAYS ON INCORRECT CHOICE
        incorrectSound = MediaPlayer.create(this, R.raw.losssound);
        //PLAYS FROM 4-9
        okResultSoundPlayer = MediaPlayer.create(this, R.raw.ok);
        //PLAYS FROM 1-3
        badResultSoundPlayer = MediaPlayer.create(this, R.raw.loser_sound);
        //PLAYS ON 10
        perfectScoreSoundPlayer = MediaPlayer.create(this, R.raw.perfect_score_sound);
        //VARIABLES IMPORTANT FOR TRACKING DURING RUNTIME
        //ARRAYLIST FOR TRACKING THE TEXTVIEWS DURING RUNTIME (FOR SETTING THE AWNSERS)
        questionViews = new ArrayList<TextView>(Arrays.asList(awnserOne, awnserTwo, awnserThree, awnserFour));
        clickCount = 0;
        score = 0;
        //LOGIC TO RESET THE QUESTION INCASE ORIENTATION CHANGES DURING RUNTIME
        if (savedInstanceState != null) {
            questionTv.setText((CharSequence) savedInstanceState.get("question"));
            awnserOne.setText((CharSequence) savedInstanceState.get("awnserOne"));
            awnserTwo.setText((CharSequence) savedInstanceState.get("awnserTwo"));
            awnserThree.setText((CharSequence) savedInstanceState.get("awnserThree"));
            awnserFour.setText((CharSequence) savedInstanceState.get("awnserFour"));
            scoreView.setText(String.valueOf(score = savedInstanceState.getInt("score")));
            remainingQuestionsView.setText(String.valueOf(10 - (clickCount = savedInstanceState.getInt("clickCount"))));
            core = (Root) savedInstanceState.getSerializable("core");
            String correctAwnserRecovery = savedInstanceState.getString("correctAwnser");
            //RESET THE ONCLICK LISTENER FOR THE AWNSER FIELDS
            for (int i = 0; i < questionViews.size(); i++) {
                TextView toSet = (TextView) questionViews.get(i);
                toSet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TIMER FOR DELAYING THE NEXT GET REQUEST
                        Timer timer = new Timer();
                        clickCount++;
                        for (int i = 0; i < questionViews.size(); i++) {
                            TextView toCheck = (TextView) questionViews.get(i);
                            //SET THE BACKGROUND COLOR OF THE CORRECT/INCORRECT ANSWERS
                            if (toCheck.getText().toString().equals(correctAwnserRecovery)) {
                                toCheck.setBackgroundColor(getResources().getColor(R.color.correctGreen));
                                //IMPORTANT-DISABLES TEXTVIEW CLICKING, UNTIL THE NEXT AWNSER IS GENERATED
                                toCheck.setClickable(false);
                            } else {
                                toCheck.setBackgroundColor(getResources().getColor(R.color.incorrectRed));
                                //IMPORTANT-DISABLES TEXTVIEW CLICKING, UNTIL THE NEXT AWNSER IS GENERATED
                                toCheck.setClickable(false);
                            }
                        }
                        //SCORE TRACKING LOGIC

                        TextView tv = (TextView) view;
                        //CORRECT LOGIC
                        if (tv.getText().toString().equals(correctAwnserRecovery)) {
                            score += 1;
                            scoreView.setText("" + score + "");
                            correctSound.start();
                        } else {/*INCORRECT LOGIC*/
                            incorrectSound.start();
                        }
                        //GAME FINISHED LOGIC
                        if (clickCount == 10) {
                            //MESSAGE DETERMINER
                            showDialog("Quiz Over", "You scored: " + score + "/10 ." + messageChecker(score));
                            resetScore();
                            reset();
                            scoreView.setText("" + score + "");
                        }

                        //DELAY THE NEXT GIT REQUEST
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                get();
                            }
                        }, 1000);
                    }
                });

            }
        } else {
            get();
        }
    }

    //RESET THE QUESTIONVIEW LIST AND BACKGROUND COLORS
    public void reset() {
        questionViews = new ArrayList<TextView>(Arrays.asList(awnserOne, awnserTwo, awnserThree, awnserFour));
        for (int i = 0; i < questionViews.size(); i++) {
            TextView sample = (TextView) questionViews.get(i);
            sample.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    public void resetScore() {
        score = 0;
        clickCount = 0;
    }


    //NETWORK AND MAPPING LOGIC
    public void get() {
        remainingQuestionsView.setText("" + (10 - clickCount) + "");
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //HANDLE CASE OF UNAVAILABLE INTERNET
                questionTv.setText("Oops.. Check your internet connection");
                reset();
                for(int i = 0; i<questionViews.size();i++){
                    TextView view = (TextView) questionViews.get(i);
                    view.setText("");
                }
                get();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                //RUNNABLE TO RUN ON UI THREAD
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < questionViews.size(); i++) {
                                TextView toCheck = (TextView) questionViews.get(i);
                                //RESET THE CLICKABLE ATTRIBUTE
                                toCheck.setClickable(true);
                            }
                            //FETCH JSON
                            String json = response.body().string();
                            Gson gson = new Gson();
                            //MAP ROOT AND QUESTION
                            Root roots[] = gson.fromJson(json, Root[].class);
                            core = roots[0];
                            Question q = core.getQuestion();
                            //SETTING THE QUESTION FIELD
                            questionTv.setText(q.getText());
                            //SETTING THE AWNSER FIELDS(RANDOM CHOICE LOGIC)
                            Random random = new Random();
                            int randomIndex = random.nextInt(questionViews.size());
                            TextView correctView = (TextView) questionViews.get(randomIndex);
                            correctView.setText(core.getCorrectAnswer());
                            questionViews.remove(correctView);
                            //LOOP FOR SETTING INCCORECT AWNSERS
                            for (int i = 0; i < questionViews.size(); i++) {
                                TextView target = (TextView) questionViews.get(i);
                                target.setText(core.getIncorrectAnswers().get(i));
                            }
                            //PUTS THE CORRECTAWNSER VIEW BACK INTO SCOPE
                            reset();
                            //BACKGROUND COLOR SETTER and AWNSER REACTION TIMER
                            for (int i = 0; i < questionViews.size(); i++) {
                                boolean isCorrect;
                                TextView target = (TextView) questionViews.get(i);
                                target.setOnClickListener(new View.OnClickListener() {
                                    //LOGIC FOR HANDLING ANSWERS
                                    //SAME LOOP AS THE ON THE ONCREATE METHOD
                                    @Override
                                    public void onClick(View view) {
                                        Timer timer = new Timer();
                                        clickCount++;
                                        for (int i = 0; i < questionViews.size(); i++) {
                                            TextView toCheck = (TextView) questionViews.get(i);
                                            if (toCheck.getText().toString().equals(core.getCorrectAnswer())) {
                                                toCheck.setBackgroundColor(getResources().getColor(R.color.correctGreen));
                                                toCheck.setClickable(false);
                                            } else {
                                                toCheck.setBackgroundColor(getResources().getColor(R.color.incorrectRed));
                                                toCheck.setClickable(false);
                                            }
                                        }
                                        TextView tv = (TextView) view;
                                        if (tv.getText().toString().equals(core.getCorrectAnswer())) {
                                            score += 1;
                                            scoreView.setText("" + score + "");
                                            correctSound.start();
                                        } else {
                                            incorrectSound.start();
                                        }
                                        if (clickCount == 10) {
                                            //MESSAGE DETERMINER
                                            showDialog("Quiz Over", "You scored: " + score + "/10 ." + messageChecker(score));
                                            resetScore();
                                            reset();
                                            scoreView.setText("" + score + "");
                                        }
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                get();
                                            }
                                        }, 1000);
                                    }

                                });
                            }
                        } catch (IOException exc) {
                            exc.printStackTrace();
                        }
                    }
                };

                //CALL THE RUNNABLE
                //SUPER IMPORTANT
                runOnUiThread(runnable);

            }
        });
    }

    //DETERMINES WHAT GAMEOVER MESSAGE THE USER GETS
    public String messageChecker(int Score) {
        String resultAppend = "";
        switch (Score) {
            case 1:
            case 2:
            case 3:
                resultAppend = " Better luck next time";
                badResultSoundPlayer.start();
                break;
            case 4:
            case 5:
            case 6:
                resultAppend = " Not Bad";
                okResultSoundPlayer.setVolume(0.4f, 0.4f);
                okResultSoundPlayer.start();

                break;
            case 7:
            case 8:
            case 9:
                resultAppend = " You are killing it";
                okResultSoundPlayer.setVolume(1.0f, 1.0f);
                okResultSoundPlayer.start();
                break;
            case 10:
                resultAppend = " Perfect Score";
                okResultSoundPlayer.setVolume(1.0f, 1.0f);
                okResultSoundPlayer.start();
                perfectScoreSoundPlayer.start();
                break;
        }

        return resultAppend;
    }


    //DIALOG LOGIC
    //TODO: MAKE A CALLBACK MECHANISM
    //LOGIC TO SHOW THE GAMEOVER_DIALOG
    public void showDialog(CharSequence title, CharSequence message) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        alertDialogFragment alertDialogFragment = new alertDialogFragment();
        alertDialogFragment.setTitle(title.toString());
        alertDialogFragment.setResult(message.toString());
        alertDialogFragment.setEnterTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        alertDialogFragment.show(ft, "dialog");
        alertDialogFragment.onCancel(new DialogInterface() {
            @Override
            public void cancel() {
                reset();
            }

            @Override
            public void dismiss() {
                reset();
            }
        });
    }

    //HANDLES THE STATE OF PRIVATE VARIABLES FOR THE CASE OF ORIENTATION CHANGE
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String correctAwnserString = "";
        outState.putString("question", questionTv.getText().toString());
        outState.putString("awnserOne", awnserOne.getText().toString());
        outState.putString("awnserTwo", awnserTwo.getText().toString());
        outState.putString("awnserThree", awnserThree.getText().toString());
        outState.putString("awnserFour", awnserFour.getText().toString());
        outState.putInt("score", score);
        outState.putInt("clickCount", clickCount);
        outState.putSerializable("core",core);
        if (core != null) {
            correctAwnserString = core.getCorrectAnswer();
            outState.putString("correctAwnser", correctAwnserString);
        }

    }

}
