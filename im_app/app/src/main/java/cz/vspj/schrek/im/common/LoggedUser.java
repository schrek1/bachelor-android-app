package cz.vspj.schrek.im.common;

import com.google.firebase.database.*;
import cz.vspj.schrek.im.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by schrek on 16.03.2017.
 */

public class LoggedUser {

    private static DatabaseReference database;

    private static User loggedUser;
    private static List<User> friends = new ArrayList<>();
    private static ChildEventListener databaseFriendsListener;

    private static List<FriendChangeListener> friendChangeListeners;

    static {
        database = FirebaseDatabase.getInstance().getReference();
        friends = new ArrayList<>();
        friendChangeListeners = new ArrayList<>();
    }

    public static User getCurrentUser() {
        return loggedUser;
    }

    public static void setCurrentUser(User user) {
        loggedUser = user;
        updateFriendList(user);
        setFirendsChangeListener(user);
    }

    public static List<User> getFriends() {
        return friends;
    }

    private static void notifyFriendsChangeSubscribers() {
        for (FriendChangeListener subscriber : friendChangeListeners) {
            subscriber.friendsChanged(getFriends());
        }
    }

    private static void updateFriendList(final User user) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friends = new ArrayList<>();
                for (DataSnapshot friend : dataSnapshot.child("app").child("users").child(user.uid).child("friends").getChildren()) {
                    if (!friend.getKey().equals(loggedUser.uid)) {
                        friends.add(new User(friend.getKey(), (String) friend.getValue()));
                    }
                }
                notifyFriendsChangeSubscribers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private static void setFirendsChangeListener(final User user) {
        if (databaseFriendsListener != null) {
            database.child("app").child("users").child(user.uid).child("friends").removeEventListener(databaseFriendsListener);
        }

        databaseFriendsListener = new ChildEventListener() {

            private void addFriend(DataSnapshot addObject) {
                User user = new User(addObject.getKey(), (String) addObject.getValue());
                if (!user.uid.equals(loggedUser.uid)) {
                    if (!friends.contains(user)) {
                        friends.add(user);
                    }
                }
                notifyFriendsChangeSubscribers();
            }

            private void removeFriend(DataSnapshot removedObject) {
                User user = new User(removedObject.getKey(), (String) removedObject.getValue());
                if (!user.uid.equals(loggedUser.uid)) {
                    friends.remove(user);
                }
                notifyFriendsChangeSubscribers();
            }

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addFriend(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeFriend(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        }

        ;

        database.child("app").

                child("users").

                child(user.uid).

                child("friends").

                addChildEventListener(databaseFriendsListener);
    }

    public interface FriendChangeListener {
        public void friendsChanged(List<User> friends);

    }

    public static void addFriendChangeListener(FriendChangeListener listener) {
        friendChangeListeners.add(listener);
    }

    public static void removeFriendChangeListener(FriendChangeListener listener) {
        friendChangeListeners.remove(listener);
    }
}
