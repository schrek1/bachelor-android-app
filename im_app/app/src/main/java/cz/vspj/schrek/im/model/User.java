package cz.vspj.schrek.im.model;

import com.google.firebase.database.*;

import java.io.Serializable;

/**
 * Created by schrek on 16.03.2017.
 */

public class User implements Serializable {
    private static DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public String uid;
    public String username;

    public User(String uid) {
        this.uid = uid;
        obtainUsername(this.uid);
    }

    private void obtainUsername(String uid) {
        database.child("app").child("users").child(uid).child("info").child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public User(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public String getName() {
        return username.split("@")[0];
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        User user = (User) obj;
        if (user.uid.equals(uid)) {
            return true;
        } else {
            return false;
        }
    }
}
