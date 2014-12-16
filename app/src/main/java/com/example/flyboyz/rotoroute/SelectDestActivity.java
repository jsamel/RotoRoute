package com.example.flyboyz.rotoroute;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import org.htmlparser.Parser;

import java.util.HashMap;


public class SelectDestActivity extends Activity {
    public static HashMap<String, String> BUILDING_CODES = new HashMap<String, String>();

    public static final String[] BUILDINGS = new String[] {
            "McKeldin_Mall", "Computer_Science_Instructional_Center", "Hornbake_Library", "Stamp_Student_Union", "Glenn_L._Martin_Wind_Tunnel",
            "Technology_Advancement_Program"
    };

    public static String[] places;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dest);

        /*Parser parser = new Parser ("http://www.umd.edu/CampusMaps/");
        /*NodeList list = parser.parse (null);*/

        BUILDING_CODES.put("McKeldin Mall", "McKeldin_Mall");
        BUILDING_CODES.put("CSIC: Computer Science Instructional Center", "Computer_Science_Instructional_Center");
        BUILDING_CODES.put("HBK: Hornbake Library", "Hornbake_Library");
        BUILDING_CODES.put("SSU: Stamp Student Union", "Stamp_Student_Union");
        BUILDING_CODES.put("Wind Tunnel Building", "Glenn_L._Martin_Wind_Tunnel");
        BUILDING_CODES.put("TAP: Technology Advancement Program Building", "Technology_Advancement_Program");
        BUILDING_CODES.put("Commons 4", "4310_Knox_Road");

        Object[] placesObj = BUILDING_CODES.keySet().toArray();
        places = new String[placesObj.length];
        for(int i = 0; i < placesObj.length; i++) {
            places[i] = (String)placesObj[i];
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, places);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.destination);
        textView.setAdapter(adapter);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("TEXT:", s.toString());
                boolean foundMatch = false;
                for (String str: places) {
                    if (str.equals(s.toString())) {
                        foundMatch = true;
                    }
                }

                Button button = (Button) findViewById(R.id.destNext);
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_destination, menu);
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


    public void showMap(View view) {
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.destination);
        String dest = textView.getText().toString();
        String code = BUILDING_CODES.get(dest);

        Intent i = new Intent(this, MapActivity.class);
        i.putExtra("destination", code);
        i.putExtra("description", dest);
        startActivity(i);
    }

}
