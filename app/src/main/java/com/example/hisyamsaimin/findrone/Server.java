package com.example.hisyamsaimin.findrone;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends AppCompatActivity {

    EditText e1;
    private static Socket s;
    //private static ServerSocket ss;
   private static PrintWriter printWriter;
    //private static BufferedReader br;
    static String message ="ff";
   //private static String ip="10.218.206.242";  //phone eduroam ip address
         private static String ip="10.218.204.41"; //pc ip address
    //private static String ip="193.205.210.44";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

                e1 =(EditText)findViewById(R.id.editText);
            }

    public void send_text(View v){
        message =e1.getText().toString();
        myTask mt = new myTask();
        mt.execute();

        Toast.makeText(getApplicationContext(),"Data Send",Toast.LENGTH_LONG).show();

    }
    static class myTask extends AsyncTask<Void, Void,Void>{

       @Override
        protected Void doInBackground(Void... params) {

           try {
                s= new Socket(ip,9119);
                printWriter= new PrintWriter(s.getOutputStream());
               printWriter.write(message);
                printWriter.flush();
                printWriter.close();
                s.close();


            } catch (IOException e) {
               e.printStackTrace();
            }
            return null;
        }
    }
}
