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

        // You can still set up button listeners here if you want
        // e.g. binding.btnViewInsights.setOnClickListener(...);
        binding.btnNotifications.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_notification);
        });

        // Help and Support button opens CMHA crisis page
        binding.btnHelpSupport.setOnClickListener(v -> {
            String url = "https://cmha.ca/find-help/if-you-are-in-crisis/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
