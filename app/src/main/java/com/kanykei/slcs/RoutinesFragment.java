package com.kanykei.slcs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class RoutinesFragment extends Fragment{
    private TextView wake;
    private TextView sleep;

    public RoutinesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View routineView = inflater.inflate(R.layout.fragment_routines, container, false);

        wake = (TextView) routineView.findViewById(R.id.wake_up);
        sleep = (TextView) routineView.findViewById(R.id.go_to_sleep);

        wake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("message", "wake");
                SetTimeFragment wake_time_fragment = new SetTimeFragment();
                wake_time_fragment.setArguments(bundle);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.routines_frag_container, wake_time_fragment).addToBackStack(null).commit();
            }
        });

        sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("message", "sleep");
                SetTimeFragment sleep_time_fragment = new SetTimeFragment();
                sleep_time_fragment.setArguments(bundle);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.routines_frag_container, sleep_time_fragment).addToBackStack(null).commit();
            }
        });

        return routineView;
    }


}


