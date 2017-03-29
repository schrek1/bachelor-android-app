package cz.vspj.schrek.im.fragment.meetups;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.activity.MainActivity;
import cz.vspj.schrek.im.common.Utils;

/**
 * Created by schrek on 29.03.2017.
 */

public class MeetupsListFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_meetups_list, container, false);

        final MainActivity mainActivity = (MainActivity) getActivity();

        View addButton = view.findViewById(R.id.addMeetup);
        addButton.setOnTouchListener(Utils.getViewClickEffect());
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.pushFragment(new MeetupAddFragment());
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Schůzky");
    }
}
