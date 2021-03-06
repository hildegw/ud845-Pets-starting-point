package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

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
        //match content URIs
        switch (sUriMatcher.match(uri)) {
            case PETS:
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case PET_ID:
                // select just one row with _ID
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                //notify Content Resolver to watch for data changes
                break;
            default:
                //no match found
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //notify Content Resolver to watch for data changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }




    /**
     * Insert new com.example.android.pets.data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        //check input before inserting into DB
        if(contentValues.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)) {
            String name = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        } else {
            throw new IllegalArgumentException("Pet requires a name");
        }
        String breed = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
        int gender = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        int weight = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);

        //todo: deal with -1 value for weight and with exceptions

        //get writable DB
        SQLiteDatabase db = mPetDbHelper.getWritableDatabase();

        //match content URIs
        switch (sUriMatcher.match(uri)) {
            case PETS:
                //insert new pet data and return row ID
                long newRowId = db.insert(PetContract.PetEntry.TABLE_NAME, null, contentValues); //i.e. table, all columns, content values
                //error info
                if (newRowId == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                //notify Content Resolver about change
                getContext().getContentResolver().notifyChange(uri, null);
                //then return URI based on Content URI plus new row ID, i.e. the URI to reach the new pet entry
                return ContentUris.withAppendedId(uri, newRowId);
            default:
                //no match found
                throw new IllegalArgumentException("Cannot insert with unknown URI " + uri);
        }
    }


    //Not yet in use by UI >>> Todo
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        //todo: implement UI and adapt these values accordingly
        String name = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        String breed = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
        int gender = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        int weight = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);

        //todo: deal with -1 value for weight and with exceptions

        //get writable DB
        SQLiteDatabase db = mPetDbHelper.getWritableDatabase();
        //initiate return value for update method
        int numberRowsUpdated;

        //match content URIs
        switch (sUriMatcher.match(uri)) {
            case PETS:
                //update more the one row
                numberRowsUpdated = db.update(PetContract.PetEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                //error info
                if (numberRowsUpdated == 0) {
                    Log.e(LOG_TAG, "Failed to update row for " + uri);
                    return 0;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return numberRowsUpdated;
            case PET_ID:
                //find data entry matching the row ID provided by URI
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                //insert updated pet data, returns the number of rows that were updated
                numberRowsUpdated = db.update(PetContract.PetEntry.TABLE_NAME, contentValues, selection,selectionArgs); //i.e. table, content values, where-String, where-Args
                //error info
                if (numberRowsUpdated == 0) {
                    Log.e(LOG_TAG, "Failed to update row for " + uri);
                    return 0;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return numberRowsUpdated;
            default:
                //no match found
                throw new IllegalArgumentException("Cannot update with unknown URI " + uri);
        }
    }

    /**
     * Delete the com.example.android.pets.data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //get writable DB
        SQLiteDatabase db = mPetDbHelper.getWritableDatabase();
        //initiate return value for update method
        int numberRowsDeleted;

        //match content URIs
        switch (sUriMatcher.match(uri)) {
            case PETS:
                //update more the one row
                numberRowsDeleted = db.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                //error info
                if (numberRowsDeleted == 0) {
                    Log.e(LOG_TAG, "Failed to delete row for " + uri);
                    return 0;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return numberRowsDeleted;
            case PET_ID:
                //find data entry matching the row ID provided by URI
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                //insert updated pet data, returns the number of rows that were updated
                numberRowsDeleted = db.delete(PetContract.PetEntry.TABLE_NAME, selection,selectionArgs); //i.e. table, content values, where-String, where-Args
                //error info
                if (numberRowsDeleted == 0) {
                    Log.e(LOG_TAG, "Failed to delete row for " + uri);
                    return 0;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return numberRowsDeleted;
            default:
                //no match found
                throw new IllegalArgumentException("Cannot delete with unknown URI " + uri);
        }
    }

    /**
     * Returns the MIME type of com.example.android.pets.data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        //match content URIs
        switch (sUriMatcher.match(uri)) {
            case PETS:
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                //no match found
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
}