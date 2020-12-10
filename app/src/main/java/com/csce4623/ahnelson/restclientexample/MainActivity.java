package com.csce4623.ahnelson.restclientexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity implements Callback<List<Post>> {

    ArrayList<Post> myPostList;
    ArrayList<Users> user;
    ListView lvPostVList;
    PostAdapter myPostAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvPostVList = (ListView)findViewById(R.id.lvPostList);
        lvPostVList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClicked(parent, view, position,id);
            }
        });
        startQuery();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }

    void itemClicked(AdapterView<?> parent, View view, int position, long id){

        Intent myIntent = new Intent(this,PostView.class);
        myIntent.putExtra("postId",myPostList.get(position).getId());
        myIntent.putExtra("postTitle",myPostList.get(position).getTitle());
        myIntent.putExtra("postBody",myPostList.get(position).getBody());
        myIntent.putExtra("userId",myPostList.get(position).getUserId());

        startActivity(myIntent);
    }



    static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    public void startQuery() {

        Debug.startMethodTracing("test");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        PostAPI postAPI = retrofit.create(PostAPI.class);

        Call<List<Post>> call = postAPI.loadPosts();
        call.enqueue(this);

    }

    @Override
    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
        if(response.isSuccessful()) {
            myPostList = new ArrayList<Post>(response.body());
            myPostAdapter = new PostAdapter(this,myPostList);
            lvPostVList.setAdapter(myPostAdapter);
            for (Post post:myPostList) {
                Log.d("MainActivity","ID: " + post.getId());
            }
        } else {
            System.out.println(response.errorBody());
        }
        Debug.stopMethodTracing();
    }

    @Override
    public void onFailure(Call<List<Post>> call, Throwable t) {
        t.printStackTrace();
    }


    protected class PostAdapter extends ArrayAdapter<Post> {
        public PostAdapter(Context context, ArrayList<Post> posts) {
            super(context, 0, posts);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final Post post = getItem(position);
            Log.d("IN GET VIEW:", Integer.toString(post.getId()));
            Log.d("GET VIEW USER:", Integer.toString(post.getUserId()));
//            Users postOwner = getUser(post.getUserId());

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_layout, parent, false);
            }
            // Lookup view for data population
            TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

            // Populate the data into the template view using the data object
//            Log.d("POST ADAPTER:", Integer.toString(post.getUserId()));
//            Users user = getUser(post.getUserId());
//            Log.d("POST ADAPTER USER:", user.getName());

            tvTitle.setText(post.getTitle());
            tvId.setText(Integer.toString(post.getId()));
            final TextView tvUser = (TextView) convertView.findViewById(R.id.tvUser);
            tvUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Main activity", "clicked username");
                    Intent myIntent = new Intent(getContext(),MapsActivity.class);
                    myIntent.putExtra("postId",post.getUserId());
                    startActivity(myIntent);
                }
            });

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
            Call<List<Users>> call = usersAPI.loadUsersById(post.getUserId());
            call.enqueue(new Callback<List<Users>>() {
                @Override
                public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                    user = new ArrayList<Users>(response.body());
                    Log.d("getting user", "we got a user!!! " + user.get(0).getUsername());
                    if (user != null) {
                        tvUser.setText(user.get(0).getUsername());
                    }

                }
                @Override
                public void onFailure(Call<List<Users>> cal, Throwable t) {
                    Log.d("getting user","Error retreiving user");
                }
            });

            // Return the completed view to render on screen
            return convertView;
        }
    }


}
