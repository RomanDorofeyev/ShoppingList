package com.rdproject.shoppinglist.modules;


import com.rdproject.shoppinglist.MainScreen;

import dagger.Component;

@Component(modules = {FirebaseModule.class, ContextModule.class, SharedPreferencesModule.class})
public interface AppComponent {

    void inject(MainScreen mainScreen);
}
