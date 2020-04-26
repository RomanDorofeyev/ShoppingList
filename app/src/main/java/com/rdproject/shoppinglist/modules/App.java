package com.rdproject.shoppinglist.modules;

import android.app.Application;

public class App extends Application {
    private AppComponent myComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        myComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(getApplicationContext()))
                .build();
    }

    public AppComponent getAppComponent() {
        return myComponent;
    }
}