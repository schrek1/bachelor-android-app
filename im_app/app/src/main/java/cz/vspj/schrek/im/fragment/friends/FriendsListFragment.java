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
import cz.vspj.schrek.im.fragment.friends.adapter.FriendsAdapter;
import cz.vspj.schrek.im.model.User;

import java.util.List;


public class FriendsListFragment extends Fragment implements LoggedUser.FriendChangeListener {

    private MainActivity mainActivity;

    private TextView emptyLabel;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setTitle("Pratele");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

        view.findViewById(R.id.findFriends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.pushFragment(new FindFriendsFragment());
            }
        });

        listView = (ListView) view.findViewById(R.id.friendList);
        emptyLabel = (TextView) view.findViewById(R.id.notFoundLabel);

        List<User> friends = LoggedUser.getFriends();
        fillUserList(friends);


        return view;
    }

    private void fillUserList(List<User> friends) {
        if (!friends.isEmpty()) {
            emptyLabel.setVisibility(View.GONE);
            final FriendsAdapter adapter = new FriendsAdapter(getContext(), R.layout.friend_list_item, friends, false);
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
        LoggedUser.addFriendChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        LoggedUser.removeFriendChangeListener(this);
    }
}
