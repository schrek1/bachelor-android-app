package cz.vspj.schrek.im.fragment.messages.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
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
import cz.vspj.schrek.im.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by schrek on 22.03.2017.
 */

public class MessagesAdapter extends ArrayAdapter<Message> {

    private static List<View> openView = new ArrayList<>();


    public MessagesAdapter(@NonNull Context context, @LayoutRes int layoutItem, @NonNull List<Message> messages) {
        super(context, layoutItem, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.message_list_item, parent, false);

        Message msg = getItem(position);

        TextView messageLabel;
        final TextView timeLabel;
        final TextView readLabel;

        if (msg.from.equals(LoggedUser.getCurrentUser())) {
            messageLabel = (TextView) rowView.findViewById(R.id.own_text);
            timeLabel = (TextView) rowView.findViewById(R.id.own_timestamp);
            readLabel = (TextView) rowView.findViewById(R.id.own_read_status);
            rowView.findViewById(R.id.ownMessage).setVisibility(View.VISIBLE);
        } else {
            messageLabel = (TextView) rowView.findViewById(R.id.friend_text);
            timeLabel = (TextView) rowView.findViewById(R.id.friend_timestamp);
            readLabel = (TextView) rowView.findViewById(R.id.friend_read_status);
            rowView.findViewById(R.id.friendMessage).setVisibility(View.VISIBLE);
        }


        messageLabel.setText(msg.value);


        timeLabel.setText(msg.timestamp + "");
        readLabel.setText((msg.read) ? "Precteno" : "Odeslano");

        messageLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!openView.isEmpty()) {
                    for (View v : openView) {
                        v.setVisibility(View.GONE);
                    }
                }

                if (openView.contains(timeLabel) && openView.contains(readLabel)) {
                    openView.clear();
                } else {
                    openView.clear();
                    timeLabel.setVisibility(View.VISIBLE);
                    openView.add(readLabel);
                    readLabel.setVisibility(View.VISIBLE);
                    openView.add(timeLabel);
                }
            }
        });


        return rowView;
    }
}

