package rjt.com.android_redfin_app.buyer;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rjt.com.android_redfin_app.R;
import rjt.com.android_redfin_app.buyer.restrofit.RetrofitInstance;
import rjt.com.android_redfin_app.buyer.restrofit.UsesServices;

import static android.app.Activity.RESULT_OK;


public class SellPropertyFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private static final int PICK_IMAGE = 100;
    private static int REQUEST_IMAGE_CAPTURE = 1;
    String stringOfImageUri;
    EditText firstName_et, lastName_et, mobile_number_et, email_et, property_cost_et, property_size_et, property_description_et;
    GoogleApiClient mGoogleApiClient;
    Spinner propertyname_spinner, property_type_spinner, property_cat_spinner;
    Button next_btn, openCamera, openGallery, editProperty, delete_property;
    String property_name, property_category, property_type;
    TextView property_latitude_tv, property_longitude_tv;
    Uri imageUri;
    ImageView image_view_one, image_view_two, image_view_three;
    String propertyId;
    private AutoCompleteTextView autoCompleteTvSellHome;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;

    public SellPropertyFragment() {


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mGoogleApiClient = new GoogleApiClient.Builder((Maps) getContext()).addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage((Maps) getContext(), this)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Caught Exception", "");

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell_property, container, false);


        autoCompleteTvSellHome = view.findViewById(R.id.autoCompleteTvSellHome);
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter((Maps) getContext(), mGoogleApiClient, LAT_LNG_BOUNDS, null);
        autoCompleteTvSellHome.setAdapter(mPlaceAutocompleteAdapter);

        firstName_et = view.findViewById(R.id.firstName_et);
        lastName_et = view.findViewById(R.id.lastName_et);
        mobile_number_et = view.findViewById(R.id.mobile_number_et);
        email_et = view.findViewById(R.id.email_et);
        property_cost_et = view.findViewById(R.id.property_cost_et);
        property_size_et = view.findViewById(R.id.property_size_et);
        property_description_et = view.findViewById(R.id.property_description_et);
        property_latitude_tv = view.findViewById(R.id.property_latitude_tv);
        property_longitude_tv = view.findViewById(R.id.property_longitude_tv);
        propertyname_spinner = view.findViewById(R.id.propertyname_spinner);
        image_view_one = view.findViewById(R.id.image_view_one);
        editProperty = view.findViewById(R.id.editProperty);
        editProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPropertyApiCall();
            }
        });
        delete_property = view.findViewById(R.id.delete_property);
        delete_property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePropertyApiCall();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.property_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        propertyname_spinner.setAdapter(adapter);

        propertyname_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                property_name = propertyname_spinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });


        property_type_spinner = view.findViewById(R.id.property_type_spinner);
        ArrayAdapter<CharSequence> adapterForType = ArrayAdapter.createFromResource(getContext(),
                R.array.property_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        property_type_spinner.setAdapter(adapterForType);
        property_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                property_type = property_type_spinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        property_cat_spinner = view.findViewById(R.id.property_cat_spinner);
        ArrayAdapter<CharSequence> adapterForCategory = ArrayAdapter.createFromResource(getContext(),
                R.array.property_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        property_cat_spinner.setAdapter(adapterForCategory);
        property_cat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                property_category = property_cat_spinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        next_btn = view.findViewById(R.id.next);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geolocate();
            }
        });

        openCamera = view.findViewById(R.id.openCamera);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        openGallery = view.findViewById(R.id.openGallery);
        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        return view;
    }

    private void deletePropertyApiCall() {

        UsesServices usesServices = RetrofitInstance.getRetrofitInstance().create(UsesServices.class);
        Call<ResponseBody> call = usesServices.deleteProperty(propertyId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("response", "Sucessfully Deleted" + response.isSuccessful());
                Toast.makeText(getContext(), "Successfully Deleted " + response.isSuccessful(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    private void editPropertyApiCall() {
        UsesServices usesServices = RetrofitInstance.getRetrofitInstance().create(UsesServices.class);
//        Call<ResponseBody> call = usesServices.editProperty(propertyId);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.e("response", "Sucessfully Deleted" + response.isSuccessful());
//                Toast.makeText(getContext(), "Successfully Deleted " + response.isSuccessful(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });

    }

    private void geolocate() {
        String addressString = autoCompleteTvSellHome.getText().toString();
        String name = firstName_et.getText().toString() + " " + lastName_et.getText().toString();
        String mobile = mobile_number_et.getText().toString();
        String email = email_et.getText().toString();
        String cost = property_cost_et.getText().toString();
        String size = property_size_et.getText().toString();
        String description = property_description_et.getText().toString();
        Geocoder geocoder = new Geocoder((Maps) getContext());
        List<Address> list = new ArrayList<Address>();
        try {
            list = geocoder.getFromLocationName(addressString, 1);
        } catch (Exception e) {
            Log.e("Excepttion Caught", "In geoLocate Method");
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.e("Long", address.getLongitude() + "");
            Log.e("Lat", address.getLatitude() + "");
            Log.e("found location:", "" + address.toString());
            String longitude = address.getLongitude() + "";
            String latitude = address.getLatitude() + "";
            property_longitude_tv.setText(longitude);
            property_latitude_tv.setText(latitude);
            sendDataToAPI(property_name, property_type, property_category, addressString, latitude, longitude, cost, size, description);

        }
    }

    private void sendDataToAPI(String property_name, String property_type, String property_category, String addressString, String latitude, String longitude, String cost, String size, String description) {

        //Network Call

        UsesServices usesServices = RetrofitInstance.getRetrofitInstance().create(UsesServices.class);
        Call<ResponseBody> call = usesServices.addProperty(property_name, property_type, property_category, addressString, "", "", latitude, longitude, cost, size, description, stringOfImageUri, "", "");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("response", "Sucessfully added" + response.isSuccessful());
                Toast.makeText(getContext(), "Successfully added " + response.isSuccessful(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage((Maps) getContext());
            mGoogleApiClient.disconnect();
        }
        //awssdejobs@amazon.com
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            image_view_one.setImageURI(imageUri);
            stringOfImageUri = imageUri.toString();
        }
    }
}

