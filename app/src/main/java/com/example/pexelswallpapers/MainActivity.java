package com.example.pexelswallpapers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WallpaperAdapter wallpaperAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    List<WallpaperModel> wallpaperModelList;
    int pageNumber=1;
    String url=  "https://api.pexels.com/v1/curated/?page="+pageNumber+"&per_page=80";

    Boolean isScrolling= false;
    int currentItems,totalItems,scrollOutItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkConnection();
        recyclerView =findViewById(R.id.recyclerView);
        wallpaperModelList =new ArrayList<>();
        wallpaperAdapter = new WallpaperAdapter(this,wallpaperModelList);
        recyclerView.setAdapter(wallpaperAdapter);

        final SwipeRefreshLayout swipeRefreshLayout;
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchWallpaper();
                wallpaperAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        final GridLayoutManager gridLayoutManager =new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling=true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems =gridLayoutManager.getChildCount();
                totalItems =gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems+scrollOutItems==totalItems))
                {
                     isScrolling =false;
                     fetchWallpaper();
                }
            }
        });

        fetchWallpaper();

    }

    public void checkConnection() {
        ConnectivityManager manager=(ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if(activeNetwork == null)
        {
            TastyToast.makeText(this,"No Internet Connection",TastyToast.LENGTH_LONG, TastyToast.WARNING);
        }

    }

    public void fetchWallpaper(){
        StringRequest request = new StringRequest(Request.Method.GET,url ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);

                            JSONArray jsonArray=jsonObject.getJSONArray("photos");

                            int length=jsonArray.length();

                            for (int i=0;i<length;i++)
                            {
                                JSONObject object=jsonArray.getJSONObject(i);
                                int id= object.getInt("id");
                                JSONObject objectImages= object.getJSONObject("src");
                                String originalUrl = objectImages.getString("original");
                                String mediumUrl = objectImages.getString("medium");

                                WallpaperModel wallpaperModel =new WallpaperModel(id,originalUrl,mediumUrl);
                                wallpaperModelList.add(wallpaperModel);
                            }

                            wallpaperAdapter.notifyDataSetChanged();
                            pageNumber++;

                        }catch (JSONException e){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization","563492ad6f91700001000001169f71b210f848318750e0698e062151");
                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_search)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText editText =new EditText(this);
            editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            alert.setMessage("Enter category e.g. Nature");
            alert.setTitle("Search Wallpaper");

            alert.setView(editText);

            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String query =editText.getText().toString().toLowerCase();
                    url = url="https://api.pexels.com/v1/search/?page="+pageNumber+"&per_page=80&query="+query;
                    wallpaperModelList.clear();
                    fetchWallpaper();
                }
            });

            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }
}