package cz.vspj.schrek.im.fragment.messages;

import android.os.Bundle;
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
import cz.vspj.schrek.im.common.Utils;
import cz.vspj.schrek.im.fragment.messages.adapter.ConversationAdapter;
import cz.vspj.schrek.im.model.Message;
import cz.vspj.schrek.im.model.User;

import java.util.ArrayList;
import java.util.List;


public class ConversationListFragment extends Fragment {
    private DatabaseReference database;

    private List<Message> lastMessages = new ArrayList<>();
    private TextView notFoundLabel;
    private ListView listView;
    private ConversationAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance().getReference();
        adapter = new ConversationAdapter(getContext(), R.layout.conversation_list_item, lastMessages);

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


    private void updateMessageList(final DataSnapshot conversation) {
        final String friendUid = conversation.getKey();
        database.child("app").child("users").child(friendUid).child("info").child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = (String) dataSnapshot.getValue();
                User friend = new User(friendUid, username);
                if (conversation.getChildrenCount() == 1) {
                    DataSnapshot message = conversation.getChildren().iterator().next();
                    Long timestamp = Long.parseLong(message.getKey());
                    Message.Type messageType = Message.Type.getType((String) message.child("type").getValue());
                    if (timestamp == Message.INITIAL_TIMPESTAMP && messageType == Message.Type.SYSTEM) {
                        //find last message from friend post box
                        findInFriendBox(friend, null);
                    } else {
                        Message toCurrentUserMsg = findInOwnBox(conversation, friend);
                        putToCoversationList(toCurrentUserMsg);
                    }
                } else {
                    //find last message from both post boxes
                    Message toCurrentUserMsg = findInOwnBox(conversation, friend);
                    findInFriendBox(friend, toCurrentUserMsg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private Message findInFriendBox(final User friend, final Message toCurrentUserMsg) {
        database.child("app").child("messages").child(friend.uid).child(LoggedUser.getCurrentUser().uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot newestNode = getLastMessage(dataSnapshot);
                Message toFriendMsg = Utils.parseMessage(LoggedUser.getCurrentUser(), friend, newestNode);

                if (toCurrentUserMsg != null) {
                    if (toCurrentUserMsg.timestamp > toFriendMsg.timestamp) {
                        putToCoversationList(toCurrentUserMsg);
                    } else {
                        putToCoversationList(toFriendMsg);
                    }
                } else {
                    putToCoversationList(toFriendMsg);
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
                //pro zmeny pri odeslani zpravy
                updateConversationList();
                return;
            }
        }

        //
        lastMessages.add(putMsg);
        updateConversationList();


    }

    private boolean compareMsgUsers(Message listMsg, Message putMsg) {
        return listMsg.getFriend().equals(putMsg.getFriend());
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
        return Utils.parseMessage(friend, LoggedUser.getCurrentUser(), newestNode);
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
        final View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);

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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Message msg = adapter.getItem(position);
                User friend = msg.getFriend();
                ((MainActivity) getActivity()).pushFragment(MessagingFragment.newInstance(friend));
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
