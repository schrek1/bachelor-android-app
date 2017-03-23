package cz.vspj.schrek.im.common;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import cz.vspj.schrek.im.model.Message;
import cz.vspj.schrek.im.model.User;

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
}
