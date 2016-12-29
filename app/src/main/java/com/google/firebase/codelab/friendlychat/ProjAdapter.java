package com.google.firebase.codelab.friendlychat;

/**
 * Created by bozhasan on 23.08.2016.
 */

import java.util.ArrayList;
import java.util.List;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProjAdapter extends BaseAdapter{

    private ArrayList<String> listArray;
    private final String Uname;

    public ProjAdapter(String userName) {
        listArray = new ArrayList<String>();
        Uname = userName;
    }

    public void addItem(String item) {
        listArray.add(item);
    }

    @Override
    public int getCount() {
        return listArray.size();    // total number of elements in the list
    }

    @Override
    public Object getItem(int i) {
        return listArray.get(i);    // single item in the list
    }

    @Override
    public long getItemId(int i) {
        return i;                   // index number
    }

    @Override
    public View getView(int index, View view, final ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.item_message, parent, false);


        final String proj = listArray.get(index);

        final Switch mSwitch = (Switch) view.findViewById(R.id.projSwitch);
        mSwitch.setText(proj);

        DatabaseReference nFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        nFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> ProjList = new ArrayList<String>();
                for (DataSnapshot ss : dataSnapshot.getChildren()) {
                    if (ss.child("Uname").getValue().toString().equals(Uname)) {
                        ProjList = (ArrayList) ss.child("ProjectPref").getValue();
                    }
                }
                if (ProjList.indexOf(proj) != -1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        mSwitch.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final boolean mCheck = isChecked;

                DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

                mFirebaseDatabaseReference.addListenerForSingleValueEvent (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> ProjList = new ArrayList<String>();
                        for (DataSnapshot ss : dataSnapshot.getChildren()) {
                            if (ss.child("Uname").getValue().toString().equals(Uname)) {
                                ProjList = (ArrayList) ss.child("ProjectPref").getValue();
                            }
                        }
                        if (mCheck && !ProjList.contains(proj)){
                            ProjList.add(mSwitch.getText().toString());
                            for (DataSnapshot ss : dataSnapshot.getChildren()) {
                                if (ss.child("Uname").getValue().toString().equals(Uname)) {
                                    ss.child("ProjectPref").getRef().setValue(ProjList);
                                }
                            }
                        }
                        else if (!mCheck && ProjList.contains(proj)) {
                            ProjList.remove(mSwitch.getText().toString());
                            for (DataSnapshot ss : dataSnapshot.getChildren()) {
                                if (ss.child("Uname").getValue().toString().equals(Uname)) {
                                    ss.child("ProjectPref").getRef().setValue(ProjList);
                                }
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

        return view;
    }
}
