package com.example.onlypuigfans;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

//import org.threeten.bp.LocalDateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class NewPostFragment extends Fragment {
    NavController navController;   // <-----------------

    Button publishButton;
    EditText postConentEditText;

    String postDateAndTime;
    String dateTimeOrdenada;
    DateTimeFormatter formatoPost =  DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss");
    DateTimeFormatter fechaOrdenada =  DateTimeFormatter.ofPattern("yyMMddHHmmss");


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPostFragment newInstance(String param1, String param2) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NewPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_post, container, false);
    }


    // ...

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);

        super.onViewCreated(view, savedInstanceState);

        publishButton = view.findViewById(R.id.publishButton);
        postConentEditText = view.findViewById(R.id.postContentEditText);

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicar();
            }
        });
    }

    private void publicar() {
        String postContent = postConentEditText.getText().toString();
        if(TextUtils.isEmpty(postContent)){
            postConentEditText.setError("Required");
            return;
        }

        publishButton.setEnabled(false);

        guardarEnFirestore(postContent);
    }
    private void guardarEnFirestore(String postContent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String photo;
        String postName;
        if(user.getDisplayName()!=null){
           postName=user.getDisplayName();
        }
        else {
            postName=user.getEmail();
        }
        if(user.getPhotoUrl()!=null){
            photo=user.getPhotoUrl().toString();
        }
        else {
            //photo=null;
            photo ="https://cdn-icons-png.flaticon.com/512/25/25231.png";
        }
        // postDateAndTime = Date.

        postDateAndTime = LocalDateTime.now().format(formatoPost);
        dateTimeOrdenada = LocalDateTime.now().format(fechaOrdenada);
        Post post = new Post(user.getUid(),postName ,postDateAndTime,dateTimeOrdenada,photo, postContent);

        FirebaseFirestore.getInstance().collection("posts")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        navController.popBackStack();
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });
    }

}
