package cz.vspj.schrek.im.fragment.messages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.*;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.activity.MainActivity;
import cz.vspj.schrek.im.common.LoggedUser;
import cz.vspj.schrek.im.common.Utils;
import cz.vspj.schrek.im.fragment.messages.adapter.ConversationAdapter;
import cz.vspj.schrek.im.fragment.messages.adapter.MessagesAdapter;
import cz.vspj.schrek.im.model.Message;
import cz.vspj.schrek.im.model.User;

import java.util.ArrayList;
import java.util.List;


public class MessagingFragment extends Fragment {
    private static final String ARG_FRIEND = "friend";

    private DatabaseReference database;

    private User friend;
    private List<Message> messages = new ArrayList<>();

    private TextView notFoundLabel;

    private ListView listView;
    private MessagesAdapter adapter;


    public static MessagingFragment newInstance(User friend) {
        MessagingFragment fragment = new MessagingFragment();
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

        database = FirebaseDatabase.getInstance().getReference();
        adapter = new MessagesAdapter(getContext(), R.layout.message_list_item, messages);

        setChangeListener();

        setHasOptionsMenu(true);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showMenuIcon(false);

        ActionBar actionBar = mainActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View customBar = LayoutInflater.from(getContext()).inflate(R.layout.messaging_toolbar, null);

        actionBar.setCustomView(customBar);
        actionBar.setDisplayShowCustomEnabled(true);

        notFoundLabel = (TextView) view.findViewById(R.id.notFoundLabel);
        listView = (ListView) view.findViewById(R.id.messageList);
        listView.setAdapter(adapter);

        return view;
    }


    private void setChangeListener() {
        database.child("app").child("messages").child(LoggedUser.getCurrentUser().uid).child(friend.uid).addChildEventListener(currentUserListener);
        database.child("app").child("messages").child(friend.uid).child(LoggedUser.getCurrentUser().uid).addChildEventListener(friendListener);
    }

    private void removeMessageListeners() {
        database.child("app").child("messages").child(LoggedUser.getCurrentUser().uid).child(friend.uid).removeEventListener(currentUserListener);
        database.child("app").child("messages").child(friend.uid).child(LoggedUser.getCurrentUser().uid).removeEventListener(friendListener);
    }

    private void updateMessageList(User from, User to, DataSnapshot messageNode) {
        Message.Type msgType = Message.Type.getType((String) messageNode.child("type").getValue());
        if (msgType != Message.Type.SYSTEM) {
            Message message = Utils.parseMessage(from, to, messageNode);

            if (to.equals(LoggedUser.getCurrentUser()) && !message.read) {
                messageNode.child("read").getRef().setValue(true);
            }

            putToMessagesList(message);
        }
    }


    private void putToMessagesList(Message putMsg) {
        //pokud existuje uprav...
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).equals(putMsg)) {
                messages.set(i, putMsg);
                updateListView();
                return;
            }
        }


        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).timestamp > putMsg.timestamp) {
                messages.add(i, putMsg);
                updateListView();
                return;
            }
        }

        messages.add(putMsg);
        updateListView();
    }

    private void updateListView() {
        if (!messages.isEmpty()) {
            notFoundLabel.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        } else {
            notFoundLabel.setVisibility(View.VISIBLE);
        }
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

        removeMessageListeners();
    }


    private ChildEventListener currentUserListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            updateMessageList(friend, LoggedUser.getCurrentUser(), dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            updateMessageList(friend, LoggedUser.getCurrentUser(), dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    private ChildEventListener friendListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            updateMessageList(LoggedUser.getCurrentUser(), friend, dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            updateMessageList(LoggedUser.getCurrentUser(), friend, dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}



