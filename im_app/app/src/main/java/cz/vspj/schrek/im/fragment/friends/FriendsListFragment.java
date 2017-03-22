package cz.vspj.schrek.im.fragment.friends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.activity.MainActivity;
import cz.vspj.schrek.im.common.LoggedUser;
import cz.vspj.schrek.im.common.Utils;
import cz.vspj.schrek.im.fragment.friends.adapter.UsersAdapter;
import cz.vspj.schrek.im.model.User;

import java.util.List;


public class FriendsListFragment extends Fragment implements LoggedUser.FriendChangeListener {

    private TextView emptyLabel;

    private ListView listView;
    private UsersAdapter adapter;
    private List<User> friends;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

        view.findViewById(R.id.findFriends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).pushFragment(new FindUsersFragment());
            }
        });

        view.findViewById(R.id.findFriends).setOnTouchListener(Utils.getViewClickEffect());

        emptyLabel = (TextView) view.findViewById(R.id.notFoundLabel);
        listView = (ListView) view.findViewById(R.id.friendList);


        fillUserList(LoggedUser.getFriends());


        return view;
    }

    private void fillUserList(List<User> friends) {
        if (!friends.isEmpty()) {
            emptyLabel.setVisibility(View.GONE);
            this.friends = friends;
            adapter = new UsersAdapter(getContext(), R.layout.user_list_item, friends, false);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            emptyLabel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void friendsChanged(List<User> friends) {
        fillUserList(friends);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Pratele");
        LoggedUser.addFriendChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        LoggedUser.removeFriendChangeListener(this);
    }
}
