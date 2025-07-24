package com.example.self_care_companion.ui.journal;

import static com.example.self_care_companion.MainActivity.databaseHelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.self_care_companion.R;
import com.example.self_care_companion.databinding.FragmentJournalBinding;
import com.google.android.material.snackbar.Snackbar;

public class JournalFragment extends Fragment {

    private FragmentJournalBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        JournalViewModel journalViewModel =
                new ViewModelProvider(this).get(JournalViewModel.class);

        binding = FragmentJournalBinding.inflate(inflater, container, false);

        binding.saveButton.setOnClickListener(v -> {
            String journalEntry = binding.journalInput.getText().toString();

            if (!journalEntry.isEmpty()) {
                databaseHelper.addJournalEntry(journalEntry);
                Snackbar.make(binding.getRoot(), "Your journal entry has been saved!", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(binding.getRoot(), "Please write something before saving!", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.backButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_mood);
        });

        binding.nextButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_insights);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
