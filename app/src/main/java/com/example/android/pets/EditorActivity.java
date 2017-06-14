/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;

import static java.lang.String.valueOf;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getName();

    //Edit Text fields to be accessed
    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;
    private static final int PET_LOADER = 0;


    //Pet Gender, see contract, 1= female, 2 = male, 0 = unknown
    private int mGender = PetContract.PetEntry.GENDER_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        setupSpinner();

        //start Loader
        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetContract.PetEntry.GENDER_FEMALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetContract.PetEntry.GENDER_MALE;;
                    } else {
                        mGender = PetContract.PetEntry.GENDER_UNKNOWN;;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetContract.PetEntry.GENDER_UNKNOWN; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //check, if pet name was entered, if not, stay in Editor
                if (mNameEditText.length() == 0) {
                    Toast.makeText(this, getString(R.string.enter_pet_name), Toast.LENGTH_LONG).show();
                } else {
                    insertPet();
                    finish();
                    return true;
                }
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //add the pet com.example.android.pets.data entered to the DB
    private void insertPet() {
        //get user input, Gender is already available via Spinner method
        String petName = mNameEditText.getText().toString().trim();
        String breed = mBreedEditText.getText().toString().trim();
        //set weight to -1 if user did not enter weight
        int weight;
        if (mWeightEditText.length() > 0) {
            weight = Integer.parseInt(mWeightEditText.getText().toString().trim());
        } else {
            weight = -1;
        }

        // Create a new map of values from user input and insert into DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetContract.PetEntry.COLUMN_PET_NAME, petName);
        contentValues.put(PetContract.PetEntry.COLUMN_PET_BREED, breed);
        contentValues.put(PetContract.PetEntry.COLUMN_PET_GENDER, mGender);
        contentValues.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, weight);

        //call Content Resolver with Content URI and the content values entered by user
        Uri mNewUri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, contentValues);

        // Show a toast message depending on whether or not the insertion was successful
        if (mNewUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //define projection to query DB, here: all columns
        String[] projection = {PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_WEIGHT};

        //provide data for pet to be edited, if available
        String selection = null;
        String[] selectionArgs = new String[] {"1"};
        Uri uri = PetContract.PetEntry.CONTENT_URI;

        if( getIntent().getExtras() != null) {
            long petId = getIntent().getLongExtra("Pet-Id", 0);
            Log.i("Intent = ", valueOf(petId));
            uri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, petId);
            //uri = Uri.parse(PetContract.PetEntry.CONTENT_URI petId);
            Log.i("lesson ", String.valueOf(ContentUris.parseId(uri)));
            Log.i("uri in Editor: Intent ", valueOf(uri));
            selection = PetContract.PetEntry._ID + "=?";
            selectionArgs = new String[]{valueOf(petId)};
            Log.i("Args0 ", selectionArgs[0]);
        }
        return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {
            // Attach cursor information to populate the EditText fields
            Log.i("# ", cursor.getString(cursor.getColumnIndex(PetContract.PetEntry._ID)));
            mNameEditText.setText(cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME)));
            mBreedEditText.setText(cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED)));
            mWeightEditText.setText(valueOf(cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT))));
            mGenderSpinner.setSelection(cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER)));
            cursor.close();
        }
        //todo inform about change, clean-up
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //no clean-up required, no Adapter used
    }

}