package com.referminds.app.chat.Singelton;

import android.app.Application;
import com.referminds.app.chat.Utils.Constants;
import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.URISyntaxException;

public class ChatApplication extends Application {

    private Socket mSocket;
    private static Retrofit retrofit = null;

    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
