package com.example.self_care_companion.ui.insights;

import static com.example.self_care_companion.MainActivity.databaseHelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.self_care_companion.databinding.FragmentInsightsBinding;
import com.example.self_care_companion.R;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class InsightsFragment extends Fragment {
    private FragmentInsightsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        InsightsViewModel insightsViewModel =
                new ViewModelProvider(this).get(InsightsViewModel.class);

        binding = FragmentInsightsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.imageButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_home);
        });

        final TextView moodReport = binding.moodReport;
        insightsViewModel.getMoodReport().observe(getViewLifecycleOwner(), moodReport::setText);
        final TextView suggestionText1 = binding.suggestionText1;
        insightsViewModel.getSuggestionText1().observe(getViewLifecycleOwner(), suggestionText1::setText);
        final TextView suggestionText2 = binding.suggestionText2;
        insightsViewModel.getSuggestionText2().observe(getViewLifecycleOwner(), suggestionText2::setText);
        final TextView suggestionText3 = binding.suggestionText3;
        insightsViewModel.getSuggestionText3().observe(getViewLifecycleOwner(), suggestionText3::setText);

        Map<String, List<String>> moodSuggestions = new HashMap<>();

        moodSuggestions.put("Happy", Arrays.asList(
                "Share your happiness with a friend",
                "Listen to your favorite upbeat music",
                "Write down what made you happy today"
        ));
        moodSuggestions.put("Sad", Arrays.asList(
                "Talk to someone you trust",
                "Watch a comforting movie",
                "Write down your feelings in a journal"
        ));
        moodSuggestions.put("Stressed", Arrays.asList(
                "Take deep breaths for 5 minutes",
                "Go for a short walk outside",
                "Try some gentle stretching exercises"
        ));
        moodSuggestions.put("Excited", Arrays.asList(
                "Plan a fun activity to channel your energy",
                "Do some creative writing or drawing",
                "Call a friend to share your excitement"
        ));
        moodSuggestions.put("Anxious", Arrays.asList(
                "Close your eyes and take a deep breath",
                "Take a walk in nature",
                "Try a 5-minute meditation"
        ));
        moodSuggestions.put("Calm", Arrays.asList(
                "Listen to calming music",
                "Practice mindfulness or yoga",
                "Enjoy a warm cup of tea"
        ));

        List<String> moods = new ArrayList<>(moodSuggestions.keySet());

        Map<String, Double> weightedMoods = databaseHelper.getWeightedMoodFrequencies(7);

        String moodParam = null;
        double maxWeight = 0.0;

        for (Map.Entry<String, Double> entry : weightedMoods.entrySet()) {
            if (entry.getValue() > maxWeight) {
                maxWeight = entry.getValue();
                moodParam = entry.getKey();
            }
        }
        String noDataText = "No mood data available";

        if (moods.contains(moodParam)) {
            List<String> suggestionsList = moodSuggestions.get(moodParam);

            if (suggestionsList == null || suggestionsList.size() < 3) {
                insightsViewModel.setMoodReport(noDataText);
                insightsViewModel.setSuggestionText1(noDataText);
                insightsViewModel.setSuggestionText2(noDataText);
                insightsViewModel.setSuggestionText3(noDataText);
            } else {
                insightsViewModel.setMoodReport("You've been feeling " + moodParam.toLowerCase() + " lately");
                insightsViewModel.setSuggestionText1(suggestionsList.get(0));
                insightsViewModel.setSuggestionText2(suggestionsList.get(1));
                insightsViewModel.setSuggestionText3(suggestionsList.get(2));
            }
        } else {
            insightsViewModel.setMoodReport(noDataText);
            insightsViewModel.setSuggestionText1(noDataText);
            insightsViewModel.setSuggestionText2(noDataText);
            insightsViewModel.setSuggestionText3(noDataText);
        }
        List<String> suggestions = moodSuggestions.get(moodParam);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}