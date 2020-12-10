package com.csce4623.ahnelson.restclientexample;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    static final String BASE_URL = "https://jsonplaceholder.typicode.com/";
    ArrayList<Users> user;
    ArrayList<Post> myPostList;
    ListView lvPostVList;
    PostAdapter myPostAdapter;
    double lat;
    double lng;
    TextView tvUserPageName;
    TextView tvUserPageUserName;
    TextView tvUserPageEmail;
    TextView tvUserPageWebsite;
    TextView tvUserPagePhone;
    TextView tvAllPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page);

        tvUserPageName = (TextView)findViewById(R.id.tvUserPageName);
        tvUserPageUserName = (TextView)findViewById(R.id.tvUserPageUsername);
        tvUserPageEmail = (TextView)findViewById(R.id.tvUserPageEmail);
        tvUserPageWebsite = (TextView)findViewById(R.id.tvUserPageWebsite);
        tvUserPagePhone = (TextView)findViewById(R.id.tvUserPagePhone);
        lvPostVList = (ListView)findViewById(R.id.lvUserPosts);
        tvAllPosts = (TextView)findViewById(R.id.tvAllPosts);
        lvPostVList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClicked(parent, view, position,id);
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
        }
        // fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mapFragment != null) {
            transaction.add(R.id.mapFrame, mapFragment);
            transaction.commit();
        }
        Log.d("INSIDE USERPAGE", "!!!!");
        setUser(this.getIntent().getIntExtra("postId", 0));
        mapFragment.getMapAsync(this);
    }

    void itemClicked(AdapterView<?> parent, View view, int position, long id){

        Intent myIntent = new Intent(this,PostView.class);
        myIntent.putExtra("postId",myPostList.get(position).getId());
        myIntent.putExtra("postTitle",myPostList.get(position).getTitle());
        myIntent.putExtra("postBody",myPostList.get(position).getBody());
        myIntent.putExtra("userId",myPostList.get(position).getUserId());

        startActivity(myIntent);
    }

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
                user = new ArrayList<Users>(response.body());
                Log.d("getting user", "we got a user!!! " + user.get(0).getUsername());
                Log.d("getting user", "we got a user!!! " + user.get(0).getWebsite());
                Log.d("getting user", "LAT IS  " + user.get(0).getAddress().getGeo().getLat());
                lat = Double.parseDouble(user.get(0).getAddress().getGeo().getLat());
                lng = Double.parseDouble(user.get(0).getAddress().getGeo().getLng());
                tvUserPageEmail.setText(user.get(0).getEmail());
                tvUserPageName.setText(user.get(0).getName());
                tvUserPagePhone.setText(user.get(0).getPhone());
                tvUserPageWebsite.setText(user.get(0).getWebsite());
                tvUserPageUserName.setText(user.get(0).getUsername());
                tvAllPosts.setText("All Posts By " + user.get(0).getName());
                getPosts();
            }
            @Override
            public void onFailure(Call<List<Users>> cal, Throwable t) {
                Log.d("getting user","Error retreiving user");
            }
        });
    }

    public void getPosts() {

        Debug.startMethodTracing("test");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        PostAPI postAPI = retrofit.create(PostAPI.class);

        Call<List<Post>> call = postAPI.loadPostsByUser(user.get(0).getId());
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    myPostList = new ArrayList<Post>(response.body());
                    myPostAdapter = new PostAdapter(getApplicationContext(), myPostList);
                    lvPostVList.setAdapter(myPostAdapter);
                    for (Post post : myPostList) {
                        Log.d("MainActivity", "ID: " + post.getId());
                    }
                } else {
                    System.out.println(response.errorBody());
                }
                Debug.stopMethodTracing();
            }

            @Override
            public void onFailure(Call<List<Post>> cal, Throwable t) {
                Log.d("getting user","Error retreiving user");
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
            tvUser.setText(user.get(0).getUsername());


            // Return the completed view to render on screen
            return convertView;
        }
    }
}