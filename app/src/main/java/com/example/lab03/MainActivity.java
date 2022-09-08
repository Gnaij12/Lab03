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
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                for (TextView temp:views) {
                    temp.setTextSize(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { //record state
                lastProgress = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { //pop snackbar
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
                                    Snackbar.make(layout,"Font Size Reverted Back To " + lastProgress + "sp",Snackbar.LENGTH_LONG);
                                }
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
                return false;
            }
        });
    }

    private void setInitialValues() {
        for (TextView temp:views) {
            temp.setText(sP.getString(temp.getTag().toString(),"0"));
        }
        seekBar.setProgress(30);
    }

    @Override
    public void onClick(View v) {
        TextView temp = (TextView) v;
        temp.setText("" + (Integer.parseInt(temp.getText().toString())+1));
        editor.putString(temp.getTag().toString(),temp.getText().toString());
        editor.apply();
        cPS = ++clicks/((System.currentTimeMillis()-startTime)/1000f);
        Toast.makeText(this,""+cPS, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setInitialValues();
    }
}