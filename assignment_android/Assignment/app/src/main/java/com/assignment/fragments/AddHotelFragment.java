package com.assignment.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.assignment.R;
import com.assignment.common.ConnectionDetector;
import com.assignment.models.Address;
import com.assignment.models.Hotel;
import com.lw.legoroid.async.service.LegoService;
import com.lw.legoroid.async.service.OnServiceTask;
import com.lw.legoroid.async.service.RequestMethod;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddHotelFragment extends Fragment implements View.OnClickListener{

    private ConnectionDetector cd;
    private Context context;

    private EditText edtName;
    private EditText edtAddress;
    private double hotelLocationLatitude = -1;
    private double hotelLocationLongitude = -1;

    private ProgressView progressView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_hotel, container, false);
        context = getActivity();
        cd = new ConnectionDetector(context);

        if(!cd.isConnectingToInternet()){
            Toast.makeText(context,"Please check your internet connection...",Toast.LENGTH_SHORT).show();
        }

        progressView = (ProgressView)rootView.findViewById(R.id.progress_pv_linear);

        edtName = (EditText) rootView.findViewById(R.id.edt_hotel_name);
        edtAddress = (EditText) rootView.findViewById(R.id.edt_hotel_address);

        Button btnAddHotel = (Button) rootView.findViewById(R.id.btn_add_hotel);
        btnAddHotel.setOnClickListener(this);

        Button btnClear = (Button) rootView.findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_add_hotel:
                addNewHotel();
                break;

            case R.id.btn_clear:
                clearForm();
                break;
        }

    }


    private void addNewHotel(){

        if(validateForm() && setUpGPSReceiver()){

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("name",edtName.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("address.addressName", edtAddress.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("address.locationLatitude", String.valueOf(hotelLocationLatitude)));
            nameValuePairs.add(new BasicNameValuePair("address.locationLongitude", String.valueOf(hotelLocationLongitude)));

            try {
                LegoService legoService = new LegoService(context);
                legoService.setOnServiceTask(new OnServiceTask() {
                    @Override
                    public void onBeforeExecute() {
                        progressView.setVisibility(View.VISIBLE);
                        progressView.start();
                    }

                    @Override
                    public void onAfterExecute(JSONObject jsonObject) {
                        progressView.stop();
                        progressView.setVisibility(View.GONE);

                        try {
                            int status = jsonObject.getInt("status");
                            if(status == 1){
                                Toast.makeText(context, "Successfully Add New Hotel", Toast.LENGTH_SHORT).show();
                            }else{
                                throw new Exception();
                            }

                        }catch (Exception e){
                            Toast.makeText(context, "Error occur in adding New Hotel", Toast.LENGTH_SHORT).show();
                        }

                        clearForm();
                    }
                });
                legoService.makeRequest("/hotel",nameValuePairs,RequestMethod.POST);

            }catch(Exception e){
                Toast.makeText(context,"Error : " + e.getMessage(),Toast.LENGTH_SHORT).show();
                clearForm();
            }

        }


    }

    private void clearForm(){
        edtName.setText("");
        edtAddress.setText("");
    }

    private boolean validateForm(){

        boolean state = true;
        if(edtName.getText().toString().equals("")){//check name
            edtName.setError("Please input the hotel name");
            state = false;
        }
        if(edtAddress.getText().toString().equals("")){//check name
            edtAddress.setError("Please input the hotel address");
            state = false;
        }
        if(!cd.isConnectingToInternet()){
            Toast.makeText(context,"Please check your internet connection...",Toast.LENGTH_SHORT).show();
            state = false;
        }

        return state;
    }

    public boolean setUpGPSReceiver(){

        boolean is_enable_gps_service = true;

        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isGPSEnabled && !isNetworkEnabled){
            showGPSSettingsAlert();
            is_enable_gps_service = false;
        }
        else{
            if(isNetworkEnabled){
                try {
                    hotelLocationLatitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
                    hotelLocationLongitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
                }catch (Exception e){
                    //Toast.makeText(context,"Problem occur in NetWork Provider",Toast.LENGTH_SHORT).show();
                    Log.e("ERROR","Problem occur in NetWork Provider");
                }
            }
            else {
                try{
                    hotelLocationLatitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                    hotelLocationLongitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                }
                catch(Exception e){
                    //Toast.makeText(context,"Problem occur in GPS Service",Toast.LENGTH_SHORT).show();
                    Log.e("ERROR","Problem occur in GPS Service");
                }
            }

        }

        return is_enable_gps_service;

    }

    public void showGPSSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("GPS is settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}
