package cz.vspj.schrek.im.common;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import cz.vspj.schrek.im.model.Message;
import cz.vspj.schrek.im.model.User;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by schrek on 10.03.2017.
 */

public class Utils {

    public static View.OnTouchListener getViewClickEffect() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        view.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return false;
            }
        };
    }


    @NonNull
    public static Message parseMessage(User from, User to, DataSnapshot messageNode) {
        long timestamp = Long.parseLong(messageNode.getKey().trim());
        boolean read;
        if (messageNode.child("read").getValue() instanceof Boolean) {
            read = ((Boolean) messageNode.child("read").getValue()).booleanValue();
        } else {
            read = Boolean.parseBoolean((String) messageNode.child("read").getValue());
        }
        Message.Type type = Message.Type.getType((String) messageNode.child("type").getValue());
        String value = (String) messageNode.child("value").getValue();

        return new Message(from, to, timestamp, read, type, value);
    }


    public static void setUserOnlineState(String userUID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference statusRef = db.child("app").child("users").child(userUID).child("info").child("status").getRef();

        HashMap<String, Object> map = new HashMap<>();
        map.put("lastUpdate", ServerValue.TIMESTAMP);
        map.put("state", "online");

        statusRef.setValue(map);
    }

    public static void setUserOfflineState(String userUID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference statusRef = db.child("app").child("users").child(userUID).child("info").child("status").getRef();

        HashMap<String, Object> map = new HashMap<>();
        map.put("lastUpdate", ServerValue.TIMESTAMP);
        map.put("state", "offline");

        statusRef.setValue(map);
    }

    public static String lastActive(long lastTimestamp, long actualTimestamp) {
        long diffSeconds = (actualTimestamp - lastTimestamp) / 1000;

        if (diffSeconds < 60) {
            //mene nez minuta
            return "malou chvílí";
        } else if (diffSeconds < 3600) {
            //mene nez hodina
            Long minutes = diffSeconds / 60;
            return minutes + " minutami";
        } else if (diffSeconds < 86_400) {
            //mene nez den
            Long hours = diffSeconds / 3600;
            return hours + " hodinami";
        } else if (diffSeconds < 604_800) {
            //mene nez tyden
            Long days = diffSeconds / (3600 * 24);
            return days + " dny";
        } else {
            //vice nez 7 dni
            return "7+ dny";
        }
    }

}
