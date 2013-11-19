package be.aurorethiaumont.artpeople;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowContact extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_contact);
		// Show the Up button in the action bar.
		setupActionBar();

		initDisplay();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void initDisplay(){
		Intent intent = getIntent();
		String name = intent.getStringExtra(MainActivity.CONTACT_NAME);
		String first_name = intent.getStringExtra(MainActivity.CONTACT_FIRST_NAME);
		ArrayList<String> phone = intent.getStringArrayListExtra(MainActivity.CONTACT_PHONE);
		ArrayList<String> email = intent.getStringArrayListExtra(MainActivity.CONTACT_EMAIL);
		
		View dividerView;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 1);
		lp.setMargins(0, 2, 0, 2);
		
		TextView tvName = (TextView) findViewById(R.id.contactName);
		tvName.setText(first_name + " " + name);
		tvName.setTextSize(30);

		TextView tvPhone;
		LinearLayout lvPhone = (LinearLayout) findViewById(R.id.contactPhone);
		for(String v : phone){
			dividerView = new View(getBaseContext());
			dividerView.setLayoutParams(lp);
			dividerView.setBackgroundColor(Color.GRAY);
			
			tvPhone = new TextView(this);
			tvPhone.setText(formatPhoneNumber(v));
			tvPhone.setTextSize(20);
			Linkify.addLinks(tvPhone, Linkify.PHONE_NUMBERS);
			lvPhone.addView(tvPhone);
			lvPhone.addView(dividerView);
		}

		TextView tvEmail;
		LinearLayout lvEmail = (LinearLayout) findViewById(R.id.contactMail);
		for(String v : email){
			dividerView = new View(getBaseContext());
			dividerView.setLayoutParams(lp);
			dividerView.setBackgroundColor(Color.GRAY);
			tvEmail = new TextView(this);
			tvEmail.setText(v);
			tvEmail.setTextSize(20);
			Linkify.addLinks(tvEmail, Linkify.EMAIL_ADDRESSES);
			lvEmail.addView(tvEmail);
			lvEmail.addView(dividerView);
		}
	}

	String formatPhoneNumber(String number){
		number = number.replaceAll("[./()]", "");
		number = number.replace('.', '\0');

		return number;
	}
}
