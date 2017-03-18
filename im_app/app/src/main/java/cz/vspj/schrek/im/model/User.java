package cz.vspj.schrek.im.model;

/**
 * Created by schrek on 16.03.2017.
 */

public class User {
    public String uid;
    public String username;

    public User(String uid) {
        this.uid = uid;
    }

    public User(String uid, String username) {
        this.uid = uid;
        this.username = username;
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
        if(user.uid.equals(uid)){
            return true;
        }else{
            return false;
        }
    }
}
