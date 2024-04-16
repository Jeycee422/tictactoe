package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SoundPool soundPool;
    private int soundWin;
    private int soundPress;
    private int soundDraw;
    String[] board;

    int[][] pattern = {{2,4,6},{0,4,8},{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8}};

    int currentPatternIndex;
    int currentPLayer;
    int playerX;
    int playerO;
    int playerTie;

    String arrayString;
    String winnerText;

    boolean gameWon;

    TextView winner;
    TextView scoreX;
    TextView scoreO;
    TextView scoreTie;
    TextView currPlayer;
    LinearLayout currentPlayerLinear;
    Animation blinkAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("gamePref",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        playerX = sharedPreferences.getInt("playerX",0);
        playerO = sharedPreferences.getInt("playerO",0);
        playerTie = sharedPreferences.getInt("playerTie",0);
        arrayString = sharedPreferences.getString("arrayString","");
        gameWon = sharedPreferences.getBoolean("gameWon",false);
        winnerText = sharedPreferences.getString("winnerText","");
        currentPatternIndex = sharedPreferences.getInt("patternIndex",0);
        currentPLayer = sharedPreferences.getInt("currentPlayer",1);

        String[] retrievedArray;


        blinkAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
        winner = findViewById(R.id.winner);
        winner.setText(winnerText);
        scoreX = findViewById(R.id.scoreX);
        scoreO = findViewById(R.id.scoreO);
        scoreTie = findViewById(R.id.scoreTie);
        scoreX.setText(Integer.toString(playerX));
        scoreO.setText(Integer.toString(playerO));
        scoreTie.setText(Integer.toString(playerTie));
        currPlayer = findViewById(R.id.currentPlayer);
        currentPlayerLinear = findViewById(R.id.currentPlayerLinear);
        currPlayer.setBackgroundResource((currentPLayer == 2) ? R.drawable.o : R.drawable.x);

        soundPool = new SoundPool.Builder().build();

        // Load sound effects
        soundWin = soundPool.load(this, R.raw.win, 1);
        soundPress = soundPool.load(this, R.raw.press, 1);
        soundDraw = soundPool.load(this, R.raw.draw, 1);

        ImageView t1 = findViewById(R.id.t0);
        ImageView t2 = findViewById(R.id.t1);
        ImageView t3 = findViewById(R.id.t2);
        ImageView t4 = findViewById(R.id.t3);
        ImageView t5 = findViewById(R.id.t4);
        ImageView t6 = findViewById(R.id.t5);
        ImageView t7 = findViewById(R.id.t6);
        ImageView t8 = findViewById(R.id.t7);
        ImageView t9 = findViewById(R.id.t8);
        t1.setTag(0);
        t2.setTag(1);
        t3.setTag(2);
        t4.setTag(3);
        t5.setTag(4);
        t6.setTag(5);
        t7.setTag(6);
        t8.setTag(7);
        t9.setTag(8);

        if(!arrayString.isEmpty()) {
            String[] arrayValues = arrayString.substring(1, arrayString.length() -1).split(",");
            retrievedArray = new String[arrayValues.length];
            for(int i = 0; i < arrayValues.length; i++) {
                retrievedArray[i] = arrayValues[i].trim();
            }
            board = Arrays.copyOf(retrievedArray,retrievedArray.length);

            if(isAllEmpty(board)) {
                reset(new View(this));
            }

            GridLayout parentLayout = findViewById(R.id.gridLayout);
            for (int i = 0; i < parentLayout.getChildCount(); i++) {
                View childView = parentLayout.getChildAt(i);


                if (childView instanceof LinearLayout) {
                    LinearLayout linearLayout = (LinearLayout) childView;

                    for (int j = 0; j < linearLayout.getChildCount(); j++) {
                        View innerChildView = linearLayout.getChildAt(j);

                        if (innerChildView instanceof ImageView) {
                            ImageView imageView = (ImageView) innerChildView;

                            if(retrievedArray[i].equals("x")) {
                                imageView.setBackgroundResource(R.drawable.x);
                            }else if(retrievedArray[i].equals("o")) {
                                imageView.setBackgroundResource(R.drawable.o);
                            }
                        }
                    }
                }
            }

            if(gameWon || !gameWon && isBoardFull(board)) {
                setOpacity(pattern[currentPatternIndex]);
                winner.setVisibility(View.VISIBLE);
                currentPlayerLinear.setVisibility(View.INVISIBLE);
                disableClick();
            }

            if(!gameWon && isBoardFull(board)) {
                setAllOpacity();
            }
        }else {
            board = new String[] {"","","",
                    "","","",
                    "","","",};
        }
    }

    public void scoreAnim(View v) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 2.0f,
                1.0f, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(250);
        scaleAnimation.setFillAfter(false);
        v.startAnimation(scaleAnimation);
    }

    public void XOAnim(View v) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                .6f, 1.2f,
                .6f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(250);
        scaleAnimation.setFillAfter(false);
        v.startAnimation(scaleAnimation);
    }

    public void handleClick(View v) {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(v, "scaleX", .8f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(v, "scaleY", .8f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.setDuration(150);


        int idx = (int) v.getTag();
        winner = findViewById(R.id.winner);
        scoreX = findViewById(R.id.scoreX);
        scoreO = findViewById(R.id.scoreO);
        scoreTie = findViewById(R.id.scoreTie);


        if(v.getBackground() == null) {
            animatorSet.start();
            playPress();
            if(currentPLayer == 2) {
                v.setBackgroundResource(R.drawable.o);
                board[idx] = "o";
                currentPLayer--;
                currPlayer.setBackgroundResource(R.drawable.x);
                XOAnim(currPlayer);
            }else {
                v.setBackgroundResource(R.drawable.x);
                board[idx] = "x";
                currentPLayer++;
                currPlayer.setBackgroundResource(R.drawable.o);
                XOAnim(currPlayer);
            }
        }
        for (int i = 0; i < pattern.length;i++) {
            for(int j = 0; j < pattern[i].length;j++) {
                if ((board[pattern[i][0]].equals("x") && board[pattern[i][1]].equals("x") && board[pattern[i][2]].equals("x")) || (board[pattern[i][0]].equals("o") && board[pattern[i][1]].equals("o") && board[pattern[i][2]].equals("o"))) {
                    gameWon = true;
                    animatorSet.end();

                    if(board[pattern[i][j]].equals("x")) {
                        playWin();
                        winnerText = "Player X win! \uD83C\uDFC6";
                        winner.setText(winnerText);
                        playerX++;
                        currentPLayer = 2;
                        String strScore = Integer.toString(playerX);
                        scoreX.setText(strScore);
                        scoreAnim(scoreX);
                    }else if(board[pattern[i][j]].equals("o")) {
                        playWin();
                        winnerText = "Player O win! \uD83C\uDFC6";
                        winner.setText(winnerText);
                        playerO++;
                        currentPLayer = 1;
                        String strScore = Integer.toString(playerO);
                        scoreO.setText(strScore);
                        scoreAnim(scoreO);
                    }
                    currentPatternIndex = i;
                    setOpacity(pattern[i]);
                    setBlink(pattern[i]);
                    break;
                }
            }
            if (gameWon) {
                break;
            }
        }

        if (!gameWon && isBoardFull(board)) {
            winnerText = "It's a Tie!";
            winner.setText(winnerText);
            playDraw();
            playerTie++;
            gridLayout.startAnimation(blinkAnimation);
            String strScore = Integer.toString(playerTie);
            scoreTie.setText(strScore);
            scoreAnim(scoreTie);
            setAllOpacity();
        }

        if (gameWon || !gameWon && isBoardFull(board)) {
            winner.setVisibility(View.VISIBLE);
            currentPlayerLinear.setVisibility(View.INVISIBLE);
            disableClick();
        }
    }

    public void reset(View v) {
        currentPlayerLinear.setVisibility(View.VISIBLE);
        gameWon = false;
        enableClick();
        setOpacityBack();
        winner.setVisibility(View.INVISIBLE);
        board = new String[]{"", "", "",
                "", "", "",
                "", "", ""};
        clearBoard();
    }

    public void clearBoard() {
        GridLayout parentLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            if (childView instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) childView;

                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View innerChildView = linearLayout.getChildAt(j);

                    if (innerChildView instanceof ImageView) {
                        ImageView imageView = (ImageView) innerChildView;

                        imageView.setBackground(null);
                    }
                }
            }
        }
    }

    public void restart(View v) {

        playerX = 0;
        playerO = 0;
        playerTie = 0;
        arrayString = Arrays.toString(new String[] {"","","","","","","","","",});
        board = new String[] {"","","","","","","","","",};
        gameWon = false;
        currentPLayer = 1;
        editor.apply();
        currPlayer.setBackgroundResource(currentPLayer == 1 ? R.drawable.x: R.drawable.o);
        scoreX.setText(String.format("%d",playerX));
        scoreO.setText(Integer.toString(playerO));
        scoreTie.setText(Integer.toString(playerTie));
        currentPlayerLinear.setVisibility(View.VISIBLE);
        winner.setVisibility(View.INVISIBLE);
        enableClick();
        setOpacityBack();
        clearBoard();
    }

    public void disableClick() {
        GridLayout parentLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            if (childView instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) childView;

                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View innerChildView = linearLayout.getChildAt(j);

                    if (innerChildView instanceof ImageView) {
                        ImageView imageView = (ImageView) innerChildView;
                        imageView.setClickable(false);
                    }
                }
            }
        }
    }

    public void enableClick() {
        GridLayout parentLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            if (childView instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) childView;

                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View innerChildView = linearLayout.getChildAt(j);

                    if (innerChildView instanceof ImageView) {
                        ImageView imageView = (ImageView) innerChildView;

                        imageView.setClickable(true);
                    }
                }
            }
        }
    }

    public void setBlink(int[] array) {
        GridLayout parentLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            if ((childView instanceof LinearLayout) && (i == array[0] || i == array[1] || i == array[2])) {
                LinearLayout linearLayout = (LinearLayout) childView;

                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View innerChildView = linearLayout.getChildAt(j);

                    if (innerChildView instanceof ImageView) {
                        ImageView imageView = (ImageView) innerChildView;
                        imageView.startAnimation(blinkAnimation);
                    }
                }
            }
        }
    }

    public void setOpacity(int[] array) {
        GridLayout parentLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            if ((childView instanceof LinearLayout) && !(i == array[0] || i == array[1] || i == array[2])) {
                LinearLayout linearLayout = (LinearLayout) childView;

                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View innerChildView = linearLayout.getChildAt(j);

                    if (innerChildView instanceof ImageView) {
                        ImageView imageView = (ImageView) innerChildView;

                        imageView.setAlpha(0.3f);
                    }
                }
            }
        }
    }

    public void setAllOpacity() {
        GridLayout parentLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            if (childView instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) childView;

                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View innerChildView = linearLayout.getChildAt(j);

                    if (innerChildView instanceof ImageView) {
                        ImageView imageView = (ImageView) innerChildView;

                        imageView.setAlpha(0.3f);
                    }
                }
            }
        }
    }

    public void setOpacityBack() {
        GridLayout parentLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            if (childView instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) childView;

                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View innerChildView = linearLayout.getChildAt(j);

                    if (innerChildView instanceof ImageView) {
                        ImageView imageView = (ImageView) innerChildView;

                        imageView.setAlpha(1f);
                    }
                }
            }
        }
    }

    boolean isBoardFull(String[] board) {
        for (String cell : board) {
            if (cell.equals("")) {
                return false;
            }
        }
        return true;
    }

    boolean isAllEmpty(String[] arr) {

        boolean allEmpty = true;
        for(String el : arr) {
            if(!el.isEmpty()) {
                allEmpty = false;
                break;
            }
        }
        return allEmpty;
    }

     private void playWin() {
         soundPool.play(soundWin, 1.0f, 1.0f, 1, 0, 1.0f);
     }

     private void playPress() {
         soundPool.play(soundPress, .5f, .5f, 0, 0, 1.8f);
     }

     private void playDraw() {
        soundPool.play(soundDraw,1.0f,1.0f,1,0,1.0f);
     }

    @Override
    protected void onPause() {
        super.onPause();

//        editor = sharedPreferences.edit();
        editor.putInt("playerO",playerO);
        editor.putInt("playerX",playerX);
        editor.putInt("playerTie",playerTie);
        editor.putString("arrayString", Arrays.toString(board));
        editor.putBoolean("gameWon",gameWon);
        editor.putString("winnerText",winnerText);
        editor.putInt("patternIndex", currentPatternIndex);
        editor.putInt("currentPlayer",currentPLayer);
        editor.apply();
    }
}