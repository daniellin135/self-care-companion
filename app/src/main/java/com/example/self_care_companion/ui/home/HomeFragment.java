package com.example.self_care_companion.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.self_care_companion.databinding.FragmentHomeBinding;
import com.example.self_care_companion.ui.home.HomeViewModel;
import com.example.self_care_companion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String firstName = requireContext()
                .getSharedPreferences("selfcare", Context.MODE_PRIVATE)
                .getString("user_name", "");

        if (!firstName.isEmpty()) {
            binding.greetingText.setText("Hello, " + firstName + "!");
        }

        binding.journalButton.setOnClickListener(v -> {
            BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
            navView.setSelectedItemId(R.id.navigation_journal_nobutton);
        });

        binding.habitsButton.setOnClickListener(v -> {
            BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
            navView.setSelectedItemId(R.id.navigation_habits);
        });

        binding.checkinButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_mood);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
