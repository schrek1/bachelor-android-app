package cz.vspj.schrek.im.fragment.messages.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.google.firebase.database.*;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.common.LoggedUser;
import cz.vspj.schrek.im.common.Utils;
import cz.vspj.schrek.im.model.Message;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by schrek on 19.03.2017.
 */

public class ConversationAdapter extends ArrayAdapter<Message> {
    List<Message> messages;

    public ConversationAdapter(@NonNull Context context, @LayoutRes int layoutItem, @NonNull List<Message> messages) {
        super(context, layoutItem, messages);
        this.messages = messages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View rowView = LayoutInflater.from(getContext()).inflate(R.layout.conversation_list_item, parent, false);

        TextView usernameLabel = (TextView) rowView.findViewById(R.id.username_label);
        final TextView timeLabel = (TextView) rowView.findViewById(R.id.time_label);
        final TextView messageLabel = (TextView) rowView.findViewById(R.id.message_label);

        final Message msg = getItem(position);

        usernameLabel.setText(msg.getFriend().getName());

        if (msg.formatedDate == null) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference temp = db.child("temp").child("conversationAdapter").child(LoggedUser.getCurrentUser().uid).push().getRef();
            temp.child("actualTimestamp").setValue(ServerValue.TIMESTAMP);
            temp.child("actualTimestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long messageTimestamp = msg.timestamp;
                    long actualTimestamp = (long) dataSnapshot.getValue() / 1000;

                    Long diffSeconds = (actualTimestamp - messageTimestamp);

                    Date date = new Date(messageTimestamp * 1000);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);

                    String formatedDate;
                    if (diffSeconds < 86_400) {
                        // tento den
                        String timeFormated = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                        timeLabel.setText(timeFormated);
                        msg.formatedDate = timeFormated;
                    } else {
                        // jiny den
                        String timeFormated = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                        formatedDate = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + " " + timeFormated;
                        timeLabel.setText(formatedDate);
                        msg.formatedDate = formatedDate;
                    }

                    temp.removeValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            timeLabel.setText(msg.formatedDate);
        }


        messageLabel.setText(msg.value);

        if (msg.to.equals(LoggedUser.getCurrentUser())) {
            if (msg.read) {
                messageLabel.setTypeface(null, Typeface.NORMAL);
            } else {
                messageLabel.setTypeface(null, Typeface.BOLD);
            }
        }

        return rowView;
    }
}
