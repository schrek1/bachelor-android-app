package cz.vspj.schrek.im.common;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

}
