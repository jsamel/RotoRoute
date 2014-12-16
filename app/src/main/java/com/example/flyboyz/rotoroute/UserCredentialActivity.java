package com.example.flyboyz.rotoroute;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UserCredentialActivity extends Activity implements OnItemSelectedListener {


    private String type = "";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_credentials);
		
		Intent intent = getIntent();
		String userType = intent.getStringExtra("USER_TYPE");
        type = userType;
		TextView helpText = (TextView) findViewById(R.id.help_credentials);
        Spinner stateSpinner = (Spinner) findViewById(R.id.spinner_state);
		if(userType.equals("student")){
			//helpText.setText(R.string.help_credentials_student);
            TextView uidText = (TextView) findViewById(R.id.editText1);
            uidText.setInputType(InputType.TYPE_CLASS_NUMBER);
            uidText.setHint("UID");
            stateSpinner.setVisibility(View.INVISIBLE);
		}
		else if (userType.equals("guest")){
			helpText.setText(R.string.help_credentials_guest);
            TextView text = (TextView) findViewById(R.id.editText1);
            text.setHint("Driver's License Number");

			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.state_abbrv, android.R.layout.simple_spinner_dropdown_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			stateSpinner.setAdapter(adapter);
			
		}

        Button nextButton = (Button) findViewById(R.id.button_submit_credentials);

        nextButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                TextView text = (TextView) findViewById(R.id.editText1);
                String credential = text.getText().toString();
                if(( credential != null) && !credential.equals("")) {
                    Intent i = new Intent(UserCredentialActivity.this, SelectDestActivity.class);
                    startActivity(i);
                }
                else{
                    String message;
                    if(type.equals("student")){
                        message = "UID";
                    }
                    else
                    message = "Driver\'s License Number";
                    Toast.makeText(getApplicationContext(), "Enter a "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });


        SpannableString ss = new SpannableString("I agree to the Terms and Conditions");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                CheckBox c = (CheckBox) findViewById(R.id.termsCheckbox);

                if (c.isChecked()) {
                    c.setChecked(false);
                } else {
                    c.setChecked(true);
                }
                startActivity(new Intent(UserCredentialActivity.this, TermsActivity.class));
            }
        };

        ss.setSpan(clickableSpan, 15, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        CheckBox checkBox = (CheckBox) findViewById(R.id.termsCheckbox);
        checkBox.setText(ss);
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());
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

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

    public void boxClicked(View v) {
        CheckBox c = (CheckBox) findViewById(R.id.termsCheckbox);
        Button b = (Button) findViewById(R.id.button_submit_credentials);

        if (c.isChecked()) {
            b.setVisibility(View.VISIBLE);
        } else {
            b.setVisibility(View.INVISIBLE);
        }
    }
}
