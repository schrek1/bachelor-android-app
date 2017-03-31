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
import cz.vspj.schrek.im.model.Meetup;
import cz.vspj.schrek.im.model.User;

import java.util.List;

/**
 * Created by schrek on 31.03.2017.
 */

public class MeetupAdapter extends ArrayAdapter<Meetup> {

    public MeetupAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Meetup> meetups) {
        super(context, resource, meetups);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.meetup_list_item, parent, false);
        TextView titleLabel = (TextView) rowView.findViewById(R.id.meetup_title);
        titleLabel.setText(getItem(position).title);

        TextView termLabel = (TextView) rowView.findViewById(R.id.meetup_term);
        termLabel.setText(getItem(position).term);

        return rowView;
    }
}
