package cz.vspj.schrek.im.fragment.messages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.activity.MainActivity;
import cz.vspj.schrek.im.model.User;


public class ConversationFragment extends Fragment {
    private static final String ARG_FRIEND = "friend";

    private User friend;

    public static ConversationFragment newInstance(User friend) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIEND, friend);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            friend = (User) getArguments().getSerializable(ARG_FRIEND);
        }

        setHasOptionsMenu(true);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showMenuIcon(false);

        ActionBar actionBar = mainActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View customBar = LayoutInflater.from(getContext()).inflate(R.layout.conversation_toolbar, null);

        actionBar.setCustomView(customBar);
        actionBar.setDisplayShowCustomEnabled(true);

        return view;
    }


    @Override
    public void onStop() {
        super.onStop();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showMenuIcon(true);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setCustomView(null);
        mainActivity.createDefaultToolbar();
    }
}



