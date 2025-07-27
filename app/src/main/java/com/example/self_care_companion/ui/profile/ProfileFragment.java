package com.example.self_care_companion.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

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

        binding.btnViewInsights.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_profile_to_trends)
        );

        binding.btnNotifications.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.navigation_notification)
        );

        binding.btnHelpSupport.setOnClickListener(v -> {
            String url = "https://cmha.ca/find-help/if-you-are-in-crisis/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        binding.btnSettings.setOnClickListener(v -> {

            requireContext()
                    .getSharedPreferences("selfcare", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Toast.makeText(requireContext(), "Signed out successfully!", Toast.LENGTH_SHORT).show();

            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_splashFragment);
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
