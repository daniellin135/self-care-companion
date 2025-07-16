package com.example.self_care_companion.ui.habits;

import static com.example.self_care_companion.MainActivity.databaseHelper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.self_care_companion.R;
import com.example.self_care_companion.databinding.FragmentHabitsBinding;

import java.util.Set;

public class HabitsFragment extends Fragment {

    private FragmentHabitsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HabitsViewModel habitsViewModel =
                new ViewModelProvider(this).get(HabitsViewModel.class);

        binding = FragmentHabitsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHabits;
        habitsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Set<String> habits = databaseHelper.getUniqueHabits();
        boolean hasWaterIntake = habits.stream().anyMatch(h -> h.startsWith("Water Intake|"));
        boolean hasExercise = habits.stream().anyMatch(h -> h.startsWith("Exercise|"));
        boolean hasSleep = habits.stream().anyMatch(h -> h.startsWith("Sleep|"));

        if (!hasWaterIntake) {
            habits.add("Water Intake|cups|0.0|8");
        }
        if (!hasExercise) {
            habits.add("Exercise|hrs|0.0|0.5");
        }
        if (!hasSleep) {
            habits.add("Sleep|hrs|0.0|8");
        }

        LinearLayout habitContainer = root.findViewById(R.id.habitContainer);

        for (String habit : habits) {
            String[] habitParts = habit.split("\\|");

            if (habitParts.length < 4) continue;

            String labelValue = habitParts[0].trim();
            String unitValue = habitParts[1].trim();
            double currentValue = Double.parseDouble(habitParts[2].trim());
            double goalValue = Double.parseDouble(habitParts[3].trim());

            addHabitRow(inflater, habitContainer, labelValue, unitValue, currentValue, goalValue);
        }

        binding.buttonAddHabit.setOnClickListener(v -> {
            Context context = getContext();
            if (context == null) return;

            View dialogView = inflater.inflate(R.layout.add_habit_popup, null);
            EditText habitNameInput = dialogView.findViewById(R.id.editHabitName);
            EditText habitUnitsInput = dialogView.findViewById(R.id.editHabitUnits);
            EditText habitGoalInput = dialogView.findViewById(R.id.editHabitGoal);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Add Custom Habit");
            builder.setView(dialogView);

            builder.setPositiveButton("Add", (dialog, which) -> {
                String habitName = habitNameInput.getText().toString().trim();
                String habitUnits = habitUnitsInput.getText().toString().trim();
                String habitGoal = habitGoalInput.getText().toString().trim();

                if (habitName.isEmpty() || habitGoal.isEmpty() || habitUnits.isEmpty()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    double habitGoalDouble = Double.parseDouble(habitGoal);

                    databaseHelper.addHabit(habitName, 0.0, habitUnits, habitGoalDouble);
                    addHabitRow(inflater, habitContainer, habitName, habitUnits, 0.0, habitGoalDouble);

                    Toast.makeText(context, "Habit added: " + habitName + " (" + habitUnits + ")", Toast.LENGTH_LONG).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        binding.buttonSaveHabits.setOnClickListener(v -> {
            for (int i = 0; i < habitContainer.getChildCount(); i++) {
                View habitRow = habitContainer.getChildAt(i);
                TextView label = habitRow.findViewById(R.id.label_habit);
                TextView unit = habitRow.findViewById(R.id.unit_habit);
                EditText valueInput = habitRow.findViewById(R.id.input_habit);

                String labelValue = label.getText().toString();
                String unitValue = unit.getText().toString();

                if (unitValue.startsWith("/")) {
                    unitValue = unitValue.substring(1);
                }

                int spaceIndex = unitValue.indexOf(' ');
                String goalPart = unitValue.substring(0, spaceIndex);
                String unitString = unitValue.substring(spaceIndex + 1);
                double goal = Double.parseDouble(goalPart);

                String valueText = valueInput.getText().toString();

                if (!valueText.isEmpty()) {
                    double value = Double.parseDouble(valueText);
                    databaseHelper.addHabit(labelValue, value, unitString, goal);
                }
            }
        });

        return root;
    }

    private void addHabitRow(LayoutInflater inflater, LinearLayout habitContainer,
                             String labelValue, String unitValue, double currentValue, double goalValue) {
        View habitRow = inflater.inflate(R.layout.item_habit_row, habitContainer, false);

        TextView label = habitRow.findViewById(R.id.label_habit);
        EditText value = habitRow.findViewById(R.id.input_habit);
        TextView unit = habitRow.findViewById(R.id.unit_habit);

        label.setText(labelValue);
        value.setText(String.valueOf(currentValue));
        unit.setText("/" + goalValue + " " + unitValue);

        Button incrementButton = habitRow.findViewById(R.id.plus_button);
        incrementButton.setOnClickListener(v -> {
            Context context = getContext();
            if (context == null) return;

            View dialogView = inflater.inflate(R.layout.increment_habit_popup, null);
            EditText incrementInput = dialogView.findViewById(R.id.incrementHabitInput);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Increment Habit");
            builder.setView(dialogView);

            builder.setPositiveButton("Increment", (dialog, which) -> {
                String incrementText = incrementInput.getText().toString().trim();

                if (incrementText.isEmpty()) {
                    Toast.makeText(context, "Please enter a value to increment", Toast.LENGTH_SHORT).show();
                } else {
                    double incrementValue = Double.parseDouble(incrementText);
                    double currentHabitValue = Double.parseDouble(value.getText().toString());
                    double newHabitValue = currentHabitValue + incrementValue;
                    value.setText(String.valueOf(newHabitValue));
                    databaseHelper.addHabit(labelValue, newHabitValue, unitValue, goalValue);
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        habitContainer.addView(habitRow);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
