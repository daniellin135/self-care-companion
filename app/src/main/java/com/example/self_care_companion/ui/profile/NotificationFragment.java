package com.example.self_care_companion.ui.profile;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import com.example.self_care_companion.Notifications.NotificationPreferences;
import com.example.self_care_companion.R;

public class NotificationFragment extends Fragment {

    private NotificationPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        prefs = new NotificationPreferences(requireContext());

        Switch morningSwitch = view.findViewById(R.id.morning_switch);
        Switch middaySwitch = view.findViewById(R.id.midday_switch);
        Switch eveningSwitch = view.findViewById(R.id.evening_switch);

        // set current state
        morningSwitch.setChecked(prefs.isMorningEnabled());
        middaySwitch.setChecked(prefs.isMiddayEnabled());
        eveningSwitch.setChecked(prefs.isEveningEnabled());

        // save on change
        morningSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.setMorningEnabled(isChecked));
        middaySwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.setMiddayEnabled(isChecked));
        eveningSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.setEveningEnabled(isChecked));

        return view;
    }
}