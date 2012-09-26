package com.example.barcampphnompenh;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.EventLog.Event;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {

	AsyncTask<Void, Void, Void> mRegisterTask;
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					CommonUtilities.EXTRA_MESSAGE);
			// mDisplay.append(newMessage + "\n");
			Log.d(CommonUtilities.TAG, newMessage);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		setContentView(R.layout.activity_main);
		// listener to button
		Button createAccount = (Button) findViewById(R.id.createUserBtn);
		final EditText userNameET = (EditText) findViewById(R.id.usernameEt);
		createAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PrefManager.write(getApplicationContext(),
						PrefManager.username, userNameET.getText().toString());
				registerOnBackground();
			}
		});
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				CommonUtilities.DISPLAY_MESSAGE_ACTION));
		final String regId = GCMRegistrar.getRegistrationId(this);
		
		if (regId.equals("")) {
			Log.v(CommonUtilities.TAG, "Not Already registered");
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		} else {
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				Log.d(CommonUtilities.TAG, "already register");
			} else {
				Log.d(CommonUtilities.TAG, "reg id"+regId);
				if (PrefManager.read(getApplicationContext(),
						PrefManager.username, null) != null) {

					registerOnBackground();
					Log.v(CommonUtilities.TAG, "Already registered");
				}
			}
		}
		if(PrefManager.read(getApplicationContext(), PrefManager.username, null)!=null
				&& GCMRegistrar.isRegisteredOnServer(getApplicationContext())){
			Intent i=new Intent(this,EventActivity.class);
			startActivity(i);
		}
	}
	

	private void registerOnBackground() {
		// Try to register again, but not in the UI thread.
		// It's also necessary to cancel the thread onDestroy(),
		// hence the use of AsyncTask instead of a raw thread.
		final Context context = this;
		mRegisterTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				boolean registered = ServerUtilities
						.register(context, GCMRegistrar
								.getRegistrationId(getApplicationContext()));
				// At this point all attempts to register with the app
				// server failed, so we need to unregister the device
				// from GCM - the app will try to register again when
				// it is restarted. Note that GCM will send an
				// unregistered callback upon completion, but
				// GCMIntentService.onUnregistered() will ignore it.
				if (!registered) {
					GCMRegistrar.unregister(context);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mRegisterTask = null;
			}

		};
		mRegisterTask.execute(null, null, null);
	}

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(getApplicationContext());
		super.onDestroy();
	}

}
