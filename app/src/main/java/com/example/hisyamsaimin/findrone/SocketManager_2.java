package com.example.hisyamsaimin.findrone;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import static com.example.hisyamsaimin.findrone.R.id.length;
import static java.lang.System.out;

/**
 * Created by Hisyam Saimin on 05/24/2017.
 */

public class SocketManager_2 {
    String droneID1;
    String droneID2;
    int port = 9119;
    ServerSocket ss;
    static double latituteDrone1;
    static double longitudeDrone1;
    static double latituteDrone2;
    static double longitudeDrone2;
    double burriedPhoneLatitute;
    double burriedPhoneLongitute;
    double accuracy;
    static Double[] burriedPhoneInfo;
    ////count is used to check number of socket client connected
    int count =0;
    static boolean found;
        static Socket drone1, drone2;
    Context context;

    //read the inetAddress and host address of drone1
    InetAddress inetDrone1 = drone1.getInetAddress();
    String remoteIpdrone1 = inetDrone1.getHostAddress();

    //read the inetAddress and host address of drone2
    InetAddress inetDrone2 = drone2.getInetAddress();
    String remoteIpdrone2 = inetDrone2.getHostAddress();

    public SocketManager_2(Context context) throws Exception
    {
        //All you need to do is Extend all the activities with BaseActivity. The app never crashes at any point
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error"+Thread.currentThread().getStackTrace()[2],paramThrowable.getLocalizedMessage());
            }
        });
        this.context = context;

        //set drone 1
        //start drone 1
        Thread listen = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ss = new ServerSocket(port);
                } catch (IOException e) {
                    e.printStackTrace();

                }
                Socket buffer;
                final Thread listen_drone1, listen_drone2;
                try {
                    buffer = ss.accept();
                    drone1 = buffer;
                } catch (Exception ex) {
                    displayWarning("Can't connect to drone 1.");
                    return;
                }

                listen_drone1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        listen_drone1Position(latituteDrone1, longitudeDrone1);
                        burriedPhoneLocation(burriedPhoneLatitute, burriedPhoneLongitute,accuracy);
                    }
                });
                listen_drone1.start();

                if ( remoteIpdrone1 == "192.168.1.1") {
                    count++;

                    try {
                        buffer = ss.accept();
                        drone1 = buffer;
                    } catch (Exception ex) {
                        displayWarning("Can't connect to drone 1.");
                        return;
                    }

                    //listen_drone1 = new Thread(new Runnable() {
                        //@Override
                       // public void run() {
                           // listen_drone1Position(latituteDrone1, longitudeDrone1);
                           // burriedPhoneLocation(burriedPhoneLatitute, burriedPhoneLongitute,accuracy);
                       // }
                    //});
                   // listen_drone1.start();

                }
                else if (remoteIpdrone2 == "192.168.2.1"){
                    count++;
                try{
                    buffer = ss.accept();
                    drone2 = buffer;
                }catch (Exception ex){
                    displayWarning("Can't connect to drone 2.");
                    return;
                }

                listen_drone2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        listen_drone2Position(latituteDrone2,longitudeDrone2);
                        burriedPhoneLocation(burriedPhoneLatitute, burriedPhoneLongitute,accuracy);
                    }
                });
                listen_drone2.start();

            }}

            private void displayWarning(String s) {
            }
        });
        listen.start();


    }

    public SocketManager_2(DialogInterface.OnClickListener onClickListener) {

    }

   // public void send_drone1( double HalfWidth, double length, double droneStartPosition1){
   public void send_drone1( String message){
        //input stream from app to drone
       if ( drone1 != null && out != null) {
           out.println(message);
       }
       //or need to try this
    /* try {
                OutputStream os = drone1.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject("configure:width=" +HalfWidth + ";length="+length + ";position=" +droneStartPosition1);
                //the command to start the drone
                oos.writeObject("start");

            } catch (IOException e) {
                displayWarning("error sending data  to drone 1.");
            }*/

    }
    //send stop command to drone 1
    public void send_stop_drone1( String stopDrone1command) throws IOException {
        OutputStream os = drone1.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(stopDrone1command);
    }
    //send stop command to drone 1
    public void send_stop_drone2( String stopDrone2command) throws IOException {
        OutputStream os = drone1.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(stopDrone2command);
    }

    public void send_drone2( double HalfWidth, double length, double droneStartPosition2){
        while (true){
        try {

            OutputStream os = drone2.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject("drone 2 width : "+HalfWidth);
            oos.writeObject("drone 2 length : "+length);
            oos.writeObject("drone 2 position  : "+droneStartPosition2);
            //the command to start the drone
            oos.writeObject("Start Drone 2");
        } catch (IOException e) {
            displayWarning("error sending data  to drone 2.");
        }

    }}


    public  double listen_drone1Position(double latituteDrone1, double longitudeDrone1){
        // output stream from drone to apps
        while (true){
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(drone1.getInputStream()));
                // String input_str = input.readLine();
                //read latitute and longitute from drone 1
                latituteDrone1  = Double.parseDouble(input.readLine());
                 longitudeDrone1 = Double.parseDouble(input.readLine());


            }catch (Exception ex){
                displayWarning("error listening to drone 1.");
            }

        }
    }

    public static void displayWarning(String s) {
    }
    public void listen_drone2Position(double latituteDrone2, double longitudeDrone2){
        // output stream from drone to apps
        while (true){
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(drone2.getInputStream()));
                //String input_str = input.readLine();
                //read latitute and longitute from drone 1
                 latituteDrone2  = Double.parseDouble(input.readLine());
                 longitudeDrone2 = Double.parseDouble(input.readLine());


            }catch (Exception ex){
                displayWarning("error listening to drone 2.");
            }

        }
    }


    public static Double[] burriedPhoneLocation (double phoneLatitute, double phoneLongitude, double accuracy) {
        // output stream from drone to apps
        while (true) {
            try {
            BufferedReader input = new BufferedReader(new InputStreamReader(drone1.getInputStream()));
                // String input_str = input.readLine();
                //read latitute and longitute from drone 1
                if (input.readLine() == "phone_connected" || input.readLine() == "phone_found") {
                    //retreive the value and set variable
                    phoneLatitute = Double.parseDouble(input.readLine());
                    phoneLongitude = Double.parseDouble(input.readLine());
                    accuracy = Double.parseDouble(input.readLine());
                    double[] burriedPhoneInfo = {phoneLatitute,phoneLongitude,accuracy};

                    found = true;

                } else {
                    found = false;
                    break;
                }
            } catch (Exception ex) {
                displayWarning("unable to fetch data about burried phone.");
            }

        }
        //send data to MapView
        return (burriedPhoneInfo);
    }

}

