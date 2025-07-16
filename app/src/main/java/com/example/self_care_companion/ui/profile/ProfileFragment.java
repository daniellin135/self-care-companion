package com.example.self_care_companion.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.self_care_companion.databinding.FragmentProfileBinding;
import com.example.self_care_companion.R;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // You can still set up button listeners here if you want
        // e.g. binding.btnViewInsights.setOnClickListener(...);
        binding.btnViewInsights.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profile_to_trends);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
