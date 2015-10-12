package com.assignment.fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.R;
import com.assignment.common.ConnectionDetector;
import com.assignment.models.Hotel;
import com.github.clans.fab.FloatingActionButton;
import com.lw.legoroid.async.service.LegoService;
import com.lw.legoroid.async.service.OnServiceTask;
import com.lw.legoroid.async.service.RequestMethod;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.ProgressView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchHotelsFragment extends Fragment implements View.OnClickListener{

    private ConnectionDetector cd;
    private Context context;

    private FloatingActionButton fabLoadAddNewHotelForm;
    private Button fabSearchHotel;
    private EditText edtSearchHotelName;
    ListView listHotels;

    List<Hotel> hotels = new ArrayList<Hotel>();

    private ProgressView progressView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_hotels, container, false);
        context = getActivity();

        cd = new ConnectionDetector(context);

        if(!cd.isConnectingToInternet()){
            Toast.makeText(context,"Please check your internet connection...",Toast.LENGTH_SHORT).show();
        }

        progressView = (ProgressView)rootView.findViewById(R.id.progress_pv_linear);

        listHotels = (ListView)rootView.findViewById(R.id.list_hotels);

        edtSearchHotelName = (EditText)rootView.findViewById(R.id.edt_hotel_name_search);

        fabSearchHotel = (Button) rootView.findViewById(R.id.btn_search_hotel);
        fabSearchHotel.setOnClickListener(this);

        fabLoadAddNewHotelForm = (FloatingActionButton) rootView.findViewById(R.id.fab_load_add_new);
        fabLoadAddNewHotelForm.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_search_hotel:
                if(validate()){
                    searchHotelsByName(edtSearchHotelName.getText().toString());
                }
                break;

            case R.id.fab_load_add_new:
                Fragment fragmentResult = new AddHotelFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack("HOTEL_LIST", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.container_body, fragmentResult).commit();
                break;
        }
    }

    private void searchHotelsByName(String hotelName){

        try {
            LegoService legoService = new LegoService(getActivity());
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
                        if (jsonObject.getInt("status") == 1) {
                            JSONArray jsonArrayMessages = jsonObject.getJSONArray("value");
                            ObjectMapper objectMapper = new ObjectMapper();

                            hotels = objectMapper.readValue(jsonArrayMessages.toString(), new TypeReference<ArrayList<Hotel>>(){});
                            listHotels.setAdapter(new ListAdapterHotels());

                        }else{
                            throw new Exception();
                        }
                    }catch(Exception e){
                        Toast.makeText(getActivity(), "Unable to load hotels", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            legoService.makeRequest("/hotel/keyword/" + hotelName ,new ArrayList<NameValuePair>(), RequestMethod.GET);

        }catch(Exception e){

            Toast.makeText(getActivity(), "Unable to load hotels", Toast.LENGTH_SHORT).show();

        }

    }

    static class ViewHolder {
        public int position;
        public TextView rowHotelId;
        public TextView rowHotelName;
        public TextView rowHotelAddress;

    }

    class ListAdapterHotels extends BaseAdapter implements View.OnClickListener{
        @Override
        public int getCount() {
            return hotels.size();
        }

        @Override
        public Object getItem(int position) {
            return hotels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {

                LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.row_hotel_details, null);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.rowHotelId = (TextView) convertView.findViewById(R.id.txt_hotel_id);
                viewHolder.rowHotelName = (TextView) convertView.findViewById(R.id.txt_hotel_name);
                viewHolder.rowHotelAddress = (TextView) convertView.findViewById(R.id.txt_hotel_address);

                convertView.setOnClickListener(this);
                convertView.setTag(viewHolder);
            }

            ViewHolder viewHolder = (ViewHolder)convertView.getTag();
            viewHolder.position = position;

            Hotel hotel = hotels.get(position);

            viewHolder.rowHotelId.setText(String.valueOf(hotel.getId()));
            viewHolder.rowHotelName.setText(hotel.getName());
            viewHolder.rowHotelAddress.setText(hotel.getAddress().getAddressName());

            return  convertView;

        }

        @Override
        public void onClick(View v) {
            ViewHolder viewHolder = (ViewHolder)v.getTag();
            final int position = viewHolder.position;
        }
    }

    private boolean validate(){

        boolean state = true;
        if(edtSearchHotelName.getText().toString().equals("")){//check name
            edtSearchHotelName.setError("Please input the hotel name");
            state = false;
        }
        if(!cd.isConnectingToInternet()){
            Toast.makeText(context,"Please check your internet connection...",Toast.LENGTH_SHORT).show();
            state = false;
        }

        return state;
    }
}
