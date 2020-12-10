package com.csce4623.ahnelson.restclientexample;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddComment extends AppCompatActivity {

    // setting up UI variables
    Button submitComment;
    EditText etEmail;
    EditText etCommentName;
    EditText etCommentBody;
    int postId;
    Comment comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("EditToDoActivity", "Called onCreate");
        comment = new Comment();
        // retrieve Intent data
        Intent callingIntent = this.getIntent();
        postId = callingIntent.getIntExtra("PostId", 0);
        Log.d("POST ID IS", Integer.toString(postId));

        // set proper view
        setContentView(R.layout.add_comment);

        // link Button and EditText views, and display proper info
        submitComment = findViewById(R.id.buttonSubmit);
        etEmail = findViewById(R.id.etEmail);
        etCommentName = findViewById(R.id.etCommentName);
        etCommentBody = findViewById(R.id.etCommentBody);


        // set onclick listener to bundle data & send back to model
        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("EditToDoActivity", "Saved Changes");
                try {
                    submitComment();
                } catch (Exception e) {
                    Log.d("Error: ", String.valueOf(e));
                }
                }
            });
        }

    void submitComment() {
        Log.d("EditToDoActivity", "SaveChangesClicked");
        Log.d("EMAIL IS", etEmail.getText().toString());
        comment.setEmail(etEmail.getText().toString());
        comment.setName(etCommentName.getText().toString());
        comment.setBody(etCommentBody.getText().toString());
        // create new Intent to return to model
        Intent returningIntent = new Intent();
//        // add in the comment
        returningIntent.putExtra("CommentEmail", comment.getEmail());
        returningIntent.putExtra("CommentName", comment.getName());
        returningIntent.putExtra("CommentBody", comment.getBody());
//        // set result for the callback function which is invoked upon return to the model
        setResult(Activity.RESULT_OK, returningIntent);
        Toast.makeText(getApplicationContext(), "Your insight has been noted..", Toast.LENGTH_SHORT).show();
        finish();
    }


}
