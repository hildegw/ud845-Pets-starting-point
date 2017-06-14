package com.example.android.pets.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.R;

/**
 * Created by hildegw on 6/12/17.
 */

public class PetCursorAdapter extends CursorAdapter {

    //constructor
    public PetCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    //inflate and return view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item, parent, false);
    }

    //binding list items to active views
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView petView = (TextView) view.findViewById(R.id.pet);
        TextView detailsView = (TextView) view.findViewById(R.id.details);

        //get the info to display from cursor
        int currentId = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry._ID));
        String currentName = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME));
        String currentBreed = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED));
        int currentGender = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER));
        int currentWeight = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT));

        petView.setText(currentName);
        detailsView.setText(currentBreed);
    }
}
