package com.referminds.app.chat.Singelton;

import android.app.Application;
import com.referminds.app.chat.DI.AppComponent;
import com.referminds.app.chat.DI.AppModule;
import com.referminds.app.chat.Utils.Constants;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.URISyntaxException;

public class ChatApplication extends Application {

    private Socket mSocket;
    private static Retrofit retrofit = null;
    private static ChatApplication chataap;
    private AppComponent appComponent;

    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        chataap  = this;
        Daggerinit();
        Realm.init(getApplicationContext());


        RealmConfiguration config =
                new RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();

        Realm.setDefaultConfiguration(config);
    }
    public static ChatApplication getChataap(){
        return chataap;
    }

    private void Daggerinit() {
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
