package com.kanykei.slcs;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class SettingsLanguageFragment extends Fragment {

    private TextView en;
    private TextView ru;
    private TextView kg;

    public SettingsLanguageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View languageView = inflater.inflate(R.layout.fragment_settings_language, container, false);

        en = (TextView) languageView.findViewById(R.id.lang_en);
        ru = (TextView) languageView.findViewById(R.id.lang_ru);
        kg = (TextView) languageView.findViewById(R.id.lang_kg);
        final Configuration config = new Configuration();
        en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                config.locale = new Locale("en");
                getResources().updateConfiguration(config, null);
            }
        });
        ru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                config.locale = new Locale("ru");
                getResources().updateConfiguration(config, null);
            }
        });
        kg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                config.locale = new Locale("kg");
                getResources().updateConfiguration(config, null);
            }
        });
        return languageView;
    }

}


