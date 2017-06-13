package com.example.hisyamsaimin.findrone;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Hisyam Saimin on 06/12/2017.
 */

public class SocketManager {
    public static String DRONE1_ID = "Drone1";
    public static String DRONE2_ID = "Drone2";
    public static String PHONE_ID = "Phone";
    public static String COMMAND_CONFIGURE = "configure";
    public static String COMMAND_START = "start";
    public static String COMMAND_STOP = "stop";
    public static String COMMAND_PHONE_CONNECTED= "phone_connected";
    public static String COMMAND_PHONE_FOUND= "phone_found";
    public static String COMMAND_PHONE_POSITION= "phone_position";
    public static String COMMAND_DRONE_POSITION= "drone_position";
    public static String DATA_WIDTH = "width";
    public static String DATA_LENGTH = "length";
    public static String DATA_POSITION = "position";
    public static String DATA_LONGITUDE = "longitude";
    public static String DATA_LATITUDE = "latitude";
    public static String DATA_ACCURACY = "accuracy";

    String DRONE1_ADDR = "192.168.0.2";
    String DRONE2_ADDR = "192.168.0.3";
    int PORT = 9119;
    int MAX_NUMBER_DRONES = 2;
    int counter = 0;
    ServerSocket ss;
    Socket drone_1, drone_2, tmp;
    PrintWriter out_drone1, out_drone2;
    BufferedReader in_drone1, in_drone2;
    boolean finished = false;
    TextView tv_numberofdrone;
    MapView mapView;

    private static SocketManager socketManager;

    public static SocketManager getSocketManager() throws Exception{
        if(socketManager == null)
        {
            socketManager = new SocketManager();
        }
        return socketManager;
    }

    public void set_textView_numberDrones(TextView tv){
        tv_numberofdrone = tv;
    }

    public void set_mapView(MapView mapView)
    {
        this.mapView = mapView;
    }

    private SocketManager() throws Exception{
        ss = new ServerSocket(PORT);

        Thread listen = new Thread(new Runnable() {
            @Override
            public void run() {
                while (counter<2){
                    try {
                        tmp = ss.accept();
                        InputStreamReader isr = new InputStreamReader(tmp.getInputStream());
                        BufferedReader br = new BufferedReader(isr);
                        String id = br.readLine();

                        checkSocket(tmp, id.toLowerCase());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        listen.start();
    }

    private void checkSocket(Socket tmp, String id){
        String tmp_addr = tmp.getInetAddress().getHostAddress();
        if(id.equals(DRONE1_ID.toLowerCase())){
            drone_1 = tmp;
            try {
                out_drone1 = new PrintWriter(drone_1.getOutputStream(), true);
                InputStreamReader isr = new InputStreamReader(drone_1.getInputStream());
                in_drone1 = new BufferedReader(isr);

                Thread listen_drone1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        listen_drone1();
                    }
                });
                listen_drone1.start();

                counter++;
            }catch (Exception e){

            }
        }
        else if(id.equals(DRONE2_ID.toLowerCase())){
            drone_2=tmp;
            try {
                out_drone2 = new PrintWriter(drone_2.getOutputStream(), true);
                InputStreamReader isr = new InputStreamReader(drone_2.getInputStream());
                in_drone2 = new BufferedReader(isr);

                Thread listen_drone2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        listen_drone2();
                    }
                });
                listen_drone2.start();

                counter++;
            }catch (Exception e){
            }
        }
        if(tv_numberofdrone!=null){
            tv_numberofdrone.setText(String.valueOf(counter));
        }
    }

    public void send_to_drone1(String message) throws IOException{
        final String msg = message;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if(drone_1 == null)
                    return;
                out_drone1.println(msg);
            }
        });
        t.start();
    }

    public void send_to_drone2(String message) throws IOException{
        final String msg = message;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if(drone_2 == null)
                    return;
                out_drone2.println(msg);
            }
        });
        t.start();
    }

    public void listen_drone1(){
        while(!finished){
            try {
                String message = in_drone1.readLine();
                message_handler(DRONE1_ID, message);
            }catch(Exception ex){

            }
        }
    }

    public void listen_drone2(){
        while(!finished){
            try {
                String message = in_drone2.readLine();
                message_handler(DRONE2_ID, message);
            }catch(Exception ex){
                String s = ex.toString();
                String boh;
            }
        }
    }

    private void message_handler(String drone_ID, String message){
        final String msg = message;
        final String droneID = drone_ID;
        mapView.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] messageArray = msg.split(":");
                String command = messageArray[0];

                if(command.equals(SocketManager.COMMAND_PHONE_CONNECTED)){
                    if(mapView != null)
                        mapView.notifyPhoneConnected(droneID);
                }
                if(command.equals(SocketManager.COMMAND_PHONE_FOUND)){
                    if(mapView != null)
                        mapView.notifyPhoneFound(droneID);
                }
                if(command.equals(SocketManager.COMMAND_DRONE_POSITION)){
                    double latitude=0.0, longitude=0.0;

                    String[] dataArray = messageArray[1].split(";");

                    for(String data : dataArray){
                        String[] valueArray = data.split("=");
                        if(valueArray[0].equals(SocketManager.DATA_LATITUDE)){
                            latitude = Float.valueOf(valueArray[1]);
                        }
                        if(valueArray[0].equals(SocketManager.DATA_LONGITUDE)){
                            longitude = Float.valueOf(valueArray[1]);
                        }
                    }
                    if(mapView != null)
                        mapView.updateDronePosition(droneID, latitude, longitude);
                }
                if(command.equals(SocketManager.COMMAND_PHONE_POSITION)){
                    double latitude=0.0, longitude=0.0, accuracy=0.0;

                    String[] dataArray = messageArray[1].split(";");

                    for(String data : dataArray){
                        String[] valueArray = data.split("=");
                        if(valueArray[0].equals(SocketManager.DATA_LATITUDE)){
                            latitude = Float.valueOf(valueArray[1]);
                        }
                        if(valueArray[0].equals(SocketManager.DATA_LONGITUDE)){
                            longitude = Float.valueOf(valueArray[1]);
                        }
                        if(valueArray[0].equals(SocketManager.DATA_ACCURACY)){
                            accuracy = Float.valueOf(valueArray[1]);
                        }
                    }

                    if(mapView != null)
                        mapView.updatePhonePosition(latitude, longitude, accuracy);
                }
            }
        });

    }
}
