package com.example.flyboyz.rotoroute;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class EnrouteActivity extends Activity {

    private SeekBar seekBar;
    private GoogleMap map;
    private LocationManager locationManager;
    private Marker userMarker;
    private int userIcon, destIcon;
    private String url;
    LatLng lastLatLng;
    private DirectionsResponse directionsResponse;
    private DistanceResponse distanceResponse;
    private Timer _timerCount = new Timer();
    private Handler handler = new Handler();
    private int DistanceCounter = 0;
    private double totalDistance = 0, distanceLeft = 0;
    private double latitude = 0, longitude = 0;
    private boolean isRunning = true;


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroute);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {


            }


            public void onStartTrackingTouch(SeekBar seekBar) {

            }


            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if(!isNetworkAvailable()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Location is not available");
            alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                } });
            AlertDialog alert = alertDialog.create();
            alert.show();
        } else {
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            userIcon = R.drawable.current_position;
            destIcon = R.drawable.red_pin2;

            if (map == null) {
                map = ((MapFragment)getFragmentManager().findFragmentById(R.id.enrouteMap)).getMap();
                if (map != null) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    updateLocation();
                    setDestination();
                }
            }

            _timerCount.schedule(new TimerTask() {

                @Override
                public void run() {

                    // Use the handler to marshal/invoke the Runnable code on the UI thread
                    //handler.post(new Runnable(){
                    //    @Override
                    //    public void run(){
                            updateDistanceLeft();
                    //   }
                    //});
                }
            }, 0, 8000);

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enroute, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateLocation(){
        Location lastLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double latitude = lastLoc.getLatitude();
        double longitude = lastLoc.getLongitude();
        lastLatLng = new LatLng(latitude, longitude);

        if(userMarker!=null) {
            userMarker.remove();
        }

        userMarker = map.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.fromResource(userIcon))
                .snippet("Your last recorded location"));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15));
        //map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);
    }


    public void setDestination() {
        /*url = "https://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=walking&";
        Location lastLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double latitude = lastLoc.getLatitude();
        double longitude = lastLoc.getLongitude();
        url += "origin=" + latitude + "," + longitude + "&";

        String dest = MapActivity.staticCode;
        url += "destination=" + dest;

        try {
            URL u = new URL(url);
            HttpGetter get = new HttpGetter();
            get.execute(u);

        } catch (Exception e) {
            e.printStackTrace();
        }*/

        directionsResponse = MapActivity.directionsResponse;
        List<LatLng> pts = decodePoly(directionsResponse.routes[0].overview_polyline.points);
        LatLng destPoint = pts.get(pts.size() - 1);
        String dest = MapActivity.staticDestination;

        map.addMarker(new MarkerOptions()
                .position(destPoint)
                .title(dest)
                .icon(BitmapDescriptorFactory.fromResource(destIcon))
                .snippet("Your destination"));

        PolylineOptions rectOptions = new PolylineOptions();
        for (LatLng pt : pts) {
            rectOptions.add(pt);
        }

        // Get back the mutable Polyline
        Polyline polyline = map.addPolyline(rectOptions);
        polyline.setWidth(5);
        polyline.setColor(Color.RED);

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(lastLatLng);
        boundsBuilder.include(destPoint);
        // pan to see all markers on map:
        LatLngBounds bounds = boundsBuilder.build();
        //map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));

    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }
        return poly;
    }


    public void updateDistanceLeft() {
        url = "https://maps.googleapis.com/maps/api/distancematrix/json?mode=walking&";
        Location lastLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        latitude = lastLoc.getLatitude();
        longitude = lastLoc.getLongitude();
        url += "origins=" + latitude + "," + longitude + "&";

        String dest = MapActivity.staticCode;
        url += "destinations=" + dest;

        try {
            URL u = new URL(url);
            HttpDistGetter get = new HttpDistGetter();
            get.execute(u);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class HttpDistGetter extends AsyncTask<URL, Void, Void> {
        private HttpDistGetter() {

        }

        @Override
        protected Void doInBackground(URL... params) {
            // Create an HTTP client
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet getRequest;
            HttpResponse response;

            getRequest = new HttpGet(url);
            // Execute the request and get an input stream of the response

            try {
                response = client.execute(getRequest);
                InputStream stream = response.getEntity().getContent();

                // Use GSON to to convert the stream into Java objects
                Reader reader = new InputStreamReader(stream);

                //Log.d("Here", "About to parse JSON");
                /**/
                Gson gson = new Gson();
                distanceResponse = gson.fromJson(reader, DistanceResponse.class);
                //Log.d("parse JSON ", distanceResponse == null ? "null response" : "directions response found");


            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //List<LatLng> pts = decodePoly(directionsResponse.routes[0].overview_polyline.points);
            LatLng currPoint = new LatLng(latitude, longitude);//pts.get(0);

            if (distanceResponse != null && distanceResponse.rows[0].elements[0] != null) {
                String distanceText = distanceResponse.rows[0].elements[0].duration.text;
                TextView v = (TextView) findViewById(R.id.timeLeft);
                v.setText("Estimated time remaining: " + distanceText);

                String dest = MapActivity.staticDestination;

                map.addMarker(new MarkerOptions()
                        .position(currPoint)
                        .title(dest)
                        .icon(BitmapDescriptorFactory.fromResource(userIcon))
                        .snippet("Where you are now"));

                double distance = distanceResponse.rows[0].elements[0].distance.value;

                if (DistanceCounter == 0) {

                    //Add up distance for original path
                    totalDistance = distance;

                    seekBar.setMax(0);
                    seekBar.setMax((int)totalDistance);

                }
                TextView t = (TextView) findViewById(R.id.textView);
                t.setText("TotalDist: " + (int)totalDistance + "\nDistLeft: " + ((int)distance)
                        + "\nCOUNT: " + DistanceCounter);

                seekBar.setProgress((int)totalDistance - (int)distance);
                DistanceCounter++;

                if (distance <= 130) {
                    if (isRunning) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EnrouteActivity.this);
                        alertDialog.setTitle("Route Complete");
                        alertDialog.setMessage("You have reached your destination!");
                        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(EnrouteActivity.this, Rate.class);
                                startActivity(i);
                                finish();
                                isRunning = false;
                                //return;
                            }
                        });
                        AlertDialog alert = alertDialog.create();
                        alert.show();
                    }

                }
            }


            /*PolylineOptions rectOptions = new PolylineOptions();
            for (LatLng pt : pts) {
                rectOptions.add(pt);
            }

            // Get back the mutable Polyline
            Polyline polyline = map.addPolyline(rectOptions);
            polyline.setWidth(5);
            polyline.setColor(Color.RED);

            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(lastLatLng);
            boundsBuilder.include(destPoint);
            // pan to see all markers on map:
            LatLngBounds bounds = boundsBuilder.build();

            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            LatLngBounds l  = new LatLngBounds(lastLatLng, currPoint);
            boundsBuilder.include(lastLatLng);
            boundsBuilder.include(currPoint);
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(l, 10));*/

        }

    }


    public void cancelClicked(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EnrouteActivity.this);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure you want to cancel your trip?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(EnrouteActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            } });
        AlertDialog alert = alertDialog.create();
        alert.show();

        //Intent i = new Intent(this, Rate.class);
        //startActivity(i);
    }

    public void playClicked(View v) {

    }
}
