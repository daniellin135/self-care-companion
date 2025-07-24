package com.example.self_care_companion.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.self_care_companion.R;
import com.example.self_care_companion.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Button listeners
        binding.btnViewInsights.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_profile_to_trends)
        );

        binding.btnNotifications.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.navigation_notification)
        );

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
