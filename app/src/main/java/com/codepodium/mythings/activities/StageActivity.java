package com.codepodium.mythings.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepodium.mythings.Adapters.MyGridAdapter;
import com.codepodium.mythings.GameManager;
import com.codepodium.mythings.MyUtilites;
import com.codepodium.mythings.R;
import com.codepodium.mythings.db.MyDataSource;
import com.codepodium.mythings.model.ObjectFrame;
import com.codepodium.mythings.model.ObjectLocationData;
import com.codepodium.mythings.model.StageResources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;


public class StageActivity extends Activity {

    GameManager gm = GameManager.getInstance();

    Button btnHint;
    TextView tvCounter, tvScore;
    FrameLayout flGameScene = null;
    RelativeLayout rlStage = null;
    LayoutInflater inflater;

    boolean hasObjectsPlaced = true;
    boolean isBackButtonPressed = false;
    boolean isNewStage = true;
    int score = gm.getCURRENT_STAGE_SCORE();
    int hints = 10;
    int cTimer = 0;

    StageResources stageDetail;
    Dialog dialog;
    GridView gvObjects;
    CountDownTimer countDownTimer;

    MediaPlayer mpBGM = null;
    Bitmap bmp;



    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Getting state Resources to use.
        stageDetail = gm.getStageResources();


        // Decoding image file to Android Bitmap Object
        bmp = BitmapFactory.decodeResource(getResources(), stageDetail.getSpriteSheet());

        // Setting activity screen to full screen
        MyUtilites.fullscreen(this);

        // Setting Layout to load
        setContentView(R.layout.activity_stage);

        // Playing BGM
        mpBGM = MediaPlayer.create(this, stageDetail.getStageBGM());
        mpBGM.setVolume(1.0f, 1.0f);
        mpBGM.start();

        // Getting reference for frame layout.
        flGameScene = (FrameLayout) findViewById(R.id.flgamesecne);

        // Initialing RelativeLayout that will be added to the frame layout.
        rlStage = new RelativeLayout(this);
        // Adding background image to RelativeLayout for current stage.
        rlStage.setBackgroundResource(stageDetail.getStageBackground());
        // Now adding RelativeLayout to FrameLayout.
        flGameScene.addView(rlStage);


        // Getting reference to TextView
        tvCounter = (TextView) findViewById(R.id.tvCountDown);
        tvScore = (TextView) findViewById(R.id.tvScore);

        // Getting reference to Buttons
        btnHint = (Button) findViewById(R.id.btnHint);
        btnHint.setText("HINT - " + hints);

        // Changing font of TextView Counter
        Typeface tf = Typeface.createFromAsset(getAssets(), "countdown.ttf");
        tvCounter.setTypeface(tf);
        tvScore.setTypeface(tf);

        // Checking if StageActivity has to load saved stage or start from 1st stage.
        if(getIntent().hasExtra("load"))
        {
            isNewStage = false;

            // Start from save state
            //stageDetail.setStageTime(gm.getCURRENT_REMAINING_TIME());

            hints = gm.getCURRENT_HINTS();

            for(String key : gm.getObjectLocations().keySet())
            {
                ObjectFrame of = gm.getFinalObjectList().get(key);
                Bitmap oBmp = Bitmap.createBitmap(bmp, of.getX(), of.getY(), of.getW(), of.getH());
                ImageView imageView = new ImageView(this);
                imageView.setOnClickListener(new ImageFoundListener(imageView, key));
                imageView.setImageBitmap(oBmp);
                RelativeLayout.LayoutParams lp;
                ObjectLocationData old = gson.fromJson(gm.getObjectLocations().get(key).toString(), ObjectLocationData.class);
                lp = new RelativeLayout.LayoutParams(old.getWidth(), old.getHeight());
                lp.addRule(old.getRule1());
                lp.addRule(old.getRule2());
                lp.setMargins(old.getLeft(), old.getTop(), old.getRight(), old.getBottom());
                imageView.setLayoutParams(lp);
                rlStage.addView(imageView);
            }

            tvCounter.setText("T: " + gm.getCURRENT_REMAINING_TIME());
            btnHint.setText("HINT - " + hints);
            //tvScore.setText("S: " + gm.getCURRENT_STAGE_SCORE());
            getIntent().removeExtra("load");
        }

        tvScore.setText("S: " + gm.getCURRENT_STAGE_SCORE());






