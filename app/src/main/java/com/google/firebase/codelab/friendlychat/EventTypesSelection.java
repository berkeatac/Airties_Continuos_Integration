package com.google.firebase.codelab.friendlychat;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bozhasan on 25.08.2016.
 */
public class EventTypesSelection extends BaseAdapter{
    private ArrayList<String> listArray;
    private final String Uname;

    public EventTypesSelection(String userName) {
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
                List<String> EventList = new ArrayList<String>();
                for (DataSnapshot ss : dataSnapshot.getChildren()) {
                    if (ss.child("Uname").getValue().toString().equals(Uname)) {
                        EventList = (ArrayList) ss.child("EventPref").getValue();
                    }
                }
                if (EventList.indexOf(proj) != -1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                        mSwitch.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                        List<String> EventList = new ArrayList<String>();
                        for (DataSnapshot ss : dataSnapshot.getChildren()) {
                            if (ss.child("Uname").getValue().toString().equals(Uname)) {
                                EventList = (ArrayList) ss.child("EventPref").getValue();
                            }
                        }
                        if (mCheck && !EventList.contains(proj)){
                            EventList.add(mSwitch.getText().toString());
                            for (DataSnapshot ss : dataSnapshot.getChildren()) {
                                if (ss.child("Uname").getValue().toString().equals(Uname)) {
                                    ss.child("EventPref").getRef().setValue(EventList);
                                }
                            }
                        }
                        else if (EventList.contains(proj) && !mCheck){
                            EventList.remove(mSwitch.getText().toString());
                            for (DataSnapshot ss : dataSnapshot.getChildren()) {
                                if (ss.child("Uname").getValue().toString().equals(Uname)) {
                                    ss.child("EventPref").getRef().setValue(EventList);
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
