package cz.vspj.schrek.im.fragment.meetups;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.firebase.database.*;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.activity.MainActivity;
import cz.vspj.schrek.im.common.LoggedUser;
import cz.vspj.schrek.im.fragment.meetups.adapter.ChoiceFriendAdapter;
import cz.vspj.schrek.im.fragment.meetups.adapter.SelectedFriendAdapter;
import cz.vspj.schrek.im.model.User;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by schrek on 29.03.2017.
 */

public class MeetupAddFragment extends Fragment {

    private EditText dateInput;
    private EditText timeInput;
    private EditText messageInput;
    private EditText titleInput;


    private ListView listView;
    private SelectedFriendAdapter sfAdapter;
    private List<User> selectedUsers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_meetup_add, container, false);

        dateInput = (EditText) view.findViewById(R.id.meetup_date);
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener, pickerDate.get(Calendar.YEAR), pickerDate.get(Calendar.MONTH), pickerDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        timeInput = (EditText) view.findViewById(R.id.meetup_time);
        timeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker = new TimePickerDialog(getActivity(), timeSetListener, pickerTime.get(Calendar.HOUR_OF_DAY), pickerTime.get(Calendar.MINUTE), true);//Yes 24 hour time
                mTimePicker.show();
            }
        });

        view.findViewById(R.id.meetup_add_butt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder listDialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
                listDialog.setTitle("Výběr přátel");
                listDialog.setCancelable(false);

                final ChoiceFriendAdapter cfAdapter = new ChoiceFriendAdapter(getContext(), R.layout.choice_friend_list_item, LoggedUser.getFriends(), selectedUsers);

                listDialog.setNegativeButton("Zrušit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                listDialog.setPositiveButton("Potvrdit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedUsers.clear();
                        selectedUsers.addAll(cfAdapter.getCheckedUsers());
                        sfAdapter.notifyDataSetChanged();
                    }
                });

                listDialog.setAdapter(cfAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                listDialog.show();
            }
        });


        listView = (ListView) view.findViewById(R.id.selected_friends);
        sfAdapter = new SelectedFriendAdapter(getContext(), R.layout.friend_list_item, selectedUsers);
        listView.setAdapter(sfAdapter);

        messageInput = (EditText) view.findViewById(R.id.meetup_message);
        titleInput = (EditText) view.findViewById(R.id.meetup_title);

        view.findViewById(R.id.meetup_save_butt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child("app").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("icon", "");
                        result.put("devices", new HashMap<String, Object>() {{
                            for (User user : selectedUsers) {
                                String instanceId = (String) dataSnapshot.child(user.uid).child("info").child("instanceId").getValue();
                                put(user.uid, instanceId);
                            }
                        }});
                        result.put("message", messageInput.getText().toString());
                        result.put("time", dateInput.getText().toString() + " " + timeInput.getText().toString());
                        result.put("title", titleInput.getText().toString());
                        result.put("type", "meetup");

                        db.child("app").child("notifications").push().setValue(result);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Toast.makeText(getContext(), "Schuzka vytvorena", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Přidat schůzku");
        setCustomToolbar();
    }

    private void setCustomToolbar() {
        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showMenuIcon(false);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
    }


    @Override
    public void onStop() {
        super.onStop();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showMenuIcon(true);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
    }


    Calendar pickerDate = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pickerDate.set(Calendar.YEAR, year);
            pickerDate.set(Calendar.MONTH, monthOfYear);
            pickerDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);


            String myFormat = "MM/dd/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            dateInput.setText(sdf.format(pickerDate.getTime()));
        }

    };


    Calendar pickerTime = Calendar.getInstance();

    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            pickerTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            pickerTime.set(Calendar.MINUTE, minute);

            timeInput.setText(hourOfDay + ":" + minute);
        }
    };


}
