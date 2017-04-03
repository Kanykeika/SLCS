package com.kanykei.slcs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends Fragment{

    public SettingsFragment() {
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
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        String[] languages = { getContext().getString(R.string.en_lang), getContext().getString(R.string.ru_lang), getContext().getString(R.string.kg_lang) };
        Integer[] images = { R.drawable.united_kingdom, R.drawable.russia, R.drawable.kyrgyzstan };
        final List<String> Locale_list = new ArrayList<String>(Arrays.asList(getContext().getString(R.string.en_lang), getContext().getString(R.string.ru_lang), getContext().getString(R.string.kg_lang)));

        final Spinner spinner = (Spinner) settingsView.findViewById(R.id.spinner_lang);

        SpinnerAdapter adapter = new SpinnerAdapter(getContext(), languages, images);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(Locale_list.indexOf(lan));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                switch (arg2){
                    case 0:
                        setLocale("en");
                        break;
                    case 1:
                        setLocale("ru");
                        break;
                    case 2:
                        setLocale("kg");
                        break;
                    default:
                        setLocale("en");
                        break;
                }
           }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        return settingsView;
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

    public class SpinnerAdapter extends ArrayAdapter<String> {

        private Context ctx;
        private String[] contentArray;
        private Integer[] imageArray;

        public SpinnerAdapter(Context context, String[] objects,
                              Integer[] imageArray) {
            super(context,  R.layout.spinner_item, R.id.spinnerTextView, objects);
            this.ctx = context;
            this.contentArray = objects;
            this.imageArray = imageArray;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.spinner_item, parent, false);
            final List<String> Locale_list = new ArrayList<String>(Arrays.asList(getContext().getString(R.string.en_lang), getContext().getString(R.string.ru_lang), getContext().getString(R.string.kg_lang)));

            TextView textView = (TextView) row.findViewById(R.id.spinnerTextView);
            textView.setText(contentArray[position]);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lan = Locale_list.get(position);
                    setLocale(Locale_list.get(position));
                    saveLanguage(lan);
                    restartActivity();
                }
            });

            ImageView imageView = (ImageView)row.findViewById(R.id.spinnerImages);
            imageView.setImageResource(imageArray[position]);

            return row;
        }

    }

    public void saveLanguage(String language)
    {
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("lan", language);
        editor.apply();
    }
    public void restartActivity() {
        if (Build.VERSION.SDK_INT >= 11) {
            getActivity().recreate();
        }
        else {
            Intent intent = getActivity().getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            getActivity().finish();
            getActivity().overridePendingTransition(0, 0);
            startActivity(intent);
            getActivity().overridePendingTransition(0, 0);
        }
    }
}