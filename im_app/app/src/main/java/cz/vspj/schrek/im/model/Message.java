package cz.vspj.schrek.im.model;

import cz.vspj.schrek.im.common.LoggedUser;

/**
 * Created by schrek on 18.03.2017.
 */

public class Message {
    public static final long INITIAL_TIMPESTAMP = -1;

    public User from;
    public User to;

    public long timestamp;

    public boolean read;
    public Message.Type type;
    public String value;

    public Message(User from, User to, long timestamp, boolean read, Type type, String value) {
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
        this.read = read;
        this.type = type;
        this.value = value;
    }


    public User getReciever() {
        if (from.equals(LoggedUser.getCurrentUser())) {
            return to;
        } else {
            return from;
        }
    }

    public enum Type {
        TEXT("text"), IMAGE("img"), URL("url"), SYSTEM("system");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public static Type getType(String name) {
            for (Type type : Type.values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            return null;
        }

        public String getName() {
            return this.name;
        }

    }
}
