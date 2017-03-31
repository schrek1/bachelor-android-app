package cz.vspj.schrek.im.fragment.meetups.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by schrek on 31.03.2017.
 */

public class ChoiceFriendAdapter extends ArrayAdapter<User> {

    private List<User> checked = new ArrayList<>();

    public ChoiceFriendAdapter(@NonNull Context context, @LayoutRes int resource, List<User> friends, List<User> checked) {
        super(context, resource, friends);
        this.checked = new ArrayList<>(checked);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.choice_friend_list_item, parent, false);
        final CheckedTextView ctv = (CheckedTextView) rowView.findViewById(R.id.label);
        ctv.setText(getItem(position).username);
        if (checked.contains(getItem(position))) {
            ctv.setChecked(true);
        }

        ctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ctv.isChecked()) {
                    checked.add(getItem(position));
                } else {
                    checked.remove(getItem(position));
                }
                ctv.setChecked(!ctv.isChecked());
            }
        });

        return rowView;
    }

    public List<User> getCheckedUsers() {
        return checked;
    }
}
