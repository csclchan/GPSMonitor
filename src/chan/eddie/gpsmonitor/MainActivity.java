package chan.eddie.gpsmonitor;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, LocationListener {

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 100; // in Milliseconds

	LocationManager locationManager;
	TextView textLatitude;
	TextView textLongitude;
	TextView textAltitude;
	TextView textAccuracy;
	TextView textDirection;
	TextView textUpdatedTime;
	TextView textStartingPoint;
	TextView textDistance;
	Button btnReset;
	Location locStartingPoint, lastPoint;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d("MainActivity", "onCreate");
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize the instances with UI elements 
        textLatitude = (TextView) findViewById(R.id.textLatitude); 
        textLongitude = (TextView) findViewById(R.id.textLongitude); 
        textAltitude = (TextView) findViewById(R.id.textAltitude); 
        textAccuracy = (TextView) findViewById(R.id.textAccuracy); 
        textDirection = (TextView) findViewById(R.id.textDirection); 
        textUpdatedTime = (TextView) findViewById(R.id.textUpdatedTime); 
        textStartingPoint = (TextView) findViewById(R.id.textStartingPoint); 
        textDistance = (TextView) findViewById(R.id.textDistance); 
        btnReset = (Button) findViewById(R.id.btnReset); 
        
        // Set the OnClickListener
        btnReset.setOnClickListener(this);
        
        // init location manager
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locStartingPoint = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	protected void onResume() {
		Log.d("MainActivity", "onResume");
		
		// register the location update event when program start
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locStartingPoint = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
        } else {
        	Toast.makeText(this, "GPS provider not supported", Toast.LENGTH_LONG).show();
        }
		
		super.onResume();
	}
	
    @Override
	protected void onPause() {
    	Log.d("MainActivity", "onPause");
    	
    	// release resource for location manager
		// when this activity is going to background
    	locationManager.removeUpdates(this);

		super.onPause();
	}

	public void onLocationChanged(Location location) {
		textLatitude.setText(String.format("%f", location.getLatitude()));
		textLongitude.setText(String.format("%f", location.getLongitude()));
		textAltitude.setText(String.format("%fm", location.getAltitude()));
		textAccuracy.setText(String.format("%fm", location.getAccuracy()));
		textDirection.setText(String.format("%f", location.getBearing()));
		java.util.Date curDate = new java.util.Date(location.getTime());
		textUpdatedTime.setText(curDate.toString());
		if (locStartingPoint != null) {
			textStartingPoint.setText(String.format("<%f , %f>", 
					locStartingPoint.getLatitude(), locStartingPoint.getLongitude()));
			textDistance.setText(String.format("%fm", location.distanceTo(locStartingPoint)));
		}
		lastPoint = location;
	}

	public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, 
                "Provider disabled by the user. GPS turned off",
                  Toast.LENGTH_LONG).show();
	}

	public void onProviderEnabled(String provider) {
        Toast.makeText(MainActivity.this, 
                "Provider enabled by the user. GPS turned on",
                  Toast.LENGTH_LONG).show();
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(MainActivity.this, 
                "Provider status changed",
                  Toast.LENGTH_LONG).show();
	}

	public void onClick(View v) {
		if (v == btnReset) {
			locStartingPoint = lastPoint;
		}
	}
	
}
