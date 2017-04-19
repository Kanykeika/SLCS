package com.kanykei.slcs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class InfoFragment extends Fragment{

    public InfoFragment() {
        // Required empty public constructor
    }
    DBHelper mydb;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String lan = loadLanguage("en");
        setLocale(lan);
        View infoView = inflater.inflate(R.layout.fragment_info, container, false);
        mydb = DBHelper.getInstance(getContext());
        TextView toolbar_title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.help);
        ArrayList<String> infoList = mydb.getInfoTitles();
        final ListView list = (ListView) infoView.findViewById(R.id.listViewInfo);
        list.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.info_listview, R.id.textViewInfo, infoList));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("info_id", position+1);
                DetailsFragment detailsFragment = new DetailsFragment();
                detailsFragment.setArguments(dataBundle);
                getFragmentManager().beginTransaction().replace(R.id.help_frame, detailsFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit();
            }
        });

        return infoView;
    }



    public String loadLanguage(String defaultLanguage)
    {
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        return pref.getString("lan", defaultLanguage);
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Configuration conf = getResources().getConfiguration();
        conf.locale = myLocale;
        getResources().updateConfiguration(conf, null);
    }
}