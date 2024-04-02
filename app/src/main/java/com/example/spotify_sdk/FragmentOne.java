package com.example.spotify_sdk;

import android.content.Intent;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FragmentOne extends Fragment {
    public static final String CLIENT_ID = "22f277e087fe4179abac40eae012e48a";
    public static final String REDIRECT_URI = "spotify-sdk://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall, mCall2;

    private TextView tokenTextView, codeTextView, profileTextView, wrappedTextView;



    public FragmentOne() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_one, container, false);

       tokenTextView = (TextView) view.findViewById(R.id.token_text_view);
        codeTextView = (TextView) view.findViewById(R.id.code_text_view);
        profileTextView = (TextView) view.findViewById(R.id.response_text_view);
        //wrappedTextView = (TextView) view.findViewById(R.id.wrapped_text_view);


        // Initialize the buttons
        Button tokenBtn = (Button) view.findViewById(R.id.token_btn);
        Button codeBtn = (Button) view.findViewById(R.id.code_btn);
        Button profileBtn = (Button) view.findViewById(R.id.profile_btn);
        Button wrappedButton = (Button) view.findViewById(R.id.wrappedButton);




        // Set the click listeners for the buttons


        tokenBtn.setOnClickListener((v) -> {
            getToken();
        });


        codeBtn.setOnClickListener((v) -> {
            getCode();
        });


        profileBtn.setOnClickListener((v) -> {
            onGetUserProfileClicked();
        });

        wrappedButton.setOnClickListener((v) -> {
            generateSpotifyWrapped();
        });

        return view;
    }


    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(getActivity(), AUTH_TOKEN_REQUEST_CODE, request);
    }


    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(getActivity(), AUTH_CODE_REQUEST_CODE, request);
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        final AuthorizationResponse response = AuthorizationClient.getResponse(result.getResultCode(), result.getData());


        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == result.getResultCode()) {
            mAccessToken = response.getAccessToken();
            Log.d("Token", mAccessToken);
            setTextAsync(mAccessToken, tokenTextView);


        } else if (AUTH_CODE_REQUEST_CODE == result.getResultCode()) {
            mAccessCode = response.getCode();
            setTextAsync(mAccessCode, codeTextView);
        }
    });

    public void onGetUserProfileClicked() {
        if (mAccessToken == null) {
            Toast.makeText(getActivity(), "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }


        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();


        cancelCall();
        mCall = mOkHttpClient.newCall(request);


        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(getActivity(), "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setTextAsync(jsonObject.toString(3), profileTextView);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(getActivity(), "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void generateSpotifyWrapped() {
        if (mAccessToken == null) {
            Toast.makeText(getActivity(), "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }
        final Request requestForTopArtists = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();


        final Request requestForTopTracks = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();


        cancelCall();


        mCall = mOkHttpClient.newCall(requestForTopArtists);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(getActivity(), "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    String[] topFiveArtists = new String[5];
                    // get top 5 artists
                    for (int i = 0; i < 5; i++) {
                        JSONObject artistObjectData = jsonArray.getJSONObject(i);
                        topFiveArtists[i] = artistObjectData.getString("name");
                        JSONArray genres = artistObjectData.getJSONArray("genres");
                        Log.d("genres", (String) genres.get(0));
                    }
                    for (int i = 0; i < topFiveArtists.length; i++) {
                        Log.d("genre:", topFiveArtists[i]);
                    }
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(getActivity(), "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });




        mCall2 = mOkHttpClient.newCall(requestForTopTracks);
        mCall2.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(getActivity(), "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    String[] topFiveTracks = new String[5];
                    // get top 5 artists
                    for (int i = 0; i < 5; i++) {
                        JSONObject artistObjectData = jsonArray.getJSONObject(i);
                        topFiveTracks[i] = artistObjectData.getString("name");
                    }
                    for (int i = 0; i < topFiveTracks.length; i++) {
                        Log.d("artist:", topFiveTracks[i]);
                    }
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(getActivity(), "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setTextAsync(final String text, TextView textView) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textView != null) {
                    textView.setText(text);
                }
            }
        });
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-private", "user-top-read"} ) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }




    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }


    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }@Override
    public void onDestroy() {
        cancelCall();
        super.onDestroy();






    }















}