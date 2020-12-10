package com.csce4623.ahnelson.restclientexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostView extends Activity implements Callback<List<Comment>>{

    ArrayList<Comment> myCommentsList;
    CommentAdapter myCommentsAdapter;
    ListView lvComments;
    int postId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);
        TextView tvPostTitle = (TextView)findViewById(R.id.tvPostTitle);
        TextView tvPostBody = (TextView)findViewById(R.id.tvPostBody);
        tvPostBody.setText(this.getIntent().getStringExtra("postBody"));
        tvPostTitle.setText(this.getIntent().getStringExtra("postTitle"));
        postId = this.getIntent().getIntExtra("postId", 0);
        Log.d("INSIDE SET USER: ", Integer.toString(postId));
        findViewById(R.id.btnMakeComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });
        setUser(this.getIntent().getIntExtra("userId", 0));
        startQuery();
    }

    static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    public void setUser(int postId) {
        Log.d("INSIDE SET USER: ", Integer.toString(postId));
        // retrieve user associated with post by calling
        // users API & sending in post ID; set text on callback
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        UsersAPI usersAPI = retrofit.create(UsersAPI.class);
        Call<List<Users>> call = usersAPI.loadUsersById(postId);
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                ArrayList<Users> user = new ArrayList<Users>(response.body());
                Log.d("getting user", "we got a user!!! " + user.get(0).getUsername());
                TextView tvName = (TextView)findViewById(R.id.tvName);
                if (user != null) {
                    tvName.setText(user.get(0).getName());
                }

            }
            @Override
            public void onFailure(Call<List<Users>> cal, Throwable t) {
                Log.d("getting user","Error retreiving user");
            }
        });
    }

    public void startQuery() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        lvComments = (ListView)findViewById(R.id.lvComments);
        CommentAPI commentAPI = retrofit.create(CommentAPI.class);
        Call<List<Comment>> call = commentAPI.loadCommentByPostId(getIntent().getIntExtra("postId",0));
        call.enqueue(this);
    }

    public void addComment() {
        Intent addNewComment = new Intent(this, AddComment.class);
        Log.d("ADDING COMMENT: ", Integer.toString(postId));
        // pass To Do item to activity to manipulate data
        addNewComment.putExtra("PostId",postId);
        // returns Intent data to be used in callback onActivityResult
        startActivityForResult(addNewComment, 0);
    }

    /**
     * callback function for startActivityForResult
     * Data intent should contain a ToDoItem
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Log.d("in postview:", "onactivty result");
            String commentEmail = data.getStringExtra("CommentEmail");
            String commentName = data.getStringExtra("CommentName");
            String commentBody = data.getStringExtra("CommentBody");
            makeNewComment(commentEmail, commentName, commentBody);
        }
    }

    public void makeNewComment(String email, String name, String body){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        CommentAPI commentAPI = retrofit.create(CommentAPI.class);
        Call<Comment> call = commentAPI.addCommentToPost(postId,email, name, body);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                Comment myComment = response.body();
                Log.d("PostView","Post Created Successfully at id: " + myComment.getId());
                myCommentsList.add(myComment);
                myCommentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.d("PostView","Post Not Created");
            }
        });
    }

    @Override
    public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
        if(response.isSuccessful()) {
            myCommentsList = new ArrayList<Comment>(response.body());
            myCommentsAdapter = new PostView.CommentAdapter(this,myCommentsList);
            lvComments.setAdapter(myCommentsAdapter);
            for (Comment comment:myCommentsList) {
                Log.d("MainActivity","ID: " + comment.getId());
            }
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<Comment>> call, Throwable t) {
        t.printStackTrace();
    }

    protected class CommentAdapter extends ArrayAdapter<Comment> {
        public CommentAdapter(Context context, ArrayList<Comment> posts) {
            super(context, 0, posts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Comment comment = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_layout, parent, false);
            }
            // Lookup view for data population
            TextView tvEmail = (TextView) convertView.findViewById(R.id.tvEmail);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvCommentTitle);
            TextView tvCommentBody = (TextView) convertView.findViewById(R.id.tvCommentBody);
            // Populate the data into the template view using the data object
            assert comment != null;
            tvEmail.setText(comment.getEmail());
            tvTitle.setText(comment.getName());
            tvCommentBody.setText(comment.getBody());
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
