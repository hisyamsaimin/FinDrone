package com.example.hisyamsaimin.findrone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketManager_1 {

    private static ServerSocket ss;
    private static Socket drone1, drone2;
    private static BufferedReader br,br2;
    private static InputStreamReader isr, isr2;
    private static String message, message2="";

/**
 * Launch the application.
 */
public static void main(String[] args) throws IOException {

    Thread listen = new Thread(new Runnable() {

        @Override
        public void run() {
            final Thread listen_drone1, listen_drone2;
            try {


                ss = new ServerSocket (9119);
               // drone1= new Socket(ip,9119);
               System.out.print("Sever running at port 9119");
                drone1= ss.accept();
                drone2= ss.accept();

                listen_drone1 = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            isr = new InputStreamReader(drone1.getInputStream());
                            br = new BufferedReader(isr);
                            message = br.readLine();
                            System.out.println(message);
                            isr.close();
                            br.close();
                            ss.close();
                            //drone1.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                listen_drone1.start();

                listen_drone2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                                isr2 = new InputStreamReader(drone2.getInputStream());
                                br2 = new BufferedReader(isr2);
                                message2 = br2.readLine();
                                System.out.println(message2);
                                isr2.close();
                                br2.close();
                                ss.close();
                                drone2.close();

                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                listen_drone2.start();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    });
    listen.start();


}}