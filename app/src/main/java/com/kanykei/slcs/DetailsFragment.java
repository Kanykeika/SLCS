package com.kanykei.slcs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class DetailsFragment extends Fragment {
    TextView tv_title;
    TextView tv_details;
    DBHelper mydb;
    TextView toolbar_title;
    public DetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String lan = loadLanguage("en");
        setLocale(lan);
        View detaisView = inflater.inflate(R.layout.fragment_details, container, false);

        mydb = DBHelper.getInstance(getActivity());
        ArrayList<String> arrayList  = mydb.getInfoDetails(getArguments().getInt("info_id"), lan);
        if(arrayList.size() != 0){
            String title = arrayList.get(0);
            String details = arrayList.get(1);
            toolbar_title = (TextView) getActivity().findViewById(R.id.toolbar_title);
            toolbar_title.setText(title);
            tv_details = (TextView) detaisView.findViewById(R.id.details);
            tv_details.setText(details);
        }


        return detaisView;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
////                getFragmentManager().beginTransaction().replace(R.id.settings_frame, new InfoFragment()).commit();
////getActivity().onBackPressed();
//                Log.i("My tag","case home");
//                if(getFragmentManager().getBackStackEntryCount()>0)
//                    getFragmentManager().popBackStack();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public String loadLanguage(String defaultLanguage)
    {
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        return pref.getString("lan", defaultLanguage);
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Configuration conf = getResources().getConfiguration();
        conf.locale = myLocale;
        getResources().updateConfiguration(conf, null);
    }
}