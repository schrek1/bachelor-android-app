package cz.vspj.schrek.im.fragment.messages;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        adapter = new ConversationAdapter(getContext(), R.layout.friend_list_item, lastMessages);


        fillLastMessagesList();

    }

    private void fillLastMessagesList() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clearConversationList();
                for (DataSnapshot conversation : dataSnapshot.child("app").child("messages").child(LoggedUser.getCurrentUser().uid).getChildren()) {
                    String friendUID = conversation.getKey();
                    if (conversation.getChildrenCount() == 1) {
                        DataSnapshot message = conversation.getChildren().iterator().next();
                        Long timestamp = Long.parseLong(message.getKey());
                        Message.Type messageType = Message.Type.getType((String) message.child("type").getValue());
                        if (timestamp == Message.INITIAL_TIMPESTAMP && messageType == Message.Type.SYSTEM) {
                            //find last message from friend post box
                            addLastFromPostBoxes(dataSnapshot, friendUID, true);
                        }
                    } else {
                        //find last message from both post boxes
                        addLastFromPostBoxes(dataSnapshot, friendUID, false);
                    }
                }


                database.child("app").child("messages").child(LoggedUser.getCurrentUser().uid).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        fillLastMessagesList();
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void clearConversationList() {
        lastMessages.clear();
        adapter.notifyDataSetChanged();
    }

    private void insertToCoversationList(Message msg) {
        for (int i = 0; i < lastMessages.size(); i++) {
            if (lastMessages.get(i).timestamp < msg.timestamp) {
                lastMessages.add(i, msg);
                updateConversationList();
                return;
            }
        }

        lastMessages.add(msg);
        updateConversationList();

    }

    private void updateConversationList() {
        if (!lastMessages.isEmpty()) {
            notFoundLabel.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        } else {
            notFoundLabel.setVisibility(View.VISIBLE);
        }
    }

    private void findAtOwnBox(final DataSnapshot rootNode, final String friendUID, final Message friendMsg) {
        rootNode.child("app").child("messages").child(LoggedUser.getCurrentUser().uid).child(friendUID)
                .getRef().orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String friendUsername = (String) rootNode.child("app").child("users").child(friendUID).child("info").child("username").getValue();
                User from = new User(friendUID, friendUsername);
                User to = LoggedUser.getCurrentUser();

                DataSnapshot lastMsg = dataSnapshot.getChildren().iterator().next();

                long timestamp = Long.parseLong(lastMsg.getKey().trim());
                boolean read = ((Boolean) lastMsg.child("read").getValue()).booleanValue();
                Message.Type type = Message.Type.getType((String) lastMsg.child("type").getValue());
                String value = (String) lastMsg.child("value").getValue();

                Message ownMsg = new Message(from, to, timestamp, read, type, value);

                if (friendMsg.timestamp > ownMsg.timestamp) {
                    insertToCoversationList(friendMsg);
                } else {
                    insertToCoversationList(ownMsg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void addLastFromPostBoxes(final DataSnapshot rootNode, final String friendUID, final boolean initialMessage) {
        rootNode.child("app").child("messages").child(friendUID).child(LoggedUser.getCurrentUser().uid)
                .getRef().orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Message friendMsg = findAtFriendBox(dataSnapshot);

                if (initialMessage) {
                    insertToCoversationList(friendMsg);
                } else {
                    findAtOwnBox(rootNode, friendUID, friendMsg);
                }
            }

            @NonNull
            private Message findAtFriendBox(DataSnapshot dataSnapshot) {
                String friendUsername = (String) rootNode.child("app").child("users").child(friendUID).child("info").child("username").getValue();
                User from = LoggedUser.getCurrentUser();
                User to = new User(friendUID, friendUsername);

                DataSnapshot lastMsg = dataSnapshot.getChildren().iterator().next();

                long timestamp = Long.parseLong(lastMsg.getKey().trim());
                boolean read = ((Boolean) lastMsg.child("read").getValue()).booleanValue();
                Message.Type type = Message.Type.getType((String) lastMsg.child("type").getValue());
                String value = (String) lastMsg.child("value").getValue();

                return new Message(from, to, timestamp, read, type, value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        notFoundLabel = (TextView) view.findViewById(R.id.notFoundLabel);

        listView = (ListView) view.findViewById(R.id.messageList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Zprávy");
    }
}
