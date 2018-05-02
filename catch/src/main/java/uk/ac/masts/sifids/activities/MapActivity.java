package uk.ac.masts.sifids.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchLocation;

public class MapActivity extends AppCompatActivityWithMenuBar implements OnMapReadyCallback {

    CatchDatabase db;
    List<CatchLocation> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        db = CatchDatabase.getInstance(getApplicationContext());

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {

        points = new ArrayList();

        Runnable r = new Runnable(){
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
                cal.set(Calendar.HOUR_OF_DAY,0);
                cal.set(Calendar.MINUTE,0);
                cal.set(Calendar.SECOND,0);
                points = db.catchDao().getLocationsSince(cal.getTime());
            }
        };

        Thread newThread= new Thread(r);
        newThread.start();
        try {
            newThread.join();
        }
        catch (InterruptedException ie) {

        }

        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        boolean first = true;
        for (CatchLocation point : points) {
            map.addMarker(new MarkerOptions().position(point.getLatLng()).title(point.getCoordinates()));
            if (first) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(point.getLatLng(), (float) 10.0));
                first = false;
            }
        }
    }

}
