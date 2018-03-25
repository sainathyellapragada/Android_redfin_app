package rjt.com.android_redfin_app.buyer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import rjt.com.android_redfin_app.R;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapsFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    GoogleMap map;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //widgets
    private AutoCompleteTextView mAutocompleteTextView;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getLocationPermission();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        mAutocompleteTextView = view.findViewById(R.id.searchBox);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        supportMapFragment.getMapAsync(this);
        try {
            mGoogleApiClient = new GoogleApiClient.Builder((Maps) getContext()).addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage((Maps) getContext(), this)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Caught Exception","");
        }

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter((Maps) getContext(), mGoogleApiClient, LAT_LNG_BOUNDS, null);
        mAutocompleteTextView.setAdapter(mPlaceAutocompleteAdapter);
        mAutocompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER) {
                    // execute our method for geoLocate();
                    geoLocate();

                }


                return false;
            }
        });

    }

    private void geoLocate() {
        String searchString = mAutocompleteTextView.getText().toString();
        Geocoder geocoder = new Geocoder((Maps) getContext());
        List<Address> list = new ArrayList<Address>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (Exception e) {
            Log.e("Excepttion Caught", "In geoLocate Method");
        }
        if (list.size() > 0) {
            Address address = list.get(0);

            Log.e("found location:", "" + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLatitude()), 15, address.getAddressLine(0));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getDeviceLocation();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
//        map.getUiSettings().setMyLocationButtonEnabled(true);


    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //set a boolean
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions((Maps) getContext(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }

        } else {
            ActivityCompat.requestPermissions((Maps) getContext(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void getDeviceLocation() {
        Log.e("getting_device_locatio", "GETTING DEVICE LOCATION");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {

                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.e("onComplete", "SuccessFoundLocation");
                            Location currentLocation = (Location) task.getResult();


                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f, "My Location");
                        } else {
                            Toast.makeText(getContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("FOUND EXCEPTION", "EXCEPTION FOUND IN getDeviceLocation()");
        }


    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.e("Moving Camera", "Moving camera");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        map.addMarker(options);

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage((Maps) getContext());
            mGoogleApiClient.disconnect();
        }
    }
}
