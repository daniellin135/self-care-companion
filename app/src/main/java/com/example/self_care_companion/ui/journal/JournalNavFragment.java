package com.example.self_care_companion.ui.journal;

import static com.example.self_care_companion.MainActivity.databaseHelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.self_care_companion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JournalNavFragment extends Fragment {

    public JournalNavFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal_nav, container, false);

        ImageButton calendarBtn = view.findViewById(R.id.calendarButton);
        calendarBtn.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select a date")
                    .build();

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formattedDate = sdf.format(new Date(selection));

                Bundle bundle = new Bundle();
                bundle.putString("selectedDate", formattedDate);

                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.navigation_journal_calendar, bundle);
            });
        });


        EditText journalInput = view.findViewById(R.id.journalInput);
        Button saveButton = view.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            String entry = journalInput.getText().toString().trim();

            if (entry.isEmpty()) {
                Snackbar.make(view, "Please write something before saving!", Snackbar.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String timestamp = sdf.format(new Date());

            databaseHelper.addJournalEntry(entry, timestamp);
            Snackbar.make(view, "Journal entry saved for today!", Snackbar.LENGTH_SHORT).show();
            journalInput.setText("");
        });

        return view;
    }
}
