package com.example.self_care_companion.ui.mood;

import static com.example.self_care_companion.MainActivity.databaseHelper;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.self_care_companion.R;
import com.example.self_care_companion.databinding.FragmentMoodBinding;

import java.util.List;

public class MoodFragment extends Fragment {

    private FragmentMoodBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MoodViewModel MoodViewModel =
                new ViewModelProvider(this).get(MoodViewModel.class);

        binding = FragmentMoodBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.nextButton.setOnClickListener(v -> {
            List<String> moods = MoodViewModel.getSelectedMoods().getValue();

            if (moods != null && !moods.isEmpty()) {
                for (String mood: moods) {
                    databaseHelper.addMood(mood);
                }
                Navigation.findNavController(v).navigate(R.id.navigation_journal);
            } else {
                Navigation.findNavController(v).navigate(R.id.navigation_journal);
            }
        });

        List<String> selectedMoods = MoodViewModel.getSelectedMoods().getValue();
        if (selectedMoods != null && !selectedMoods.isEmpty()) {
            for (String mood : selectedMoods) {
                switch (mood) {
                    case "Happy":
                        toggleMoodButtonTint(binding.mood1btn);
                        break;
                    case "Sad":
                        toggleMoodButtonTint(binding.mood2btn);
                        break;
                    case "Excited":
                        toggleMoodButtonTint(binding.mood3btn);
                        break;
                    case "Stressed":
                        toggleMoodButtonTint(binding.mood4btn);
                        break;
                    case "Calm":
                        toggleMoodButtonTint(binding.mood5btn);
                        break;
                    case "Anxious":
                        toggleMoodButtonTint(binding.mood6btn);
                        break;
                }
            }
        }

        MoodViewModel viewModel = new ViewModelProvider(this).get(MoodViewModel.class);

        setupMoodButton(binding.mood1btn, "Happy");
        setupMoodButton(binding.mood2btn, "Sad");
        setupMoodButton(binding.mood3btn, "Excited");
        setupMoodButton(binding.mood4btn, "Stressed");
        setupMoodButton(binding.mood5btn, "Calm");
        setupMoodButton(binding.mood6btn, "Anxious");

        final TextView textView = binding.textMood;
        MoodViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void toggleMoodButtonTint(Button button) {
        int pressedColor = ContextCompat.getColor(requireContext(), R.color.button_pressed);
        int defaultColor = ContextCompat.getColor(requireContext(), R.color.button);

        ColorStateList currentTint = button.getBackgroundTintList();
        if (currentTint != null && currentTint.getDefaultColor() == pressedColor) {
            button.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(pressedColor));
        }
    }
    private void setupMoodButton(Button button, String moodName) {
        button.setOnClickListener(v -> {
            toggleMoodButtonTint(button);
            MoodViewModel.toggleMood(moodName);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}