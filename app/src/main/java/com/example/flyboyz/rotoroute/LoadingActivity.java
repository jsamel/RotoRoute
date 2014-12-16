package com.example.flyboyz.rotoroute;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.io.InputStream;


public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ImageView image = (ImageView) findViewById(R.id.imageView);
        Ion.with(image).load("http://nakeddoorman.com/wp-content/uploads/2014/12/rotorouteicon.gif");

        /*InputStream stream = null;
        try {
            stream = getAssets().open("piggy.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }
        GifWebView view = new GifWebView(this, "file:///android_asset    /piggy.gif");

        setContentView(view);*/

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // TODO: Your application init goes here.
                //Intent i = new Intent(this, InvoiceASAPTabActivity.class);
                //Splash_Screen_Activity.this.startActivity(mInHome);
                //Splash_Screen_Activity.this.finish();
                TextView loadingView = (TextView)findViewById(R.id.loading_text);
                loadingView.setText("A quadcopter is en route to your location.");
            }
        }, 3000);

        handler.postDelayed(new Runnable() {
            public void run() {
                // TODO: Your application init goes here.
                Intent i = new Intent(LoadingActivity.this, EnrouteActivity.class);
                startActivity(i);
                finish();
                //Splash_Screen_Activity.this.finish();
            }
        }, 6000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loading, menu);
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
}
