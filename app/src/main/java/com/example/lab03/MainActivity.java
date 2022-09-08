package com.example.lab03;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView topLeft,topRight,botLeft,botRight;
    SharedPreferences sP;
    SharedPreferences.Editor editor;
    String TAG = "com.tradan.Lab03.sharedprefs";
    SeekBar seekBar;
    TextView[] views;
    ConstraintLayout layout;
    long startTime,clicks;
    float cPS;
    int multiplier = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topLeft = findViewById(R.id.topLeft);
        topRight = findViewById(R.id.topRight);
        botLeft = findViewById(R.id.botLeft);
        botRight = findViewById(R.id.botRight);
        views = new TextView[]{botRight,botLeft,topRight,topLeft};
        sP = getSharedPreferences(TAG,MODE_PRIVATE);
        editor = sP.edit();
        sP.getString("mResponse","defaultString");
        sP.getInt("mResponseNum",99);
        seekBar = findViewById(R.id.seekBar);
        setInitialValues();
        layout = findViewById(R.id.activityMainLayout);
        startTime = System.currentTimeMillis();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int lastProgress;
            int currentProgress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                for (TextView temp:views) {
                    temp.setTextSize(progress);
                }
                editor.putInt(seekBar.getTag().toString(),seekBar.getProgress());
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { //record state
                lastProgress = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { //pop snackbar
                currentProgress = seekBar.getProgress();
                Snackbar snackbar = Snackbar.make(layout,
                        "Font Size Changed To " + seekBar.getProgress() + "sp",
                        Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                seekBar.setProgress(lastProgress);
                                for (TextView temp:views) {
                                    temp.setTextSize(lastProgress);
                                }
                                Snackbar tempSnackBar = Snackbar.make(layout,"Font Size Reverted Back To " + lastProgress + "sp",Snackbar.LENGTH_LONG);
                                tempSnackBar.setAction("REDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        seekBar.setProgress(currentProgress);
                                        for (TextView temp:views) {
                                            temp.setTextSize(currentProgress);
                                        }
                                        Snackbar redoSnackBar = Snackbar.make(layout,"Font Size Rereverted Back To " + currentProgress + "sp",Snackbar.LENGTH_LONG);
                                        View redoSnackBarView = redoSnackBar.getView();
                                        TextView redoTextView = redoSnackBarView.findViewById(R.id.snackbar_text);
                                        redoTextView.setTextColor(Color.RED);
                                        redoSnackBar.show();
                                    }
                                });
                                tempSnackBar.setActionTextColor(Color.RED);
                                View tempSnackBarView = tempSnackBar.getView();
                                TextView tempTextView = tempSnackBarView.findViewById(R.id.snackbar_text);
                                tempTextView.setTextColor(Color.MAGENTA);
                                tempSnackBar.show();
                            }
                        });
                snackbar.setActionTextColor(Color.MAGENTA);
                View snackBarView = snackbar.getView();
                TextView textView = snackBarView.findViewById(R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        });
        topLeft.setOnClickListener(this);
        topRight.setOnClickListener(this);
        botLeft.setOnClickListener(this);
        botRight.setOnClickListener(this);
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editor.clear().apply();
                setInitialValues();
                Snackbar snackbar = Snackbar.make(layout,
                        "Everything Reset!",Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                TextView textView = snackBarView.findViewById(R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
                startTime = System.currentTimeMillis();
                clicks = 0;
                return false;
            }
        });
    }

    private void setInitialValues() {
        for (TextView temp:views) {
            temp.setText(sP.getString(temp.getTag().toString(),"0"));
        }
        seekBar.setProgress(sP.getInt(seekBar.getTag().toString(),30));
        int progress = seekBar.getProgress();
        for (TextView temp:views) {
            temp.setTextSize(progress);
        }
    }

    @Override
    public void onClick(View v) {
        TextView temp = (TextView) v;
        temp.setText("" + (Integer.parseInt(temp.getText().toString())+multiplier));
        editor.putString(temp.getTag().toString(),temp.getText().toString());
        editor.apply();
        cPS = multiplier*(++clicks/((System.currentTimeMillis()-startTime)/1000f));
        Toast.makeText(this,""+cPS, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setInitialValues();
    }

    public void ten(View view) {
        multiplier = 10;
    }
    public void one(View view) {
        multiplier = 1;
    }
}