package cz.vspj.schrek.im.fragment.messages.adapter;

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
import cz.vspj.schrek.im.model.User;

import java.util.List;

/**
 * Created by schrek on 22.03.2017.
 */

public class FriendAdapter extends ArrayAdapter<User> {
    public FriendAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> friends) {
        super(context, resource, friends);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.friend_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.email);
        textView.setText(getItem(position).username);

        return rowView;
    }
}
