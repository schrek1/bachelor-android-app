package cz.vspj.schrek.im.fragment.meetups;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.activity.MainActivity;
import cz.vspj.schrek.im.fragment.messages.MessagingFragment;
import cz.vspj.schrek.im.model.Meetup;
import cz.vspj.schrek.im.model.Message;
import cz.vspj.schrek.im.model.User;

/**
 * Created by schrek on 29.03.2017.
 */

public class MeetupDetailFragment extends Fragment {
    private static final String ARG_MEETUP = "meetup";

    public static MeetupDetailFragment newInstance(Meetup meetup) {
        MeetupDetailFragment fragment = new MeetupDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEETUP, meetup);
        fragment.setArguments(args);
        return fragment;
    }


    private Meetup meetup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            meetup = (Meetup) getArguments().getSerializable(ARG_MEETUP);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_meetup_detail, container, false);

        ((TextView) view.findViewById(R.id.meetup_title)).setText(meetup.title);
        ((TextView) view.findViewById(R.id.meetup_message)).setText(meetup.message);
        String[] term = meetup.term.split(" ");
        ((TextView) view.findViewById(R.id.meetup_date)).setText(term[0]);
        ((TextView) view.findViewById(R.id.meetup_time)).setText(term[1]);

        ListView listView = (ListView) view.findViewById(R.id.meetup_invites);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, meetup.invitedUsers.toArray(new String[meetup.invitedUsers.size()]));
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Detail schůzky");
        setCustomToolbar();
    }

    private void setCustomToolbar() {
        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showMenuIcon(false);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        setDefaultToolbar();
    }

    private void setDefaultToolbar() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showMenuIcon(true);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setCustomView(null);
        mainActivity.createDefaultToolbar();
    }


    @Override
    public void onStop() {
        super.onStop();
    }
}
