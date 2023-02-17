package com.example.onlypuigfans;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;


public class SignOutFragment extends Fragment {


    public SignOutFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_out, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GoogleSignIn.getClient(requireActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build()).signOut();

        FirebaseAuth.getInstance().signOut();

        Navigation.findNavController(view).navigate(R.id.signInFragment);


        // Obtener la referencia al archivo
        Context context = requireContext();
        File file = new File(context.getFilesDir(), "account.txt");

// Sobrescribir el contenido con una cadena vacía
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}