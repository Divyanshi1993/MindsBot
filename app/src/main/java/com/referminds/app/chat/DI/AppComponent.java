package com.referminds.app.chat.DI;

import com.referminds.app.chat.Activity.MainActivity;
import dagger.Component;

@Component(modules = AppModule.class)
public interface AppComponent {
    void  inject(MainActivity mainActivity);
}
