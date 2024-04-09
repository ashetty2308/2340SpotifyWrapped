package com.example.spotify_sdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotify_sdk.databinding.ActivityMainBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }


    public static final String CLIENT_ID = "22f277e087fe4179abac40eae012e48a";
    public static final String REDIRECT_URI = "spotify-sdk://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall, mCall2;

    private TextView tokenTextView, codeTextView, profileTextView, wrappedTextView, llmTextView;

    ActivityMainBinding binding;

    public static final String geminiAPIKey = "AIzaSyAmCsPrXK6q5bm81_T86daRBVU7w6R5jws";
    public Bundle dataBundle, apiKeyStorage, topFiveSongsSeeds, topFiveArtistsSeeds;
    private boolean navSetup = false;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Initialize the views
        tokenTextView = (TextView) findViewById(R.id.token_text_view);
        codeTextView = (TextView) findViewById(R.id.code_text_view);
        profileTextView = (TextView) findViewById(R.id.response_text_view);
//        wrappedTextView = (TextView) findViewById(R.id.wrapped_text_view);
        dataBundle = new Bundle();
        // Initialize the buttons
        Button tokenBtn = (Button) findViewById(R.id.token_btn);
        Button codeBtn = (Button) findViewById(R.id.code_btn);
        Button profileBtn = (Button) findViewById(R.id.profile_btn);
        Button wrappedButton = (Button) findViewById(R.id.wrappedButton);
        Button enterButton = (Button) findViewById(R.id.enterButton);




//        apiKeyStorage = new Bundle();
//        topFiveSongsSeeds = new Bundle();
//        topFiveArtistsSeeds = new Bundle();
//        dataBundle = new Bundle();

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
        enterButton.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, NavActivity.class);
            intent.putExtra("DATA_BUNDLE", dataBundle);
            startActivity(intent);
        });


//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.Home:
//                        FragmentOne frag1 = new FragmentOne();
//                        frag1.setArguments(dataBundle);
//                        replaceFragment(frag1);
//                        return true;
//                    case R.id.Profile:
//                        replaceFragment(new FragmentTwo());
//                        return true;
//                    case R.id.Settings:
//                        FragmentThree frag3 = new FragmentThree();
//                        Log.d("DataBundle", dataBundle.toString());
//                        frag3.setArguments(dataBundle);
//                        replaceFragment(frag3);
//                        return true;
//                }
//                return false;
//            }
//        });

    }
//    private void replaceFragment(Fragment fragment){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, fragment);
//        fragmentTransaction.commit();
//    }


    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_CODE_REQUEST_CODE, request);
    }


    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            Log.d("Token", mAccessToken);
            setTextAsync(mAccessToken, tokenTextView);
            dataBundle.putString("TOKEN", mAccessToken);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            setTextAsync(mAccessCode, codeTextView);
        }
    }

    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void onGetUserProfileClicked() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user profile
//        mAccessToken = " BQABph0jGCmdXRJnG20laooDHxGZvCImMBwaVq-Cm8sop-Xr0ykjjZzetgDCJdNlg8pG-uHdM89aJ4SoZWSwX-fvd1NKOTeig0eOS_uy64q5R0rnRxytpBuc7dQv3OZB9C3nNQUpvjXfimmtiD6SJoyqNFENP4aUFRqEiHxQBATXZ-QIJOyQIk3mbDu5o36MuKTHnApeT1ZhMAuVmG0qi7mhjDUgJ80EnA";
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
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.d("response", response.toString());
                    Log.d("json", response.body().toString());
                    setTextAsync(jsonObject.toString(3), profileTextView);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
//                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
//                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void generateSpotifyWrapped() {

        ArrayList<String> myMusicTaste = new ArrayList<>();
        ArrayList<String> myMusicTasteButForDB = new ArrayList<>();

        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

