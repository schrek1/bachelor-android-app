package cz.vspj.schrek.im.fragment.friends;

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
import android.widget.Toast;
import com.google.firebase.database.*;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.activity.MainActivity;
import cz.vspj.schrek.im.common.LoggedUser;
import cz.vspj.schrek.im.fragment.friends.adapter.UsersAdapter;
import cz.vspj.schrek.im.model.User;

import java.util.ArrayList;
import java.util.List;


public class FindUsersFragment extends Fragment implements LoggedUser.FriendChangeListener {

    private DatabaseReference database;

    private List<User> users;

    private TextView notFoundLabel;
    private EditText searchInput;

    private ListView listView;
    private UsersAdapter adapter;
    private List<User> matches = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.database = FirebaseDatabase.getInstance().getReference();

        adapter = new UsersAdapter(getContext(), R.layout.user_list_item, matches, true);

        fillUserWithoutFirends(LoggedUser.getCurrentUser(), LoggedUser.getFriends());
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_find_users, container, false);

        searchInput = (EditText) view.findViewById(R.id.searchInput);
        notFoundLabel = (TextView) view.findViewById(R.id.notFoundLabel);

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
        if (users != null && !users.isEmpty() && !searchInput.getText().toString().isEmpty()) {
            for (User u : users) {
                if (u.username.matches(".*" + searchInput.getText().toString() + ".*")) {
                    matches.add(u);
                }
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
        fillUserWithoutFirends(LoggedUser.getCurrentUser(), friends);
    }


    public void fillUserWithoutFirends(final User user, final List<User> friends) {
        final List<User> filtered = new ArrayList<>();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.child("app").child("users").getChildren()) {
                    if (!item.getKey().equals(user.uid)) {
                        User user = new User(item.getKey(), (String) item.child("info").child("username").getValue());
                        if (!friends.contains(user)) {
                            filtered.add(user);
                        }
                    }
                }
                users = filtered;
                filterUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Nepodarila se komunikace se serverem", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setCustomToolbar(){
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showMenuIcon(false);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Vyhledavání uživatelů");
        LoggedUser.addFriendChangeListener(this);
        setCustomToolbar();
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
