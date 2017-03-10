package com.kanykei.slcs;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.EditText;


public class NewRoomActivity extends AppCompatActivity {
    private EditText inputName;
    private TextInputLayout inputLayoutName;
    private Toolbar toolbar;
    private DBHelper mydb ;
    int id_To_Update = 0;
    private Cursor check_room_for_duplicate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Create a new room");

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputName = (EditText) findViewById(R.id.input_name);

        mydb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            int Value = extras.getInt("id");

            if(Value>0){
                //means this is the view part not the add room part.
                Cursor rs = mydb.getData(Value);
                id_To_Update = Value;
                rs.moveToFirst();
                String col_name = rs.getString(rs.getColumnIndex(DBHelper.ROOMS_COLUMN_NAME));
                if (!rs.isClosed())  {
                    rs.close();
                }
                Button b = (Button)findViewById(R.id.btn_save);
                b.setVisibility(View.INVISIBLE);

                inputName.setText((CharSequence)col_name);
                inputName.setFocusable(false);
                inputName.setClickable(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            int Value = extras.getInt("id");
            if(Value>0){
                getMenuInflater().inflate(R.menu.display_rooms, menu);
            }
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.Edit_Room:
                Button b = (Button)findViewById(R.id.btn_save);
                b.setVisibility(View.VISIBLE);
                inputName.setEnabled(true);
                inputName.setFocusableInTouchMode(true);
                inputName.setClickable(true);
                return true;
            case R.id.Delete_Room:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteRoom).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mydb.deleteRoom(id_To_Update);
                        Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        HomeFragment fragmentObject = new HomeFragment();
                        if(fragmentObject != null){
                            FragmentManager fm = getSupportFragmentManager();
                            fm.beginTransaction().replace(android.R.id.content,fragmentObject).commit();
                        }
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void run(View view) {
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int Value = extras.getInt("id");
            if(Value>0){
                if (!validateNameForUpdating()) {
                    return;
                }
                else if(mydb.updateRoom(id_To_Update,inputName.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    HomeFragment fragmentObject = new HomeFragment();
                    if(fragmentObject != null){
                        FragmentManager fm = getSupportFragmentManager();
                        fm.beginTransaction().replace(android.R.id.content,fragmentObject).commit();
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                }
            } else{
                if (!validateNameForInsertion()) {
                    return;
                }
                else if (mydb.insertRoom(inputName.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "done",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "not done",
                            Toast.LENGTH_SHORT).show();
                }
                HomeFragment fragmentObject = new HomeFragment();
                int pager_id = getResources().getIdentifier("viewpager","ViewPager", getPackageName());
                if(fragmentObject != null){
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction().replace(pager_id,fragmentObject).commit();
                }
            }
        }
    }

    private boolean validateNameForUpdating() {
        check_room_for_duplicate = mydb.getData(getIntent().getExtras().getInt("id"));
        check_room_for_duplicate.moveToFirst();
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else if (inputName.getText().toString() == check_room_for_duplicate.getString(check_room_for_duplicate.getColumnIndex("name"))) {
            inputLayoutName.setError(getString(R.string.err_msg_duplicate_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateNameForInsertion() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
