package com.kanykei.slcs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment  {

    private TimePicker timePicker;

    public interface TimeDialogListener {
        void onFinishDialog(String time);
    }

    private TimeDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_time,null);

        timePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
        timePicker.setIs24HourView(true);
        return new android.support.v7.app.AlertDialog.Builder(getContext())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int hour = 0;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    hour = timePicker.getHour();
                                }else{
                                    hour = timePicker.getCurrentHour();
                                }
                                int minute = 0;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    minute = timePicker.getMinute();
                                }else{
                                    minute = timePicker.getCurrentMinute();
                                }
                                mListener = (TimeDialogListener) getTargetFragment();
                                mListener.onFinishDialog(updateTime(hour,minute));
                                dismiss();
                            }
                        })
                .create();
    }

    private String updateTime(int hours, int mins) {

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        return new StringBuilder().append(hours).append(":")
                .append(minutes).toString();


    }

}