package be.aurorethiaumont.artpeople;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import be.aurorethiaumont.artpeople.DownloadXMLTask.DataFetchedListener;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class MainActivity extends FragmentActivity implements LoginDialog.LoginDialogListener,DataFetchedListener{

	ArrayAdapter<String> ad;
	public final static String CONTACT_NAME = "be.aurorethiaumont.artpeople.CONTACTNAME";
	public final static String CONTACT_FIRST_NAME = "be.aurorethiaumont.artpeople.CONTACTFIRSTNAME";
	public final static String CONTACT_PHONE = "be.aurorethiaumont.artpeople.CONTACTPHONE";
	public final static String CONTACT_EMAIL = "be.aurorethiaumont.artpeople.CONTACTEMAIL";

	Map<String, Contact> contactList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((PullToRefreshListView) findViewById(R.id.listContacts)).setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadFromNetwork();
				//((PullToRefreshListView) findViewById(R.id.listContacts)).onRefreshComplete();
			}
		});

		init();
		loadFromFile();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void init(){		
		ListView l = (ListView) findViewById(R.id.listContacts);

		contactList = new HashMap<String, Contact>();
		ad = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

		l.setAdapter(ad);
		l.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id){
				showContact((ListView) adapter, view, pos, id);
			}
		});
	}

	public void loadFromFile(){
		DownloadXMLTask fetchDataTask = new DownloadXMLTask();
		fetchDataTask.parent = this;

		fetchDataTask.execute("artContacts.xml");
	}
	
	public void loadFromNetwork(){
		if(testNetwork()){
			LoginDialog d = new LoginDialog();
			d.show(getSupportFragmentManager(), "loginBox");
		}
		else{
			((PullToRefreshListView) findViewById(R.id.listContacts)).onRefreshComplete();
		}
	}

	public boolean testNetwork(){
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public void addContact(Contact c){
		String display = c.first_Name + " " + c.name; 
		contactList.put(display, c);
		ad.add(display);
		ad.sort(String.CASE_INSENSITIVE_ORDER);
		ad.notifyDataSetChanged();
	}

	public void showContact(ListView l, View v, int position, long id){
		TextView tv  = (TextView) v;
		Contact c = contactList.get(tv.getText().toString());

		Intent intent = new Intent(this, ShowContact.class);
		intent.putExtra(CONTACT_NAME, c.name);
		intent.putExtra(CONTACT_FIRST_NAME, c.first_Name);
		intent.putExtra(CONTACT_PHONE, c.phone);
		intent.putExtra(CONTACT_EMAIL, c.email);

		startActivity(intent);
	}

	
	@Override
	public void onDialogPositiveClick(LoginDialog dialog, String login, String password) {
		DownloadXMLTask fetchDataTask = new DownloadXMLTask();
		fetchDataTask.parent = this;

		fetchDataTask.execute("http", login, password);
	}


	@Override
	public void onDialogNegativeClick(LoginDialog dialog) {
		((PullToRefreshListView) findViewById(R.id.listContacts)).onRefreshComplete();		
	}


	@Override
	public void newDataFetched(List<Contact> l) {
		if(l != null){
			ad.clear();
			contactList.clear();
			for(Contact el : l){
				addContact(el);
			}
		}
		((PullToRefreshListView) findViewById(R.id.listContacts)).onRefreshComplete();		
	}
	
	public void errorWhileFetchingData(int error){
		ErrorDialog d = new ErrorDialog();
		((PullToRefreshListView) findViewById(R.id.listContacts)).onRefreshComplete();
		
		Log.d("DEBUG", "" + error);
		d.message = getString(error);
		d.show(getSupportFragmentManager(), "ErrorDialog");
	}
}
