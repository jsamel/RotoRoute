package com.example.flyboyz.rotoroute;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button studentButton = (Button) findViewById(R.id.button_student);
        final Button guestButton = (Button) findViewById(R.id.button_guest);

        studentButton.getPaint().setAntiAlias(true);
        guestButton.getPaint().setAntiAlias(true);

        studentButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                createCredentialView("student");
            }
        });

        guestButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                createCredentialView("guest");
            }
        });
    }


    private void createCredentialView(String userType){
        Intent intent = new Intent(MainActivity.this, UserCredentialActivity.class);
        intent.putExtra("USER_TYPE", userType);
        startActivity(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void nextClicked(View view) {
        Intent i = new Intent(this, SelectDestActivity.class);
        startActivity(i);
    }


}