//        SharedPreferences mySharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = mySharedPreferences.edit();


        RadioButton b1 = findViewById(R.id.b1);
        RadioButton b2 = findViewById(R.id.b2);
        RadioButton b3 = findViewById(R.id.b3);

        String timeRange = "";
        if (b1.isChecked()) {
            timeRange = "short_term";
        } else if (b2.isChecked()) {
            timeRange = "medium_term";
        } else {
            timeRange = "long_term";
        }
        Log.d("Time range", timeRange);
        final Request requestForTopArtists = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range="+timeRange)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        final Request requestForTopTracks = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range="+timeRange)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();


        GenerativeModel gm = new GenerativeModel("gemini-pro", geminiAPIKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);



        mCall = mOkHttpClient.newCall(requestForTopArtists);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    String[] topFiveArtists = new String[5];
                    String[] topFiveArtistsSeedsArray = new String[5];
                    String[] topFiveArtistImages = new String[5];
                    JSONArray genres;
                    // get top 5 artists
                    for (int i = 0; i < 5; i++) {
                        JSONObject artistObjectData = jsonArray.getJSONObject(i);
                        topFiveArtists[i] = artistObjectData.getString("name");
                        topFiveArtistsSeedsArray[i] = artistObjectData.getString("id");
                        genres = artistObjectData.getJSONArray("genres");
                        topFiveArtistImages[i] = artistObjectData.getJSONArray("images").getJSONObject(0).getString("url");
                        myMusicTaste.add(topFiveArtists[i]);
                        myMusicTaste.add(String.valueOf(genres));
                        myMusicTasteButForDB.add(topFiveArtists[i]);
                    }
                    dataBundle.putStringArray("ARTIST_SEEDS", topFiveArtistsSeedsArray);
                    dataBundle.putStringArray("ARTIST_IMAGES", topFiveArtistImages);
                    dataBundle.putStringArray("ARTIST_NAMES", topFiveArtists);
                    Content content = new Content.Builder()
                            // look to possibly limit the response size (characters/words/sentences, etc.)
                            .addText("Dynamically describe how I act, think, and dress based on my music taste: "+myMusicTaste)
                            .build();


                    ListenableFuture<GenerateContentResponse> res2 = model.generateContent(content);
                    Futures.addCallback(res2, new FutureCallback<GenerateContentResponse>() {
                        @Override
                        public void onSuccess(GenerateContentResponse result) {
                            String resultText = result.getText();
                            dataBundle.putString("GEMINI_DESCRIPTION", resultText);
//                            llmTextView.setMovementMethod(new ScrollingMovementMethod());
//                            llmTextView.setText(resultText);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                        }
                    }, getMainExecutor());



                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //to dynamically describe how someone who
        //listens to my kind of music tends to act/think/dre

        Log.d("my music taste", myMusicTasteButForDB.toString());

        mCall2 = mOkHttpClient.newCall(requestForTopTracks);
        mCall2.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    String[] topFiveTracks = new String[5];
                    String[] topFiveTrackSeeds = new String[5];
                    String[] topFiveSongImages = new String[5];
                    String[] topFiveSongsWho = new String[5];
                    String[] topFiveSongsURL = new String[5];
                    // get top 5 artists
                    for (int i = 0; i < 5; i++) {
                        JSONObject artistObjectData = jsonArray.getJSONObject(i);
                        topFiveTracks[i] = artistObjectData.getString("name");
                        topFiveTrackSeeds[i] = artistObjectData.getString("id");
                        topFiveSongImages[i] = artistObjectData.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                        topFiveSongsWho[i] = artistObjectData.getJSONArray("artists").getJSONObject(0).getString("name");
                        topFiveSongsURL[i] = artistObjectData.getString("preview_url");
                        myMusicTasteButForDB.add(topFiveTracks[i]);
                    }

//                    if (mySharedPreferences.getString("MUSIC", null) != null) {
//                        // there is music we have to consider to make sure we store
//
//                    }
//                    Gson gson = new Gson();
//                    String stringDataRep = gson.toJson(myMusicTasteButForDB);
//                    editor.putString("MUSIC", stringDataRep);
//                    editor.commit();
//
//
//                    Log.d("shared preferences", mySharedPreferences.getAll().toString());
//                    dataBundle.putStringArrayList("STORAGE_DATA", myMusicTasteButForDB);
                    dataBundle.putStringArray("SONG", topFiveTracks);
                    dataBundle.putStringArray("SONG_SEEDS", topFiveTrackSeeds);
                    dataBundle.putStringArray("SONG_WHO", topFiveSongsWho);
                    dataBundle.putStringArray("SONG_IMAGES", topFiveSongImages);
                    dataBundle.putStringArray("SONG_SNIPPETS", topFiveSongsURL);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

//        Content content = new Content.Builder();
////                                .addText(".")
////                                .build();
        Gson gson = new Gson();
        String musicTasteNowString = gson.toJson(myMusicTaste);
        Log.d("set my music taste", musicTasteNowString);
//        editor.putString("MUSIC", musicTasteNowString);


    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] {"user-read-private", "user-read-email", "user-read-playback-state", "user-modify-playback-state", "user-read-recently-played", "user-top-read"} ) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }
}