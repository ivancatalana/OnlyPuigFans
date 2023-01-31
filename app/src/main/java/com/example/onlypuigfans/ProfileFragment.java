package com.example.onlypuigfans;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    ImageView photoImageView;
    TextView displayNameTextView, emailTextView;
    NavController navController;
    private Button editarFoto;
    public ProfileFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        photoImageView = view.findViewById(R.id.photoImageView);
        displayNameTextView = view.findViewById(R.id.displayNameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        editarFoto =  view.findViewById(R.id.cambiarFoto);
        navController = Navigation.findNavController(view);
        /*
        // Boton Floating Listener

        view.findViewById(R.id.gotoEditProfileFragmentButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),EditProfilePage.class);
                startActivity(intent);
            }
        });

         */


        editarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.profilePictureFragment);

            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            if (user.getDisplayName()==null){
                displayNameTextView.setText("Imagen No Disponible");
                emailTextView.setText(user.getEmail());

            }
            else {
                displayNameTextView.setText(user.getDisplayName());
                emailTextView.setText(user.getEmail());
            }
            if(user.getPhotoUrl()==null){
                photoImageView.setImageResource(R.drawable.profile);
            }
              else{
                Glide.with(requireView()).load(user.getPhotoUrl()).into(photoImageView);
            }
        }
    }
}
