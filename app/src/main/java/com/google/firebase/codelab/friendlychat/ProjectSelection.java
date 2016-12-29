package com.google.firebase.codelab.friendlychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProjectSelection extends AppCompatActivity {

    private static final String TAG = "LOGLUYORUM";

    private ListView pList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_selection);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pList = (ListView) findViewById(R.id.lstText);

        String uName = MainActivity.getmUsername();

        final ProjAdapter mAdapter = new ProjAdapter(uName);

        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("ProjectNames");

        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ss : dataSnapshot.getChildren()) {
                    String item = ss.getValue().toString();
                    mAdapter.addItem(item);
                }
                if (pList != null) {
                    pList.setAdapter(mAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });

    }

    @Override
    public void onStart() { super.onStart(); }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() { super.onResume(); }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void setIt(ProjAdapter pada) {
        pList.setAdapter(pada);
    }
}
