package com.shoppinglist.rdproject.shoppinglist.modules;


import com.shoppinglist.rdproject.shoppinglist.MainScreen;

import dagger.Component;

@Component(modules = {FirebaseModule.class, ContextModule.class, SharedPreferencesModule.class})
public interface AppComponent {

    void inject(MainScreen mainScreen);
}
