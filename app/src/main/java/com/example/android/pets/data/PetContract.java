package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by hildegw on 6/8/17.
 */

public final class PetContract {

    //content authority
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    //content provider URIs
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //content path
    public static final String PATH_PETS = "pets";

    //constructor
    private PetContract() {};

    //sub-class for pets table
    public static final class PetEntry implements BaseColumns {

        //content uri for pets table
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        //table name = content path name
        public static final String TABLE_NAME = "pets";

        //table setup
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";

        //gender values for pets
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_FEMALE = 1;
        public static final int GENDER_MALE = 2;



    }
}
