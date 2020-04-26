package com.shoppinglist.rdproject.shoppinglist;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseUtil {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}

