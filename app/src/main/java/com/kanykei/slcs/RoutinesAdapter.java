package com.kanykei.slcs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class RoutinesAdapter extends ArrayAdapter<Room> {

    private final Context context;
    private final ArrayList<Room> roomsArrayList;
    private final String action;
    private DBHelper mydb;
    private TextView timeView;
    private ImageView deleteBtn;


    public RoutinesAdapter(Context context, ArrayList<Room> itemsArrayList, String toDo) {

        super(context, R.layout.routine_listview, itemsArrayList);

        this.context = context;
        this.roomsArrayList = itemsArrayList;
        this.action = toDo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.routine_listview, parent, false);

        // 3. Get the two text view from the rowView
        TextView labelView = (TextView) rowView.findViewById(R.id.room_id);
        TextView valueView = (TextView) rowView.findViewById(R.id.room_name);
        timeView = (TextView) rowView.findViewById(R.id.display_time);
        deleteBtn = (ImageView)  rowView.findViewById(R.id.btn_delete);
        deleteBtn.setVisibility(View.INVISIBLE);

        // 4. Set the text for textView
        labelView.setText(String.valueOf(roomsArrayList.get(position).getId()));
        valueView.setText(String.valueOf(roomsArrayList.get(position).getName()));

        if(action == "wake"){
            if(roomsArrayList.get(position).getWake() != null) {
                timeView.setText(String.valueOf(roomsArrayList.get(position).getWake()));
                deleteBtn.setVisibility(View.VISIBLE);
            }else{
                timeView.setText(getContext().getText(R.string.set_wake));
                deleteBtn.setVisibility(View.INVISIBLE);
            }
        } else if(action == "sleep"){
            if(roomsArrayList.get(position).getSleep() != null) {
                timeView.setText(String.valueOf(roomsArrayList.get(position).getSleep()));
                deleteBtn.setVisibility(View.VISIBLE);
            } else{
                timeView.setText(getContext().getText(R.string.set_sleep));
                deleteBtn.setVisibility(View.INVISIBLE);
            }
        }

        // 5. get database
        mydb = DBHelper.getInstance(getContext());
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if(action.equals("sleep")){
                    mydb.updateGoSleepTimer(roomsArrayList.get(position).getId(),null);
                    timeView.setText(getContext().getText(R.string.set_sleep));
                    deleteBtn.setVisibility(View.INVISIBLE);

                }else if(action.equals("wake")){
                    mydb.updateWakeUpTimer(roomsArrayList.get(position).getId(),null);
                    timeView.setText(getContext().getText(R.string.set_wake));
                    deleteBtn.setVisibility(View.INVISIBLE);
                }

            }
        });

        // 5. return rowView
        return rowView;
    }

}