        // Getting Layout for Object Dialog. Which have GridView and a button in it.
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.objectstofind, null);

        // Getting Reference for GridView and Button from "dialogView" View.
        Button btnFindObjects = (Button) dialogView.findViewById(R.id.btnFind);
        gvObjects = (GridView) dialogView.findViewById(R.id.gvObjects);




        // Initializing data adapter for grid view. That will show objects
        MyGridAdapter adapter = new MyGridAdapter(this,
                R.layout.single_object,
                _SelectObjects(stageDetail.getSpritePoints()),
                bmp,
                gm.getIV_WIDTH());

        // Assigning adapter to GridView.
        gvObjects.setAdapter(adapter);




        // Building and showing AlertDialog box instance that will display objects need to be found.

        dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        dialog.show();
        dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);


        // Adding OnClickListener on the Button that I got from "dialogView"
        btnFindObjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasObjectsPlaced) {
                    hasObjectsPlaced = false;

                    // closing the dialog box
                    dialog.dismiss();

                    if(isNewStage) {
                        // Placing Objects in RelativeLayout that need to be found
                        placeObjects(StageActivity.this, bmp);
                    }

                    // starting Game counter
                    countDownTimer.start();
                }
                else
                {
                    dialog.dismiss();
                }
            }
        });



        // Setting Click Listener on Hint button. The action will be preformed when user clicks it
        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHint();
            }
        });

        rlStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score -= 2;
                gm.setCURRENT_STAGE_SCORE(score);
                tvScore.setText("S: " + score);
                playScoreLessSound();
            }
        });


        // Stage Count Down Timer
        countDownTimer = new CountDownTimer(stageDetail.getStageTime(), 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                cTimer = (int) millisUntilFinished/1000;
                tvCounter.setText("T: " + cTimer);
            }

            @Override
            public void onFinish() {
                tvCounter.setText("T: 0");
                rlStage.setOnClickListener(null);
                Toast.makeText(StageActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
                showGameOverDialog();
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Setting activity screen to full screen
        MyUtilites.fullscreen(this);
        if(mpBGM != null)
            mpBGM.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mpBGM != null)
            mpBGM.pause();
    }

    @Override
    public void onBackPressed() {

        if(isBackButtonPressed) {
            // stop count down timer
            countDownTimer.cancel();
            // before exit save high score
            saveHighScore();
            // call super method of onBackPressed to finish current activity
            super.onBackPressed();
            finish();
        }
        else {
            // setting "isBackButtonPressed" to 'true' for keeping track of button pressed.
            isBackButtonPressed = true;
            // Displaying Toast Message to use for conformation and pressing back button again.
            Toast.makeText(this, "Press again to go back", Toast.LENGTH_SHORT).show();
            // Starting an Thread that waits for 2 seconds and then again sets the value of "isBackButtonPressed" to 'false'
            // if user presses back button before 2 seconds then activity ends otherwise this process reStarts.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Making thread sleep for 2 seconds.
                        // Value is given in milliseconds. So 2000 milliseconds mean 2 Seconds.
                        Thread.sleep(2000);
                        isBackButtonPressed = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_MENU)
            showMenuDialog();
        return super.onKeyDown(keyCode, event);
    }



    private void saveStage()
    {
        MyDataSource db = new MyDataSource(this);
       // db.open();
        gm.setCURRENT_HINTS(hints);
        gm.setCURRENT_REMAINING_TIME(cTimer);
        countDownTimer.cancel();
        ArrayList<View> alIV = new ArrayList<>();
        //for(int i = 0; i < rlStage.getChildCount(); i++)
         //   alIV.add(rlStage.getChildAt(i));
        //gm.setObjectLocations(alIV);
        db.insertGameData();
        //db.close();
    }

    private void showHint()
    {
        if(hints > 0) {
            if(rlStage != null) {
                int childCount = rlStage.getChildCount();
                if(childCount > 0) {
                    ImageView vHint = (ImageView) rlStage.getChildAt((new Random()).nextInt(childCount));
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vHint.getLayoutParams();
                    params.width = (int) (params.width * 1.5f);
                    params.height = (int) (params.height * 1.5f);
                    vHint.setLayoutParams(params);
                    vHint.setBackgroundResource(R.drawable.hintborder);
                    hints--;
                    btnHint.setText("HINT - " + hints);
                }
            }

        }
    }

    private void saveHighScore()
    {
        MyDataSource db = new MyDataSource(this);
        db.insertHighScore(gm.getCURRENT_STAGE_SCORE(), gm.getPlayerName());
    }

    private void placeObjects(Context context, Bitmap bmp)
    {


        if(stageDetail != null) {



            //int spriteSheetResource = (int) stageDetail[0];
            //Bitmap bmp = BitmapFactory.decodeResource(getResources(), spriteSheetResource);

            for(int i = 0; i < 10; i++) {
                String objName = gm.getObjectKeys().get(i);
                ObjectFrame of = gm.getFinalObjectList().get(objName);

                final ImageView imageView = new ImageView(context);
                Bitmap oBmp = Bitmap.createBitmap(bmp, of.getX(), of.getY(), of.getW(), of.getH());
                imageView.setImageBitmap(oBmp);

                Random random = new Random();
                int type = random.nextInt(6);
                RelativeLayout.LayoutParams lp;
                int width = of.getW();
                int height = of.getH();

                if (width <= 50 || height <= 50) {
                    width *= 1.8f;
                    height *= 1.8f;
                }

                int left = random.nextInt(250);
                int top = random.nextInt(500);
                int right = random.nextInt(250);
                int bottom = random.nextInt(500);

                ObjectLocationData old;

            switch (type)
            {
                case 0:
                    lp = new RelativeLayout.LayoutParams(width, height);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    right = bottom = 0;
                    lp.setMargins(left, top, 0, 0);
                    imageView.setLayoutParams(lp);
                    old = new ObjectLocationData(left, top, right, bottom, height, width, RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.ALIGN_PARENT_LEFT);
                    break;
                case 1:
                    lp = new RelativeLayout.LayoutParams(width, height);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    right = top = 0;
                    lp.setMargins(left, 0, 0, bottom);
                    imageView.setLayoutParams(lp);
                    old = new ObjectLocationData(left, top, right, bottom, height, width, RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.ALIGN_PARENT_LEFT);
                    break;
                case 2:
                    lp = new RelativeLayout.LayoutParams(width, height);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    left = bottom = 0;
                    lp.setMargins(0, top, right, 0);
                    imageView.setLayoutParams(lp);
                    old = new ObjectLocationData(left, top, right, bottom, height, width, RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.ALIGN_PARENT_LEFT);
                    break;
                case 3:
                    lp = new RelativeLayout.LayoutParams(width, height);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    left = bottom = 0;
                    lp.setMargins(0, top, right, 0);
                    imageView.setLayoutParams(lp);
                    old = new ObjectLocationData(left, top, right, bottom, height, width, RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.ALIGN_PARENT_LEFT);
                    break;
                case 4:
                    lp = new RelativeLayout.LayoutParams(width, height);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    left = right = bottom = 0;
                    lp.setMargins(0, top, 0, 0);
                    imageView.setLayoutParams(lp);
                    old = new ObjectLocationData(left, top, right, bottom, height, width, RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.ALIGN_PARENT_LEFT);
                    break;
                default:
                    lp = new RelativeLayout.LayoutParams(width, height);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    lp.addRule(RelativeLayout.CENTER_VERTICAL);
                    top = bottom = right = 0;
                    lp.setMargins(left, 0, 0, 0);
                    imageView.setLayoutParams(lp);
                    old = new ObjectLocationData(left, top, right, bottom, height, width, RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.ALIGN_PARENT_LEFT);
                    break;
            }

                imageView.setOnClickListener(new ImageFoundListener(imageView, objName));

                gm.addObjectToLocationHolder(objName, gson.toJson(old));
            rlStage.addView(imageView, lp);
            }
        }
    }

    private class ImageFoundListener implements View.OnClickListener {

        ImageView ivRemove;
        String objName;

        public ImageFoundListener(ImageView ivRemove, String objName) {
            this.ivRemove = ivRemove;
            this.objName = objName;
        }

        @Override
        public void onClick(View v) {
            rlStage.removeView(ivRemove);

            score += 10;
            gm.setCURRENT_STAGE_SCORE(score);
            tvScore.setText("S: " + score);
            playSoundFound();
            int remainingObjects = gm.removeObjectFromList(this.objName);
            if(remainingObjects == 0)
            {
                countDownTimer.cancel();
                rlStage.setOnClickListener(null);
                showCompletionDialog();
            }
        }
    }

    private void playSoundFound()
    {
        MediaPlayer mpFound;
        mpFound = MediaPlayer.create(this, R.raw.found);
        mpFound.setVolume(1.0f, 1.0f);
        mpFound.start();
    }

    private void playScoreLessSound()
    {
        MediaPlayer mp;
        mp = MediaPlayer.create(this, R.raw.less);
        mp.setVolume(1.0f, 1.0f);
        mp.start();
    }

    private void showCompletionDialog()
    {
        View vCompletionDialog = inflater.inflate(R.layout.stage_complete_dialog, null);
        final Dialog dialog1 = new Dialog(this);
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(vCompletionDialog);

        Button btnExit = (Button) vCompletionDialog.findViewById(R.id.btnEndGame);
        Button btnSave = (Button) vCompletionDialog.findViewById(R.id.btnSave);
        Button btnRestart = (Button) vCompletionDialog.findViewById(R.id.btnRestart);
        Button btnNext = (Button) vCompletionDialog.findViewById(R.id.btnNext);
        TextView tvFinalScore = (TextView) vCompletionDialog.findViewById(R.id.tvFinalScore);

        tvFinalScore.setText("Your Final Score: " + gm.getCURRENT_STAGE_SCORE());

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // before exit save high score
                saveHighScore();
                finish();
                dialog1.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gm.setCURRENT_STAGE(gm.getCURRENT_STAGE()+1);
                saveStage();
                dialog1.dismiss();
                finish();
            }
        });
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                recreate();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gm.setCURRENT_STAGE(gm.getCURRENT_STAGE()+1);
                dialog1.dismiss();
                recreate();
            }
        });

        dialog1.show();
        dialog1.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
    }

    private void showGameOverDialog()
    {
        View vGameOver = inflater.inflate(R.layout.game_over_dialog, null);

        Button btnExit = (Button) vGameOver.findViewById(R.id.btnGameOverExit);
        Button btnRestart = (Button) vGameOver.findViewById(R.id.btnGameOverRestart);
        TextView tvObjectsRemaining = (TextView) vGameOver.findViewById(R.id.tvObjectsRemaining);

        tvObjectsRemaining.setText(gm.getFinalObjectList().size() + " - Objects not found");


        final Dialog dialog2 = new Dialog(this);
        dialog2.setCancelable(false);
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(vGameOver);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // before exit save high score
                saveHighScore();
                gm.setCURRENT_STAGE_SCORE(0);
                finish();
            }
        });
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                gm.setCURRENT_STAGE_SCORE(0);
                recreate();
            }
        });

        try {
            dialog2.show();
            dialog2.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMenuDialog()
    {
        View v = View.inflate(this, R.layout.dialog_stage_menu, null);

        Button btnExit = (Button) v.findViewById(R.id.btnMenuExit);
        Button btnSave = (Button) v.findViewById(R.id.btnMenuSave);
        Button btnList = (Button) v.findViewById(R.id.btnMenuObjectList);
        Button btnHint = (Button) v.findViewById(R.id.btnMenuHint);


        final Dialog menuDialog = new Dialog(this);
        menuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        menuDialog.setContentView(v);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
                saveStage();
                stageDetail.setStageTime(cTimer);
                countDownTimer.start();
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
                gvObjects.deferNotifyDataSetChanged();
                dialog.show();
            }
        });

        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
                showHint();
            }
        });


        menuDialog.show();
        menuDialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        menuDialog.getWindow().setLayout(250, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    private HashMap<String, ObjectFrame> _SelectObjects(String stateSpriteSheetName)
    {
        HashMap<String, ObjectFrame> finalList = new HashMap<>();
        if(isNewStage) {
            try {
                final HashMap<String, ObjectFrame> spritePoints = new HashMap<>();
                final String[] keys20 = new String[20];
                String s = "";
                //InputStream is = getResources().openRawResource(R.raw.sp);
                InputStream is = getAssets().open(stateSpriteSheetName);
                StringBuffer sb = new StringBuffer();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                if (is != null) {
                    while ((s = br.readLine()) != null) {
                        sb.append(s);
                    }
                    is.close();
                    br.close();
                }

                JSONObject jObj = new JSONObject(sb.toString());
                if (jObj.has("frames")) {
                    JSONObject jo = jObj.getJSONObject("frames");
                    Iterator<String> i = jo.keys();

                    int c = 0;
                    while (i.hasNext()) {
                        String key = i.next().toString();
                        keys20[c++] = key;
                        spritePoints.put(key, new ObjectFrame(jo.getJSONObject(key).getJSONObject("frame")));
                    }

                }

                Random random = new Random();
                int keyIndex;
                ArrayList<String> keys10 = gm.getObjectKeys();

                int j = 0;
                while (j < 10) {
                    keyIndex = random.nextInt(20);
                    if (!finalList.containsKey(keys20[keyIndex])) {
                        finalList.put(keys20[keyIndex], spritePoints.get(keys20[keyIndex]));
                        keys10.add(keys20[keyIndex]);
                        j++;
                    }
                }

                gm.setObjectKeys(keys10);
                gm.setFinalObjectList(finalList);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            // use the list populated form saved stage data
            finalList = gm.getFinalObjectList();
        }
        return finalList;
    }




}
