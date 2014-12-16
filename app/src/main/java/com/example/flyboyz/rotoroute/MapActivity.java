package com.example.flyboyz.rotoroute;

import android.animation.AnimatorSet;
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
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapActivity extends Activity {
    public static String staticDestination = "";
    public static String staticCode = "";

    //private static final String[] BUILDINGS = SelectDestActivity.BUILDINGS;
    public static String[] places = SelectDestActivity.places;
    public static HashMap<String, String> BUILDING_CODES = SelectDestActivity.BUILDING_CODES;
    private GoogleMap map;
    private LocationManager locationManager;
    private Marker userMarker;
    private int userIcon, destIcon;
    private String url;
    LatLng lastLatLng;
    public static DirectionsResponse directionsResponse;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        String destination = getIntent().getStringExtra("description");
        staticDestination = destination;
        staticCode = getIntent().getStringExtra("destination");
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.mapDestination);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, places);
        textView.setAdapter(adapter);
        textView.setText(destination);

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean foundMatch = false;
                for (String str: places) {
                    if (str.equals(s.toString())) {
                        foundMatch = true;
                    }
                }

                Button button = (Button) findViewById(R.id.destSearch);
                if (foundMatch) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
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
                map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
                if (map != null) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    updateLocation();
                    setDestination();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
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

        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 13));
        //map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);
    }


    public void setDestination() {
        url = "https://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=walking&";
        Location lastLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double latitude = lastLoc.getLatitude();
        double longitude = lastLoc.getLongitude();
        url += "origin=" + latitude + "," + longitude + "&";

        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.mapDestination);
        String dest = textView.getText().toString();
        String code = BUILDING_CODES.get(dest);

        url += "destination=" + code;

        try {
            URL u = new URL(url);
            HttpGetter get = new HttpGetter();
            get.execute(u);

            /*HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost, localContext);

            Log.d("RESPONSE: " , response.toString());
            */
            /*InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            return doc;*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void showMap(View view) {
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.mapDestination);
        String dest = textView.getText().toString();
        String code = BUILDING_CODES.get(dest);

        setDestination();
    }


    public void requestClicked(View view) {
        Intent i = new Intent(this, LoadingActivity.class);
        startActivity(i);
    }


    private class HttpGetter extends AsyncTask<URL, Void, Void> {
        private HttpGetter() {

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
                directionsResponse = gson.fromJson(reader, DirectionsResponse.class);
                Log.d("parse JSON ", directionsResponse == null ? "null response" : "directions response found");


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
            List<LatLng> pts = decodePoly(directionsResponse.routes[0].overview_polyline.points);
            LatLng destPoint = pts.get(pts.size() - 1);

            AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.mapDestination);
            String dest = textView.getText().toString();

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
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));


        }

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

}
