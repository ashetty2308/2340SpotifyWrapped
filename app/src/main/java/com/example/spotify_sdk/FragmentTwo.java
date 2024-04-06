package com.example.spotify_sdk;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;



import java.util.HashMap;
import java.util.Map;


public class FragmentTwo extends Fragment {

    TextView profileUsername, proflieEmail, profilePassword;
    TextView titleName, titleEmail;

    Button editProfile, deleteProfile;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
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

        editProfile = view.findViewById(R.id.editProfile_button);
        deleteProfile = view.findViewById(R.id.deleteProfile_button);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();



        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
                        .setContentHolder(new ViewHolder(R.layout.edit_popup))
                        .setExpanded(true, 1500)
                        .create();

                dialogPlus.show();

                View view1 = dialogPlus.getHolderView();

                EditText username = view1.findViewById(R.id.editprofile_username);
                EditText email = view1.findViewById(R.id.editprofile_userEmail);
                EditText password = view1.findViewById(R.id.editprofile_userPass);

                Button saveButton = view1.findViewById(R.id.savechanges_profile_button);


                if (currentUser != null) {
                    String Name = currentUser.getDisplayName();
                    String userEmail = currentUser.getEmail();
                    String userPass = null;


                    username.setText(Name);
                    email.setText(userEmail);
                    password.setText(userPass);
                }

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName = username.getText().toString();
                        String newEmail = email.getText().toString();
                        String pass = password.getText().toString();

                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(newName)
                                .build();
                        currentUser.updateProfile(profileChangeRequest)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Username Updated!");

                                            profileUsername.setText(newName);
                                            titleName.setText(newName);
                                        } else {
                                            Log.e(TAG, "Failed to update username!");
                                        }
                                    }
                                });

                        if (!pass.isEmpty()) {
                            currentUser.updatePassword(pass)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseFirestore.getInstance().collection("users")
                                                        .document(currentUser.getUid())
                                                        .update("password", pass)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Log.d(TAG, "Updated email into Firestore successfully!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.e(TAG, "Failed to update email in Firestore!", e);
                                                            }
                                                        });
                                                Log.d(TAG, "User Password Updated!");
                                            } else {
                                                Log.e(TAG, "Failed to update user password!");
                                            }
                                        }
                                    });
                        }

                        if (!newEmail.equals(currentUser.getEmail())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setTitle("Warning")
                                    .setMessage("If you are changing your email, make sure that you will verify it to apply change!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            currentUser.verifyBeforeUpdateEmail(newEmail);
                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        dialogPlus.dismiss();
                    }


                });

            }
        });

        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setMessage("Are you sure you want to delete your account?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUserAccount();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Dismiss the dialog if canceled
                            }
                        })
                        .show();
            }
        });
        return view;
    }

    private void deleteUserAccount() {
        if (currentUser != null) {
            currentUser.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                deleteUserDataFromFirestore();
                                navigatetoLogin();
                            } else {
                                Toast.makeText(getContext(), "Failed to delete user account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void navigatetoLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void deleteUserDataFromFirestore() {
        FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getUid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "User data deleted successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to delete user data from Firestore!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();
            String userPass = "********";

            profileUsername.setText(username);
            proflieEmail.setText(userEmail);
            profilePassword.setText(userPass);

            titleName.setText(username);
            titleEmail.setText(userEmail);

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