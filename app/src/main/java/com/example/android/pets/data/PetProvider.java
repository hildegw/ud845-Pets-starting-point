package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    //prepare UriMatcher
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PETS = 100;
    private static final int PET_ID = 101;
    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    //provide DB helper
    private PetDbHelper mPetDbHelper;

    //Initialize the provider and the database helper object.
    @Override
    public boolean onCreate() {
        // Initialize a PetDbHelper object to gain access to the pets database.
        mPetDbHelper = new PetDbHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }



    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    // Implements ContentProvider.query()
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //get readable DB
        SQLiteDatabase database = mPetDbHelper.getReadableDatabase();
        Cursor cursor;
        // select all columns for projection - todo: not sure if this goes into Catalog Activity
        projection = new String[] {PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME, PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.COLUMN_PET_WEIGHT};

        //match content URIs
        switch (sUriMatcher.match(uri)) {
            case PETS:
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, null, null, null, null, null);
                break;
            case PET_ID:
                // select just one row with _ID
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);                break;
                break;
            default:
                //no match found
                throw  new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }




    /**
     * Insert new com.example.android.pets.data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    /**
     * Updates the com.example.android.pets.data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the com.example.android.pets.data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of com.example.android.pets.data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}