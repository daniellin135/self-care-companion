package com.example.self_care_companion.ui.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.self_care_companion.DatabaseHelper;
import com.example.self_care_companion.R;

public class SignUpFragment extends Fragment {

    private EditText usernameInput, emailInput, pinInput;
    private DatabaseHelper db;

    public SignUpFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        usernameInput = view.findViewById(R.id.et_username);
        emailInput = view.findViewById(R.id.et_email);
        pinInput = view.findViewById(R.id.et_password);
        Button signUpButton = view.findViewById(R.id.btn_create_account);

        db = new DatabaseHelper(requireContext());

        signUpButton.setOnClickListener(v -> {
            String name = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String pin = pinInput.getText().toString().trim();

            // Validation
            if (name.isEmpty() || email.isEmpty() || pin.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(requireContext(), "Enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pin.length() < 4) {
                Toast.makeText(requireContext(), "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            db.addUser(name, email, pin);
            requireContext()
                    .getSharedPreferences("selfcare", Context.MODE_PRIVATE)
                    .edit()
                    .putString("user_name", name)
                    .apply();
            Toast.makeText(requireContext(), "Account created!", Toast.LENGTH_SHORT).show();

            // Clear fields
            usernameInput.setText("");
            emailInput.setText("");
            pinInput.setText("");

            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            // Navigate to login
            Navigation.findNavController(v).navigate(R.id.action_signUpFragment_to_loginFragment);
        });
    }
}
