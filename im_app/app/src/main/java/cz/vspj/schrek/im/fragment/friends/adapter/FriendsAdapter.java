package cz.vspj.schrek.im.fragment.friends.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.common.LoggedUser;
import cz.vspj.schrek.im.common.Utils;
import cz.vspj.schrek.im.model.User;

import java.util.List;

/**
 * Created by schrek on 16.03.2017.
 */

public class FriendsAdapter extends ArrayAdapter<User> {
    private final Context context;
    private DatabaseReference databse;
    private User loggedUser;

    private boolean addingMode;


    public FriendsAdapter(Context context, int layoutItem, List<User> users, boolean addingMode) {
        super(context, layoutItem, users);
        this.context = context;
        this.addingMode = addingMode;
        this.databse = FirebaseDatabase.getInstance().getReference();
        this.loggedUser = LoggedUser.getCurrentUser();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.friend_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.email);
        textView.setText(getItem(position).username);

        Button button;
        if (addingMode) {
            button = (Button) rowView.findViewById(R.id.addFriend);
            button.setOnClickListener(addListener(position));
        } else {
            button = (Button) rowView.findViewById(R.id.removeFriend);
            button.setOnClickListener(removeListener(position));
        }
        button.setVisibility(View.VISIBLE);

        button.setOnTouchListener(Utils.getViewClickEffect());

        return rowView;
    }

    private View.OnClickListener removeListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User friend = getItem(position);
                databse.child("app").child("users").child(loggedUser.uid).child("friends").child(friend.uid).removeValue();
            }
        };
    }

    private View.OnClickListener addListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User friend = getItem(position);
                databse.child("app").child("users").child(loggedUser.uid).child("friends").child(friend.uid).setValue(friend.username);
            }
        };
    }

}