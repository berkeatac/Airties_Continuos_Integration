/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "LOGLUYORUM";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private  String ANONYMOUS = "ANONYMOUS";
    private static String ProjemailAddr;

    //Switches
    private Switch PushNot;
    //Google Account Image
    private CircleImageView messengerImageView;
    //Google Account Name
    private TextView UserName;
    //Switch to Projects Page
    private Button pButton;
    //Events' ListView
    private ListView eListView;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        final String token = FirebaseInstanceId.getInstance().getToken();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        PushNot = (Switch) findViewById(R.id.PushSwitch);

        DatabaseReference nFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        nFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ss : dataSnapshot.getChildren()) {
                    if (ss.child("Uname").getValue().toString().equals(ProjemailAddr)) {
                        if (ss.child("PushNot").getValue().toString().equals("1"))
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                                PushNot.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });

        PushNot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final boolean mCheck = isChecked;

                DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

                mFirebaseDatabaseReference.addListenerForSingleValueEvent (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ss : dataSnapshot.getChildren()) {
                            if (ss.child("Uname").getValue().toString().equals(ProjemailAddr)) {
                                if (mCheck)
                                    ss.child("PushNot").getRef().setValue(1);
                                else
                                    ss.child("PushNot").getRef().setValue(0);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //TODO
                    }
                });
            }
        });

        messengerImageView = (CircleImageView) findViewById(R.id.messengerImageView);

        Glide.with(MainActivity.this)
                .load(mPhotoUrl)
                .into(messengerImageView);

        UserName = (TextView) findViewById(R.id.editText);

        UserName.setText(mUsername);

        pButton = (Button) findViewById(R.id.pButton);

        ProjemailAddr = mFirebaseUser.getEmail();
        int inx = ProjemailAddr.indexOf("@");
        ProjemailAddr = ProjemailAddr.substring(0, inx);

        pButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProjectSelection.class);
                startActivity(intent);
            }
        });

        eListView = (ListView) findViewById(R.id.listViewMain);

        final EventTypesSelection mAdapter = new EventTypesSelection(ProjemailAddr);

        DatabaseReference TokenRef = FirebaseDatabase.getInstance().getReference("Users");

        TokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ss : dataSnapshot.getChildren()) {
                    if (ss.child("Uname").getValue().toString().equals(ProjemailAddr)) {
                        ss.child("Token").getRef().setValue(token);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });

        DatabaseReference EventFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("EventTypes");

        EventFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ss : dataSnapshot.getChildren()) {
                    String item = ss.getValue().toString();
                    mAdapter.addItem(item);
                }
                if (eListView != null) {
                    eListView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });

    }

    public static String getmUsername() {
        return ProjemailAddr;
    }

    @Override
    public void onStart() {
        super.onStart();
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}
