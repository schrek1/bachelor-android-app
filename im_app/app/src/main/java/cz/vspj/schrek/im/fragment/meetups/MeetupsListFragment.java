package cz.vspj.schrek.im.fragment.meetups;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.firebase.database.*;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.activity.MainActivity;
import cz.vspj.schrek.im.common.Utils;
import cz.vspj.schrek.im.fragment.meetups.adapter.MeetupAdapter;
import cz.vspj.schrek.im.fragment.messages.adapter.MessagesAdapter;
import cz.vspj.schrek.im.model.Meetup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by schrek on 29.03.2017.
 */

public class MeetupsListFragment extends Fragment {

    private ListView listView;
    private MeetupAdapter adapter;
    private List<Meetup> meetups;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_meetups_list, container, false);

        final MainActivity mainActivity = (MainActivity) getActivity();

        View addButton = view.findViewById(R.id.addMeetup);
        addButton.setOnTouchListener(Utils.getViewClickEffect());
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.pushFragment(new MeetupAddFragment());
            }
        });

        meetups = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.meetupList);
        adapter = new MeetupAdapter(getContext(), R.layout.meetup_list_item, meetups);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity) getActivity()).pushFragment(MeetupDetailFragment.newInstance(adapter.getItem(position)));
            }
        });

        fillMeetups();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Schůzky");
    }


    public void fillMeetups() {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("app").child("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot notifications) {
                for (final DataSnapshot notification : notifications.getChildren()) {
                    final Meetup meetup = new Meetup();
                    db.child("app").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot users) {
                            for (DataSnapshot device : notification.child("devices").getChildren()) {
                                String inviteUid = device.getKey();
                                for (DataSnapshot user : users.getChildren()) {
                                    String userUid = user.getKey();
                                    if (userUid.equals(inviteUid)) {
                                        String username = (String) user.child("info").child("username").getValue();
                                        meetup.invitedUsers.add(username);
                                        break;
                                    }
                                }
                            }
                            meetup.icon = (String) notification.child("icon").getValue();
                            meetup.message = (String) notification.child("message").getValue();
                            meetup.term = (String) notification.child("time").getValue();
                            meetup.title = (String) notification.child("title").getValue();

                            meetups.add(meetup);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
