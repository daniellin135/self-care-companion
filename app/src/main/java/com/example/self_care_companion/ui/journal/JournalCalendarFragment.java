package com.example.self_care_companion.ui.journal;

import static com.example.self_care_companion.MainActivity.databaseHelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.self_care_companion.R;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class JournalCalendarFragment extends Fragment {

    public JournalCalendarFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Past Entries");
        }

        Button openDatePicker = view.findViewById(R.id.openDatePicker);
        LinearLayout entryContainer = view.findViewById(R.id.entryContainer);
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        Runnable loadEntries = () -> {
            String selectedDate = (String) openDatePicker.getTag();

            List<String> entries = databaseHelper.getJournalEntriesForDate(selectedDate);
            entryContainer.removeAllViews();

            if (entries.isEmpty()) {
                TextView noEntry = new TextView(requireContext());
                noEntry.setText("No entries for " + selectedDate);
                noEntry.setTextSize(16);
                entryContainer.addView(noEntry);
            } else {
                for (String entry : entries) {
                    View cardView = inflater.inflate(R.layout.item_journal_entry, entryContainer, false);
                    TextView entryText = cardView.findViewById(R.id.journalEntryText);
                    entryText.setText(entry);
                    entryContainer.addView(cardView);
                }
            }
        };

        String initialDate = getArguments() != null ? getArguments().getString("selectedDate") : null;
        if (initialDate != null) {
            openDatePicker.setTag(initialDate); // store it
            loadEntries.run();
        }

        openDatePicker.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select a date")
                    .build();

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formattedDate = sdf.format(new Date(selection));

                openDatePicker.setTag(formattedDate);
                loadEntries.run();
            });
        });
    }


}
