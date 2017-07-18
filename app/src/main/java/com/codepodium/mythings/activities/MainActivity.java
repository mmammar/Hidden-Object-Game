package com.codepodium.mythings.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepodium.mythings.Adapters.MyListViewHighScore;
import com.codepodium.mythings.GameManager;
import com.codepodium.mythings.MyUtilites;
import com.codepodium.mythings.R;
import com.codepodium.mythings.db.MyDataSource;


public class MainActivity extends Activity {

    MediaPlayer mp = null;

    private TextView tvMyThings;
    private Button btnNewGame, btnContinue, btnHighScore, btnExit;
    private FrameLayout flMainMenu;
    private RelativeLayout flHighScore;

    private boolean isHighScoreShowing = false;
    private boolean isBackButtonPressed = false;

    Animation fadeIn;
    Animation fadeOut;

    GameManager gm = null;

    ListView lvHighScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing Game Manager
        // Just calling one time getInstance() will initialize game manager.
        gm = GameManager.getInstance();

        // Setting activity screen to full screen
        MyUtilites.fullscreen(this);

        // Setting reference to layout to load
        setContentView(R.layout.activity_main);

        // Playing Background sound
        mp = MediaPlayer.create(this, R.raw.bgmain);
        mp.setVolume(1.0f, 1.0f);
        mp.setLooping(true);
        mp.start();

        // Getting reference to Buttons
        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnExit = (Button) findViewById(R.id.btnExit);
        btnHighScore = (Button) findViewById(R.id.btnHighScore);
        btnNewGame = (Button) findViewById(R.id.btnNewGame);

        // Getting reference for TextView
        tvMyThings = (TextView) findViewById(R.id.tvMyThings);

        // Getting reference for FrameLayout
        flMainMenu = (FrameLayout) findViewById(R.id.flMenuList);

        // Getting reference for RelativeLayout of HighScore
        flHighScore = (RelativeLayout) findViewById(R.id.flHighScore);

        // Getting reference for listView of High Score
        lvHighScore = (ListView) findViewById(R.id.lvHighScore);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);


        try {
            Typeface tf = Typeface.createFromAsset(getAssets(), "comic.ttf");
            btnContinue.setTypeface(tf);
            btnExit.setTypeface(tf);
            btnHighScore.setTypeface(tf);
            tvMyThings.setTypeface(tf);
            btnNewGame.setTypeface(tf);
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Font - Exception", Toast.LENGTH_SHORT).show();
        }

        // Showing menu after 1 second
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // This Thread sleeps for 1 second. So menu show after 1 second.
                    // 1000 milli seconds = 1 second
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    // To interact with UI elements we need to shift on UI main Thread !!
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // UI element
                            flMainMenu.setVisibility(FrameLayout.VISIBLE);

                            // Starting Animation
                            flMainMenu.setAnimation(fadeIn);
                            fadeIn.start();
                        }
                    });

                }
            }
        }).start();







        // Setting action of Click to High Score Button
        btnHighScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Setting ListView
                MyDataSource db = new MyDataSource(MainActivity.this);
                MyListViewHighScore listAdapter = new MyListViewHighScore(MainActivity.this, R.layout.row_high_score, db.getHighScores());
                lvHighScore.setAdapter(listAdapter);

                // Hiding Game menu
                flMainMenu.setVisibility(FrameLayout.GONE);
                flMainMenu.setAnimation(fadeOut);
                fadeOut.start();

                // Showing High Score
                flHighScore.setVisibility(FrameLayout.VISIBLE);
                // Setting this variable to true so we can keep track of what is showing

                // Starting Animation
                flHighScore.setAnimation(fadeIn);
                fadeIn.start();

                isHighScoreShowing = true;

            }
        });
        // Setting action to Exit button.
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Here can't write "this" only it has to be "MainActivity.this" because we are not
                 * in "ManiActivity" class but in new instance of OnClickListener class.
                 *
                 * Displays a message at bottom of screen.
                 */
                Toast.makeText(MainActivity.this, "Good Bye", Toast.LENGTH_SHORT).show();

                // Finishes current visible Activity
                finish();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        // Setting action to New Game Button
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(gm.getPlayerName().length() > 0)
                {
                    // Stopping Media player
                    mp.stop();
                    gm.resetGame();

                    // Starting new Activity for game play
                    Intent intent = new Intent(MainActivity.this, StageActivity.class);
                    startActivity(intent);
                }
                else {

                    final Dialog dialog = new Dialog(MainActivity.this);
                    View nameDialogView = View.inflate(MainActivity.this, R.layout.player_name_dialog, null);
                    final EditText etPlayer = (EditText) nameDialogView.findViewById(R.id.etPlayerName);

                    etPlayer.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                                gm.setCURRENT_STAGE(1);
                                gm.setCURRENT_STAGE_SCORE(0);
                                gm.setPlayerName(etPlayer.getText().toString());

                                // Closing dialog box
                                dialog.dismiss();

                                // Stopping Media player
                                mp.stop();
                                gm.resetGame();

                                // Starting new Activity for game play
                                Intent intent = new Intent(MainActivity.this, StageActivity.class);
                                startActivity(intent);
                                return true;
                            }
                            return false;
                        }
                    });


                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(nameDialogView);
                    dialog.show();
                    dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

                }
            }
        });
        // Setting on click action to Continue Button
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDataSource db = new MyDataSource(MainActivity.this);
                //db.open();
                boolean flag = db.loadGameData();
                //db.close();
                if(flag) {
                    Intent intent = new Intent(MainActivity.this, StageActivity.class);
                    intent.putExtra("load", gm.getCURRENT_STAGE());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No Data found to Load", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // getting screen dimensions for later use
        MyUtilites.screenDimensionsPX(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mp != null)
            mp.start();
        // Setting activity screen to full screen
        MyUtilites.fullscreen(this);

    }

    @Override
    protected void onPause() {
        if(mp != null)
            mp.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mp != null)
            mp.pause();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        /**
         * First time High Score screen is hided if shown.
         * Then user is given 2 seconds to decide if he/she wants to exit or not
         */
        if(isHighScoreShowing) {
            flHighScore.setVisibility(FrameLayout.GONE);
            flHighScore.setAnimation(fadeOut);
            fadeOut.start();

            flMainMenu.setVisibility(FrameLayout.VISIBLE);
            flMainMenu.setAnimation(fadeIn);
            fadeIn.start();
        }
        else {
                if(isBackButtonPressed) {
                    super.onBackPressed();
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    Toast.makeText(this, "Good Bye", Toast.LENGTH_SHORT).show();
                }
                else {
                        isBackButtonPressed = true;
                        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                    isBackButtonPressed = false;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                }
        }
    }


}
