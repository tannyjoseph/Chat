
package com.example.chat;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyListAdapter extends FirebaseListAdapter<Message> {

    private MainActivity activity;

    public MyListAdapter(MainActivity activity, Class<Message> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity = activity;
    }



    @Override
    protected void populateView(View v, Message model, int position) {
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        TextView messageUser = (TextView) v.findViewById(R.id.message_user);
        TextView messageTime = (TextView) v.findViewById(R.id.message_time);

        messageText.setText(model.getMessageText());
        messageUser.setText(model.getMessageUser());

        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Message chatMessage = getItem(position);
        if (chatMessage.getMessageUserId().equals(activity.getLoggedInUserName()))
            view = activity.getLayoutInflater().inflate(R.layout.message, viewGroup, false);
        else
            view = activity.getLayoutInflater().inflate(R.layout.inmesa, viewGroup, false);

        //generating view
        populateView(view, chatMessage, position);

        return view;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }
}