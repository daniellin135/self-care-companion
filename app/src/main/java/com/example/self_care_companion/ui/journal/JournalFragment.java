package com.example.self_care_companion.ui.journal;

import static com.example.self_care_companion.MainActivity.databaseHelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.self_care_companion.R;
import com.example.self_care_companion.databinding.FragmentJournalBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JournalFragment extends Fragment {

    private FragmentJournalBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        JournalViewModel journalViewModel =
                new ViewModelProvider(this).get(JournalViewModel.class);

        binding = FragmentJournalBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);

        binding.saveButton.setOnClickListener(v -> {
            String journalEntry = binding.journalInput.getText().toString().trim();

            if (!journalEntry.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                String timestamp = sdf.format(new Date());  // local time

                databaseHelper.addJournalEntry(journalEntry, timestamp);
                Snackbar.make(root, "Journal entry saved for today!", Snackbar.LENGTH_SHORT).show();
                binding.journalInput.setText("");
            } else {
                Snackbar.make(root, "Please write something before saving!", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.calendarButton.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select a date")
                    .build();

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String formattedDate = sdf.format(new Date(selection));

                Bundle bundle = new Bundle();
                bundle.putString("selectedDate", formattedDate);

                navController.navigate(R.id.navigation_journal_calendar, bundle);
            });
        });

        binding.backButton.setOnClickListener(v -> {
            navController.navigate(R.id.navigation_mood);
        });

        binding.nextButton.setOnClickListener(v -> {
            navController.navigate(R.id.navigation_insights);
        });

        return root;
    }
}
