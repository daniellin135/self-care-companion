package com.example.self_care_companion.ui.splash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.self_care_companion.DatabaseHelper;
import com.example.self_care_companion.R;

public class SplashFragment extends Fragment {

    public SplashFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        Button signUpBtn = view.findViewById(R.id.btn_sign_up);
        Button loginBtn = view.findViewById(R.id.btn_log_in);

        DatabaseHelper db = new DatabaseHelper(requireContext());
        if (db.checkifUserExists()) {
            signUpBtn.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        } else {
            signUpBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }

        signUpBtn.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_signUpFragment);
        });

        loginBtn.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_loginFragment);
        });

        return view;
    }
}
