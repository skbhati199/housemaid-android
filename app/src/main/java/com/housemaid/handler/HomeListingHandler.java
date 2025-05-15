package com.housemaid.handler;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.housemaid.model.bean.UserBean;

/**
 * Created by fluper on 23/5/18.
 */

public class HomeListingHandler {
    private final Context context;
    private final UserBean userBean;

    public HomeListingHandler(Context context, UserBean userBean) {
        this.context = context;
        this.userBean = userBean;
    }


    public void onMessageClicked(View view){
        Toast.makeText(context, "on message pressed", Toast.LENGTH_SHORT).show();

    }
}
