package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hildegw on 6/8/17.
 */

public class PetDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Pets.db";

    //DB commands
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PetContract.PetEntry.TABLE_NAME + " (" +
                    PetContract.PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PetContract.PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL," +
                    PetContract.PetEntry.COLUMN_PET_BREED + " TEXT," +
                    PetContract.PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL DEFAULT NULL," +
                    PetContract.PetEntry.COLUMN_PET_WEIGHT + " INTEGER)";

    private static final String SQL_UPGRADE_ENTRIES =
            "DROP TABLE IF EXISTS " + PetContract.PetEntry.TABLE_NAME;

    //constructor
    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //DB helper methods
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // todo: upgrade policy
        db.execSQL(SQL_UPGRADE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //todo: implement
        onUpgrade(db, oldVersion, newVersion);
    }

}
