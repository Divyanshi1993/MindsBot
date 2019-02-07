package com.referminds.app.chat.view.Listners;

public interface AuthenticationListner {
   void onAuthenticated(String username);
   void  onAuthenticationFailed();
}
