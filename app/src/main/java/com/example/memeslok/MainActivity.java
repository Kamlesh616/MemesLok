package com.example.memeslok;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.memeslok.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends Activity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActivityMainBinding binding;

    private LinearLayout likeImagesLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getMeme();
        binding.Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMeme();
            }
        });

        binding.Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareMeme();
            }
        });

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigration_open, R.string.navigration_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.login_menu) {
                    // Open the LoginActivity when the login option is selected
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                // Handle other menu options

                // Close the navigation drawer
                drawerLayout.closeDrawers();
                return true;
            }

        });
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    private void getMeme() {
        String url = "https://meme-api.com/gimme";
        binding.loader.setVisibility(View.VISIBLE);
        binding.memeImage.setVisibility(View.GONE);
        RequestQueue que = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String imgUrl = response.getString("url");
                            Glide.with(getApplicationContext())
                                    .load(imgUrl)
                                    .into(binding.memeImage);
                            binding.loader.setVisibility(View.GONE);
                            binding.memeImage.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        que.add(jsonObjectRequest);

    }

    private void ShareMeme() {
        Bitmap image = getBitmapFromView(binding.memeImage);
        shareImageandText(image);

    }

    private void shareImageandText(Bitmap image) {
        Uri uri = getImageToShare(image);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent,"Share Image Via:"));
    }

    private Uri getImageToShare(Bitmap image) {
        File imageFolder = new File(getCacheDir(),"images");
        Uri uri =null;
        try{
            imageFolder.mkdir();
            File file = new File(imageFolder,"meme.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG,100, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this,"com.rishav.shareImage.fileProvidr",file);
        }catch (Exception e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return uri;
    }


    private Bitmap getBitmapFromView(View view) {
        Bitmap returneBitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returneBitmap);
        Drawable background = view.getBackground();
        if(background != null){
            background.draw(canvas);
        }else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returneBitmap;
    }




}



