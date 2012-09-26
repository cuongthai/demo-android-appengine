package com.example.barcampphnompenh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.example.barcampphnompenh.dtos.EventDTO;
import com.example.barcampphnompenh.gps.LocationHelper;
import com.example.barcampphnompenh.gps.LocationResult;
import com.google.gson.Gson;

public class EventActivity extends ListActivity {
	AsyncTask<Void, Void, Void> fetcher;
	private Location location;
	LocationResult locationResult = new LocationResult() {
		String jsonResult;

		@Override
		public void gotLocation(final Location location) {
			setTitle("lat:" + location.getLatitude() + " lng:"
					+ location.getLongitude());
			EventActivity.this.location = location;

			fetcher = new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					jsonResult = ServerUtilities.get(CommonUtilities.SERVER_URL
							+ "/event/discover/?lat=" + location.getLatitude()
							+ "&log=" + location.getLongitude());

					return null;
				}

				protected void onPostExecute(Void result) {
					List<String> list=new ArrayList<String>();
					Gson gson = new Gson();
					Log.i(CommonUtilities.TAG, jsonResult);
					final EventDTO[] events = gson.fromJson(jsonResult,
							EventDTO[].class);
					for(EventDTO event:events){
						Log.d(CommonUtilities.TAG,event.event_name);
						list.add(event.event_name);
					}
					
					setListAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list.toArray(new String[list.size()])));
					getListView().setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								final int position, long arg3) {
							AsyncTask<Void, Void, Void> poster=new AsyncTask<Void, Void, Void>(){

								@Override
								protected Void doInBackground(Void... p) {
									Log.i(CommonUtilities.TAG,"Joining to event pos "+position+" "+events[position].event_name);
									Intent intent=new Intent(EventActivity.this,UsersInEventActivity.class);
									intent.putExtra("event_name", events[position].event_name);
									startActivity(intent);
									
									return null;
								}
								
							};
							poster.execute();
						}
						
					});
					
				};
			};
			fetcher.execute();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_activity);
		new LocationHelper().getLocation(getApplicationContext(),
				locationResult);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.i(CommonUtilities.TAG, "f+"+item.getAlphabeticShortcut());
		switch (item.getAlphabeticShortcut()) {
		
		case 'c':
			showCreateDialog();
			break;
		case 'r':
			refresh();
			break;
		default:
			break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}

	private void refresh() {
		new LocationHelper().getLocation(getApplicationContext(),
				locationResult);
	}

	private void showCreateDialog() {

		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		alert.setView(input);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			fetcher=new AsyncTask<Void, Void, Void>(){

				@Override
				protected Void doInBackground(Void... p) {
					String eventName = input.getText().toString().trim();
					if (location != null && eventName != null
							&& eventName.length() != 0) {
						Map<String, String> params = new HashMap<String, String>();
						params.put("event_name", eventName);
						params.put("lat", location.getLatitude() + "");
						params.put("log", location.getLongitude() + "");
						try {
							ServerUtilities.post(CommonUtilities.SERVER_URL
									+ "/event/create/", params);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					return null;
				}
				
			};
			fetcher.execute();
			}
		});
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
	}
}
