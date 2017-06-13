package com.example.hisyamsaimin.findrone;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import static com.example.hisyamsaimin.findrone.R.id.chronometer;
import static java.lang.System.*;


public class SecondPage extends AppCompatActivity {

    EditText et, et2;
    TextView tv, tv_numofDrone;
    TextView seekBarValue;
    String areaWidth, areaLength;
    Integer areaWidth1, areaLength1, HalfWidth;
    Integer seekBarValueInt;
    Button bt;
    static String message = "";
    //private static String ip="10.218.204.41";
    //private static Socket drone1,drone2;
    PrintWriter out = null;
    SocketManager socketManager;
    //static String droneInstruction1, droneInstruction2;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_page);

        bt = (Button) findViewById(R.id.SetArea);
        et = (EditText) findViewById(R.id.width);
        et2 = (EditText) findViewById(R.id.length);
        tv = (TextView) findViewById(R.id.widthEstimation);
        tv_numofDrone = (TextView) findViewById(R.id.numOfDrone);

        try {
            socketManager = SocketManager.getSocketManager();
            socketManager.setSecondPage(this);
            socketManager.set_textView_numberDrones(tv_numofDrone);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: notify problems with socket manager
        }

        //All you need to do is Extend all the activities with BaseActivity. The app never crashes at any point
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error" + Thread.currentThread().getStackTrace()[2], paramThrowable.getLocalizedMessage());
            }
        });



        SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);
        seekbar.setProgress(0);
        seekbar.incrementProgressBy(10);
        seekbar.setMax(0);


        //button SET on click function
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //TODO: check if number of drone is 2
                areaLength = et2.getText().toString();
                areaLength1 = Integer.parseInt(areaLength);
                HalfWidth = areaWidth1 / 2;
                AlertDialog.Builder a_builder = new AlertDialog.Builder(SecondPage.this);
                a_builder.setMessage("The dimension you have selected is " + areaWidth + "m (width) X " + areaLength + "m (length)." +
                        " Press Start to start searching or Cancel to define area again.")
                        .setCancelable(false)
                        .setPositiveButton("Start", new DialogInterface.OnClickListener() {

                            @Override
                            //here the passing value of width, length and drone start position
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(SecondPage.this, MapView.class);
                                //Bundle params = new Bundle();
                                // params.putDouble("areaWidth", areaWidth1);
                                // params.putDouble("areaLength", areaLength1);
                                //params.putDouble("droneStartPosition", seekBarValueInt);
                                //i.putExtras(params);

                                try {
                                    Integer droneStartPosition2 = seekBarValueInt - HalfWidth;

                                    String configure_drone1 = SocketManager.COMMAND_CONFIGURE + ":" + SocketManager.DATA_WIDTH + "=" + HalfWidth + ";" +
                                            SocketManager.DATA_LENGTH + "=" + areaLength1 + ";" + SocketManager.DATA_POSITION + "=" + seekBarValueInt;

                                    String configure_drone2 = SocketManager.COMMAND_CONFIGURE + ":" + SocketManager.DATA_WIDTH + "=" + HalfWidth + ";" +
                                            SocketManager.DATA_LENGTH + "=" + areaLength1 + ";" + SocketManager.DATA_POSITION + "=" + droneStartPosition2;

                                    socketManager.send_to_drone1(configure_drone1);
                                    socketManager.send_to_drone2(configure_drone2);

                                    socketManager.send_to_drone1(SocketManager.COMMAND_START);
                                    socketManager.send_to_drone2(SocketManager.COMMAND_START);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // sendToDrone2(message);
                                startActivity(i);
                            }

                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Toast.makeText(getApplicationContext(),"Data Send",Toast.LENGTH_LONG).show();

                                dialog.cancel();
                            }
                        });
                AlertDialog alert = a_builder.create();
                alert.setTitle("Search Coverage Confirmation");
                alert.show();
            }
        });

        //displaying the width in text view
        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv.setText(et.getText() + "m");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            //variable convertion to int
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    areaWidth = et.getText().toString();
                    //Toast toast = Toast.makeText(getApplicationContext(),"Width:"+areaWidth,Toast.LENGTH_SHORT);
                    //toast.show();
                    areaWidth1 = Integer.parseInt(areaWidth);
                    SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);
                    seekbar.setMax((int) areaWidth1);
                } catch (Exception ex) {
                    //manage exception? or do nothing?

                }

            }

        });

        //seekbar functions
        seekBarValue = (TextView) findViewById(R.id.droneEstimationPosition);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //progress = progress / 10;
                //progress = progress * 10;
                seekBarValue.setText(String.valueOf(progress) + "m");
                //set seekbar value to int
                seekBarValueInt = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


}