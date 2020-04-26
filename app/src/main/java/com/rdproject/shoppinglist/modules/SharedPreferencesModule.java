package com.rdproject.shoppinglist.modules;

import android.content.Context;
import android.content.SharedPreferences;

import dagger.Module;
import dagger.Provides;

import static com.rdproject.shoppinglist.MainScreen.APP_PREFERENCES;

@Module
public class SharedPreferencesModule {


    @Provides
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
    }
}