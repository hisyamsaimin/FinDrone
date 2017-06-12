package com.example.hisyamsaimin.findrone;

import android.support.v4.app.FragmentActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import static com.example.hisyamsaimin.findrone.SocketManager_2.drone1;
import static com.example.hisyamsaimin.findrone.SocketManager_2.drone2;


public class droneManager extends FragmentActivity {

    static SecondPage secondPage = new SecondPage();
    static double areaWidth= secondPage.areaWidth1;
    double areaLength= secondPage.areaLength1;
    double droneStartPosition= secondPage.seekBarValueInt;


    //dividing the area to search into 2
    //eg width :300 length :200 drone position :100
    //drone 1 w=150 l=200 position=100
    //drone 2 w=150 l =200 position =-50
    //drone_position - starting point of the second area
    double HalfWidth = areaWidth/2;
    double droneStartPosition2 = droneStartPosition - areaWidth;

    //passing the value width, length, dronePosition to mapView
     public static double returnWidth()
    {
         return areaWidth ;
    }
     public double returnLength()
      {
     return areaLength ;
      }
      public double returnDronePosition()
     {
       return droneStartPosition ;
     }

    public void send_drone1( double HalfWidth, double length, double droneStartPosition1){
        //input stream from app to drone

        try {
            // DataOutputStream outputStream = new DataOutputStream(drone1.getOutputStream());
            // outputStream.writeDouble(drone1StartArea);
            // outputStream.writeDouble(drone1EndArea);
            OutputStream os = drone1.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject("drone 1 width : "+HalfWidth);
            oos.writeObject("drone 1 length : "+length);
            oos.writeObject("drone 1 position  : "+droneStartPosition1);
            //the command to start the drone
            oos.writeObject("Start Drone 1");
        } catch (IOException e) {
            displayWarning("error sending data  to drone 1.");
        }

    }
    public void send_drone2( double HalfWidth, double length, double droneStartPosition2){
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

    }
    private void listen_drone1(){
        // output stream from drone to apps
        while (true){
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(drone1.getInputStream()));
                // String input_str = input.readLine();
                //read latitute and longitute from drone 1
                double latituteDrone1  = Double.parseDouble(input.readLine());
                double longitudeDrone1 = Double.parseDouble(input.readLine());

            }catch (Exception ex){
                displayWarning("error listening to drone 1.");
            }

        }
    }

    private void displayWarning(String s) {
    }
    private void listen_drone2(){
        // output stream from drone to apps
        while (true){
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(drone2.getInputStream()));
                //String input_str = input.readLine();
                //read latitute and longitute from drone 1
                double latituteDrone2  = Double.parseDouble(input.readLine());
                double longitudeDrone2 = Double.parseDouble(input.readLine());


            }catch (Exception ex){
                displayWarning("error listening to drone 2.");
            }

        }
    }
    }