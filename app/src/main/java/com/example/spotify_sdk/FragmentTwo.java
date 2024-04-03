package com.example.spotify_sdk;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;


public class FragmentTwo extends Fragment {

    TextView profileUsername, proflieEmail, profilePassword;
    TextView titleName, titleEmail;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration userDataListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);

        profileUsername = view.findViewById(R.id.profile_username);
        proflieEmail = view.findViewById(R.id.profile_userEmail);
        profilePassword = view.findViewById(R.id.profile_userPass);
        titleName = view.findViewById(R.id.titleUsername);
        titleEmail = view.findViewById(R.id.titleEmail);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userDataListener = userRef.addSnapshotListener((documentSnapshot, e) -> {
                if (e != null) {
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString("username");
                    String userEmail = documentSnapshot.getString("email");
                    String userPass = documentSnapshot.getString("password");

                    profileUsername.setText(userName);
                    proflieEmail.setText(userEmail);
                    profilePassword.setText(userPass);

                    titleName.setText(userName);
                    titleEmail.setText(userEmail);

                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userDataListener != null) {
            userDataListener.remove();
        }
    }
}