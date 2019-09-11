package com.referminds.app.chat.di;

import com.referminds.app.chat.view.Activity.MainActivity;

import dagger.Component;

@Component(modules = AppModule.class)
public interface AppComponent {
    void  inject(MainActivity mainActivity);
}
