package cz.vspj.schrek.im.fragment.meetups.adapter;

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
 * Created by schrek on 29.03.2017.
 */

    public class SelectedFriendAdapter extends ArrayAdapter<User> {

        public SelectedFriendAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> selectedFriend) {
            super(context, resource, selectedFriend);
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
