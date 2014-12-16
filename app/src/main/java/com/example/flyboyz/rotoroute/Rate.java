package com.example.flyboyz.rotoroute;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.graphics.drawable.LayerDrawable;
import android.graphics.Canvas;
public class Rate extends Activity {


    private RatingBar ratingBar;
    private View view;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        EditText editText = (EditText) findViewById(R.id.editText);
        setContentView(R.layout.activity_rate);

        //LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        //stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        addTextListener();
        addRatingListener();
    }

    public void addRatingListener() {
       // ratingText = (TextView)findViewById(R.id.editText);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                Button submit = (Button) findViewById(R.id.button);
                submit.setVisibility(View.VISIBLE);
            }
        });
    }

   public void addTextListener(){
       editText = (EditText) findViewById(R.id.editText);

       editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView editText, int actionId, KeyEvent event) {
               // if (event = KeyEvent.)
              // editText.setText("");
               return false;
           }
       });


       }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rate, menu);
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


    public void submitClicked(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Rate.this);
        alertDialog.setTitle("Thanks!");
        alertDialog.setMessage("Thank you for navigating with RotoRoute!");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Rate.this, MainActivity.class);
                startActivity(i);
                finish();
                //return;
            } });
        AlertDialog alert = alertDialog.create();
        alert.show();

        //Intent i = new Intent(this, MainActivity.class);
        //startActivity(i);
    }
}
