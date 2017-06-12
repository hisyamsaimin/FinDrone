package com.example.hisyamsaimin.findrone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//import the thread listen function
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MapView extends FragmentActivity implements OnMapReadyCallback {
    double areaWidth, areaLength, droneStartPosition;
    private GoogleMap mMap;
    Button stopDrone1, stopDrone2;
    String stopDrone1command, stopDrone2command = "stop";

    Marker drone1, drone2, phone;
    //private static Socket drone1,drone2;
    //private static String ip="10.218.204.41";
    //double Drone1Latitute, Drone2Latitute, Drone1Longitute, Drone2Longitute;

    // public MapView(DialogInterface.OnClickListener onClickListener) {

    // }
    SocketManager socketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);

        try {
            socketManager = SocketManager.getSocketManager();
            socketManager.set_mapView(this);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: notify there are errors
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        stopDrone1 = (Button) findViewById(R.id.stopDrone1);
        stopDrone2 = (Button) findViewById(R.id.stopDrone2);

        //stopDrone 1 button onlclick function
        stopDrone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder a_builder = new AlertDialog.Builder(MapView.this);
                a_builder.setMessage("Are you sure you want to stop drone 1? Clicking YES will instruct drone to return to base.")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //send stop command to drone1outputstream
                                try {
                                    socketManager.send_to_drone1(SocketManager.COMMAND_STOP);
                                    Toast toast = Toast.makeText(getApplicationContext(), "Drone 1 Stopped", Toast.LENGTH_SHORT);
                                    toast.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Intent i = new Intent(MapView.this, MapView.class);
                                Toast toast = Toast.makeText(getApplicationContext(), "stop:" + stopDrone1command, Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = a_builder.create();
                alert.setTitle("Confirm Stop Drone");
                alert.show();

            }
        });

        //stopDrone 2 button onlclick function
        stopDrone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder a_builder = new AlertDialog.Builder(MapView.this);
                a_builder.setMessage("Are you sure you want to stop drone 2? Clicking YES will instruct drone to return to base.")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //send stop command to drone1outputstream
                                try {
                                    socketManager.send_to_drone2(SocketManager.COMMAND_STOP);
                                    Toast toast = Toast.makeText(getApplicationContext(), "Drone 2 Stopped", Toast.LENGTH_SHORT);
                                    toast.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Intent i = new Intent(MapView.this, MapView.class);
                            }

                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = a_builder.create();
                alert.setTitle("Confirm Stop Drone");
                alert.show();
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //drone 1 pointer
        final LatLng drone1_pos = new LatLng(46.021385, 11.124158);
        //final LatLng drone1= new LatLng(latituteDrone1,longitudeDrone1);
        drone1 = mMap.addMarker(new MarkerOptions()
                .position(drone1_pos)
                .title(SocketManager.DRONE1_ID)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(drone1_pos, 13.2f));

        //drone 2 pointer
        final LatLng drone2_pos = new LatLng(49.021385, 11.124158);
        //uncomment this to get the drone2 altitude
        // final LatLng drone2= new LatLng(latituteDrone2,longitudeDrone2);
        drone2 = mMap.addMarker(new MarkerOptions()
                .position(drone2_pos)
                .title(SocketManager.DRONE2_ID)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(drone2_pos, 13.2f));

        // Toast toast = Toast.makeText(getApplicationContext(), "areaWidth:" + areaWidth, Toast.LENGTH_SHORT);
        //toast.show();

    }

    public void notifyPhoneFound(String droneID) {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        a_builder.setMessage(" has found the buried person.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                    }

                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Person Found!");
        alert.show();
        Toast toast = Toast.makeText(getApplicationContext(), "Person Found!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void notifyPhoneConnected(String droneID) {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        a_builder.setMessage("The buried phone is connected to " + droneID)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                    }

                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Phone Connected!");
        alert.show();

        Toast toast = Toast.makeText(getApplicationContext(), "Phone connected to "+droneID, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void updateDronePosition(String droneID, double latitude, double longitude) {
        final double lat = latitude;
        final double lon = longitude;
        final String id = droneID;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                LatLng drone_pos = new LatLng(lat, lon);
                if (id.equals(SocketManager.DRONE1_ID)) {
                    drone1.remove();
                    drone1 = mMap.addMarker(new MarkerOptions()
                            .position(drone_pos)
                            .title(SocketManager.DRONE1_ID)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    //drone1.setPosition(drone_pos);
                }
                else if (id.equals(SocketManager.DRONE2_ID)) {
                    //drone2.setPosition(drone_pos);
                    drone2.remove();
                    drone2 = mMap.addMarker(new MarkerOptions()
                            .position(drone_pos)
                            .title(SocketManager.DRONE2_ID)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }
            }
        });
        t.start();
    }

    public void updatePhonePosition(double latitude, double longitude, double accuracy) {
        final double lat = latitude;
        final double lon = longitude;
        final double acc = accuracy;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                LatLng phone_pos = new LatLng(lat, lon);
                if (phone != null) {
                    phone.remove();
                }
                phone = mMap.addMarker(new MarkerOptions()
                        .position(phone_pos)
                        .title("Buried Phone")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        });
        t.start();

    }

}
