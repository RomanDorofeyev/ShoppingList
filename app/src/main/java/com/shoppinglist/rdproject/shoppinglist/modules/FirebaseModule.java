package com.shoppinglist.rdproject.shoppinglist.modules;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.shoppinglist.rdproject.shoppinglist.FirebaseDatabaseUtil;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {

    @Provides
    FirebaseDatabase provideFirebaseDatabase() {
        return FirebaseDatabaseUtil.getDatabase();
    }
    @Provides
    FirebaseAuth provideFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

}
