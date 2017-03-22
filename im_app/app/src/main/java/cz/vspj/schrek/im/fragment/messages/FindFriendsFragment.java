package cz.vspj.schrek.im.fragment.messages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.activity.MainActivity;
import cz.vspj.schrek.im.common.LoggedUser;
import cz.vspj.schrek.im.fragment.friends.adapter.UsersAdapter;
import cz.vspj.schrek.im.fragment.messages.adapter.FriendAdapter;
import cz.vspj.schrek.im.model.User;

import java.util.ArrayList;
import java.util.List;


public class FindFriendsFragment extends Fragment implements LoggedUser.FriendChangeListener {


    private List<User> friends;

    private TextView notFoundLabel;
    private EditText searchInput;

    private ListView listView;
    private FriendAdapter adapter;
    private List<User> matches = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        friends = LoggedUser.getFriends();
        matches.addAll(friends);
        adapter = new FriendAdapter(getContext(), R.layout.friend_list_item, matches);

        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_find_friends, container, false);

        notFoundLabel = (TextView) view.findViewById(R.id.notFoundLabel);
        searchInput = (EditText) view.findViewById(R.id.searchInput);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showMenuIcon(false);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        listView = (ListView) view.findViewById(R.id.resultList);
        listView.setAdapter(adapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterUsers();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }


    public void filterUsers() {
        matches.clear();

        for (User u : friends) {
            if (u.username.matches(".*" + searchInput.getText().toString() + ".*")) {
                matches.add(u);
            }
        }

        if (matches.isEmpty() && !searchInput.getText().toString().isEmpty()) {
            notFoundLabel.setVisibility(View.VISIBLE);
        } else {
            notFoundLabel.setVisibility(View.GONE);
        }

        if (getContext() != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void friendsChanged(List<User> friends) {
        this.friends.clear();
        this.friends.addAll(friends);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Vyhledavání přátel");
        LoggedUser.addFriendChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        LoggedUser.removeFriendChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showMenuIcon(true);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
    }
}
