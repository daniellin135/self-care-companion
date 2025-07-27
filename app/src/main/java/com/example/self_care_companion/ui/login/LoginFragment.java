package com.example.self_care_companion.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.self_care_companion.DatabaseHelper;
import com.example.self_care_companion.R;
import java.util.Calendar;

public class LoginFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private EditText pinInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        dbHelper = new DatabaseHelper(requireContext());
        pinInput = view.findViewById(R.id.pin_input);
        Button loginButton = view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            String enteredPin = pinInput.getText().toString();
            String hashedInput = DatabaseHelper.hashPin(enteredPin);
            String storedPin = dbHelper.getUserPin();

            if (storedPin != null && storedPin.equals(hashedInput)) {

                String nameFromDB = dbHelper.getUserFirstName();

                requireContext()
                        .getSharedPreferences("selfcare", Context.MODE_PRIVATE)
                        .edit()
                        .putString("user_name", nameFromDB)
                        .apply();

                Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                if (hour < 17) {
                    // Before 5 PM → navigate to check-in (User Scenario 1)
                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
                } else {
                    // After 5 PM → navigate to journal prompt (User Scenario 2)
                    Navigation.findNavController(view).navigate(R.id.navigation_journal_prompt);
                }

            } else {
                Toast.makeText(requireContext(), "Incorrect PIN", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
