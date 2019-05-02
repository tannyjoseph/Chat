package com.example.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import com.firebase.ui.auth.AuthUI;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mdatabase;
    private static final int SIGN_IN_REQUEST_CODE = 111;
    private FirebaseListAdapter<Message> adapter;
    private ListView listView;
    private String loggedInUserName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        final EditText input = (EditText) findViewById(R.id.input);


        listView = (ListView)findViewById(R.id.list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(), SIGN_IN_REQUEST_CODE);
        } else {
            // User is already signed in, show list of messages
            showAllOldMessages();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter some texts!", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .push()
                            .setValue(new Message(input.getText().toString(),
                                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())
                            );
                    input.setText("");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.signout) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "You have logged out!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in successful!", Toast.LENGTH_LONG).show();
                showAllOldMessages();
            } else {
                Toast.makeText(this, "Sign in failed, please try again later", Toast.LENGTH_LONG).show();

                // Close the app
                finish();
            }
        }
    }


    private void showAllOldMessages() {
        loggedInUserName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("Main", "user id: " + loggedInUserName);

        adapter = new MyListAdapter(this, Message.class, R.layout.inmesa,
                FirebaseDatabase.getInstance().getReference());
        listView.setAdapter(adapter);
    }

    public String getLoggedInUserName() {
        return loggedInUserName;
    }
}











