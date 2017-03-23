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
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.common.LoggedUser;
import cz.vspj.schrek.im.common.Utils;
import cz.vspj.schrek.im.model.Message;

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
        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.conversation_list_item, parent, false);

        TextView usernameLabel = (TextView) rowView.findViewById(R.id.username_label);
        TextView timeLabel = (TextView) rowView.findViewById(R.id.time_label);
        TextView messageLabel = (TextView) rowView.findViewById(R.id.message_label);

        Message msg = getItem(position);

        usernameLabel.setText(msg.getFriend().getName());
        timeLabel.setText(msg.timestamp + "");
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
