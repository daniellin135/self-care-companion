package com.example.self_care_companion.ui.journal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.self_care_companion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class JournalPromptFragment extends Fragment {

    public JournalPromptFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal_prompt, container, false);

//        ImageButton homeButton = view.findViewById(R.id.homeButton);
//        homeButton.setOnClickListener(v -> {
//            NavController navController = Navigation.findNavController(v);
//            BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
//
//            navController.popBackStack(R.id.navigation_home, false);
//            navView.setSelectedItemId(R.id.navigation_home);  // triggers nav + syncs bottom nav UI
//        });

        Button yesButton = view.findViewById(R.id.yes_btn);
        Button noButton = view.findViewById(R.id.no_btn);

        yesButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_journal_nobutton);
        });

        noButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_home);
        });

        return view;
    }
}