package cz.vspj.schrek.im.fragment.messages;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.*;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.activity.MainActivity;
import cz.vspj.schrek.im.common.LoggedUser;
import cz.vspj.schrek.im.fragment.messages.adapter.ConversationAdapter;
import cz.vspj.schrek.im.model.Message;
import cz.vspj.schrek.im.model.User;

import java.util.ArrayList;
import java.util.List;


public class MessageListFragment extends Fragment {
    private DatabaseReference database;

    private List<Message> lastMessages = new ArrayList<>();
    private TextView notFoundLabel;
    private ListView listView;
    private ConversationAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance().getReference();
        adapter = new ConversationAdapter(getContext(), R.layout.user_list_item, lastMessages);

    }

    private void setChangeListener() {
        database.child("app").child("messages").child(LoggedUser.getCurrentUser().uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updateMessageList(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateMessageList(dataSnapshot);
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
        });
    }

    private void updateMessageList(DataSnapshot conversation) {
        User friend = new User(conversation.getKey());
        if (conversation.getChildrenCount() == 1) {
            DataSnapshot message = conversation.getChildren().iterator().next();
            Long timestamp = Long.parseLong(message.getKey());
            Message.Type messageType = Message.Type.getType((String) message.child("type").getValue());
            if (timestamp == Message.INITIAL_TIMPESTAMP && messageType == Message.Type.SYSTEM) {
                //find last message from friend post box
                findInFriendBox(friend, null);
            }
        } else {
            //find last message from both post boxes
            Message ownMsg = findInOwnBox(conversation, friend);
            findInFriendBox(friend, ownMsg);
        }
    }

    private Message findInFriendBox(final User friend, final Message ownMsg) {
        database.child("app").child("messages").child(friend.uid).child(LoggedUser.getCurrentUser().uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot newestNode = getLastMessage(dataSnapshot);
                Message friendMsg = parseMessage(friend, LoggedUser.getCurrentUser(), newestNode);

                if (ownMsg != null) {
                    if (ownMsg.timestamp > friendMsg.timestamp) {
                        putToCoversationList(ownMsg);
                    } else {
                        putToCoversationList(friendMsg);
                    }
                } else {
                    putToCoversationList(friendMsg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return null;
    }

    private void putToCoversationList(Message putMsg) {
        //pokud existuje uprav...
        for (int i = 0; i < lastMessages.size(); i++) {
            if (compareMsgUsers(lastMessages.get(i), putMsg)) {
                lastMessages.set(i, putMsg);
                updateConversationList();
                return;
            }
        }

        // pokud neexistuje vloz...
        for (int i = 0; i < lastMessages.size(); i++) {
            if (lastMessages.get(i).timestamp < putMsg.timestamp) {
                lastMessages.add(i, putMsg);
                updateConversationList();
                return;
            }
        }

        //
        lastMessages.add(putMsg);
        updateConversationList();


    }

    private boolean compareMsgUsers(Message listMsg, Message putMsg) {
        if ((listMsg.from.equals(LoggedUser.getCurrentUser()) && listMsg.to.equals(putMsg.to)) ||
                (listMsg.from.equals(LoggedUser.getCurrentUser()) && listMsg.to.equals(putMsg.from)) ||
                (listMsg.from.equals(putMsg.from) && listMsg.to.equals(LoggedUser.getCurrentUser())) ||
                (listMsg.from.equals(putMsg.to) && listMsg.to.equals(LoggedUser.getCurrentUser()))
                ) {
            return true;
        } else {
            return false;
        }
    }

    private void updateConversationList() {
        if (!lastMessages.isEmpty()) {
            notFoundLabel.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        } else {
            notFoundLabel.setVisibility(View.VISIBLE);
        }
    }

    private Message findInOwnBox(final DataSnapshot conversationNode, final User friend) {
        DataSnapshot newestNode = getLastMessage(conversationNode);
        return parseMessage(LoggedUser.getCurrentUser(), friend, newestNode);
    }

    @NonNull
    private Message parseMessage(User from, User to, DataSnapshot messageNode) {
        long timestamp = Long.parseLong(messageNode.getKey().trim());
        boolean read = ((Boolean) messageNode.child("read").getValue()).booleanValue();
        Message.Type type = Message.Type.getType((String) messageNode.child("type").getValue());
        String value = (String) messageNode.child("value").getValue();

        return new Message(from, to, timestamp, read, type, value);
    }

    private DataSnapshot getLastMessage(DataSnapshot conversationNode) {
        DataSnapshot newestNode = conversationNode.getChildren().iterator().next();
        for (DataSnapshot node : conversationNode.getChildren()) {
            Long newestTimestamp = Long.parseLong(newestNode.getKey().trim());
            Long msgTimestamp = Long.parseLong(node.getKey().trim());

            if (msgTimestamp > newestTimestamp) {
                newestNode = node;
            }
        }
        return newestNode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        notFoundLabel = (TextView) view.findViewById(R.id.notFoundLabel);

        view.findViewById(R.id.sendMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).pushFragment(new FindFriendsFragment());
            }
        });

        listView = (ListView) view.findViewById(R.id.messageList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity) getActivity()).pushFragment(ConversationFragment.newInstance(null));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Zprávy");
        setChangeListener();
    }
}
