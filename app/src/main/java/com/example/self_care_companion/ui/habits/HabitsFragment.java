package com.example.self_care_companion.ui.habits;

import static com.example.self_care_companion.MainActivity.databaseHelper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.material.snackbar.Snackbar;

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
        habitContainer.removeAllViews();

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
                    String habitString = habitName + "|" + habitUnits + "|0.0|" + habitGoalDouble;
                    habits.add(habitString);

                    Toast.makeText(context, "Habit added: " + habitName + " (" + habitUnits + ")", Toast.LENGTH_LONG).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        binding.buttonEditHabit.setOnClickListener(v -> {
            Context context = getContext();
            if (context == null) return;

            View dialogView = inflater.inflate(R.layout.modify_habit_list, null);
            LinearLayout habitListContainer = dialogView.findViewById(R.id.modify_habit_list_container);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Modify Habits");
            builder.setMessage("Select a habit to edit or delete");
            builder.setView(dialogView);

            builder.setPositiveButton("Done", (dialog, which) -> dialog.dismiss());
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            if (habits.size() < 4) {
                Toast.makeText(context, "No custom habits to modify", Toast.LENGTH_SHORT).show();
                return;
            }

            for (String habit: habits) {
                String[] habitParts = habit.split("\\|");

                if (habitParts.length < 4) continue;

                String labelValue = habitParts[0].trim();

                if (labelValue.equals("Water Intake") || labelValue.equals("Exercise") || labelValue.equals("Sleep")) {
                    continue;
                }

                String unitValue = habitParts[1].trim();
                double currentValue = Double.parseDouble(habitParts[2].trim());
                double goalValue = Double.parseDouble(habitParts[3].trim());

                View habitRow = inflater.inflate(R.layout.modify_habit_row, habitListContainer, false);
                TextView label = habitRow.findViewById(R.id.modify_habit_name);
                ImageButton editButton = habitRow.findViewById(R.id.modify_edit_button);
                ImageButton deleteButton = habitRow.findViewById(R.id.modify_delete_button);

                String labelOption = labelValue + " - / " + goalValue + " " + unitValue;

                label.setText(labelOption);

                editButton.setOnClickListener(v1 -> {
                    View editDialogView = inflater.inflate(R.layout.add_habit_popup, null);
                    EditText habitNameInput = editDialogView.findViewById(R.id.editHabitName);
                    EditText habitUnitsInput = editDialogView.findViewById(R.id.editHabitUnits);
                    EditText habitGoalInput = editDialogView.findViewById(R.id.editHabitGoal);

                    habitNameInput.setText(labelValue);
                    habitUnitsInput.setText(unitValue);
                    habitGoalInput.setText(String.valueOf(goalValue));

                    AlertDialog.Builder editBuilder = new AlertDialog.Builder(context);
                    editBuilder.setTitle("Edit Habit");
                    editBuilder.setView(editDialogView);

                    editBuilder.setPositiveButton("Save", (dialog, which) -> {
                        String newHabitName = habitNameInput.getText().toString().trim();
                        String newHabitUnits = habitUnitsInput.getText().toString().trim();
                        String newHabitGoal = habitGoalInput.getText().toString().trim();

                        if (newHabitName.isEmpty() || newHabitGoal.isEmpty() || newHabitUnits.isEmpty()) {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        } else {
                            double newHabitGoalDouble = Double.parseDouble(newHabitGoal);
                            databaseHelper.updateHabitsByLabel(labelValue, newHabitName, newHabitUnits, newHabitGoalDouble);
                            String labelString = newHabitName + " - / " + newHabitGoalDouble + " " + newHabitUnits;
                            label.setText(labelString);
                            String updatedHabitString = newHabitName + "|" + newHabitUnits + "|" + currentValue + "|" + newHabitGoalDouble;
                            habits.remove(habit);
                            habits.add(updatedHabitString);

                            for (int i = 0; i < habitContainer.getChildCount(); i++) {
                                View habitRowInContainer = habitContainer.getChildAt(i);
                                TextView labelInContainer = habitRowInContainer.findViewById(R.id.label_habit);
                                TextView unitInContainer = habitRowInContainer.findViewById(R.id.unit_habit);
                                if (labelInContainer.getText().toString().equals(labelValue)) {
                                    labelInContainer.setText(newHabitName);
                                    String unitString = "/" + newHabitGoalDouble + " " + newHabitUnits;
                                    unitInContainer.setText(unitString);
                                    break;
                                }
                            }
                            Toast.makeText(context, "Habit updated: " + newHabitName + " (" + newHabitUnits + ")", Toast.LENGTH_LONG).show();
                        }
                    });

                    editBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                    editBuilder.create().show();
                });

                deleteButton.setOnClickListener(v1 -> {
                    AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(context);
                    deleteBuilder.setTitle("Delete Habit");
                    deleteBuilder.setMessage("Are you sure you want to delete this habit?");
                    deleteBuilder.setPositiveButton("Delete", (dialog, which) -> {
                        databaseHelper.deleteHabitsByLabel(labelValue);
                        habitListContainer.removeView(habitRow);
                        habits.remove(habit);

                        for (int i = 0; i < habitContainer.getChildCount(); i++) {
                            View habitRowInContainer = habitContainer.getChildAt(i);
                            TextView labelInContainer = habitRowInContainer.findViewById(R.id.label_habit);
                            if (labelInContainer.getText().toString().equals(labelValue)) {
                                habitContainer.removeView(habitRowInContainer);
                                break;
                            }
                        }
                        Toast.makeText(context, "Habit deleted: " + labelValue, Toast.LENGTH_SHORT).show();
                    });
                    deleteBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                    deleteBuilder.create().show();
                });

                habitListContainer.addView(habitRow);
            }

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
                    Snackbar.make(binding.getRoot(), "Your habit progress has been saved!", Snackbar.LENGTH_SHORT).show();
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
        String unitString = "/" + goalValue + " " + unitValue;
        unit.setText(unitString);

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
