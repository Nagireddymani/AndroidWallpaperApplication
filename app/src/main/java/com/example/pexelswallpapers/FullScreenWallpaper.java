package com.example.pexelswallpapers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.IOException;

public class FullScreenWallpaper extends AppCompatActivity {

    String originalUrl;
    PhotoView photoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_wallpaper);

        getSupportActionBar().hide();

        Intent intent =getIntent();
         originalUrl =intent.getStringExtra("originalUrl");

         photoView = findViewById(R.id.photoView);
        Glide.with(this).load(originalUrl).placeholder(R.drawable.progress_bar).diskCacheStrategy(DiskCacheStrategy.NONE).into(photoView);
    }

    public void SetWallpaperEvent(View view) {

        final AlertDialog.Builder alert =new AlertDialog.Builder(FullScreenWallpaper.this);
        View nView = getLayoutInflater().inflate(R.layout.custom_dialog,null);



        final TextView homeScreen =(TextView) nView.findViewById(R.id.homeScreen);
        final TextView lockScreen =(TextView) nView.findViewById(R.id.lockScreen);
        final TextView homeAndLock=(TextView) nView.findViewById(R.id.homeAndlock);

        alert.setView(nView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        homeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallpaperManager wallpaperManager =WallpaperManager.getInstance(FullScreenWallpaper.this);
                Bitmap bitmap = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM);
                        alertDialog.dismiss();
                        TastyToast.makeText(FullScreenWallpaper.this,"HomeScreen Changed Successfully",TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        lockScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullScreenWallpaper.this);
                Bitmap bitmap = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
                try {
                    if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.N)
                    {
                        wallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);
                        alertDialog.dismiss();
                        TastyToast.makeText(FullScreenWallpaper.this,"LockScreen Changed Successfully",TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

                    }
                    else
                    {
                        TastyToast.makeText(FullScreenWallpaper.this,"LockScreen Wallpaper Not Supported",TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        alertDialog.dismiss();
                    }
                }catch (Exception e)
                {
                    TastyToast.makeText(FullScreenWallpaper.this,e.getMessage(),TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
            }
        });

        homeAndLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallpaperManager wallpaperManager =WallpaperManager.getInstance(FullScreenWallpaper.this);
                Bitmap bitmap = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap);
                        alertDialog.dismiss();
                        TastyToast.makeText(FullScreenWallpaper.this,"Profiles Updated Successfully",TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        alertDialog.show();

    }

    public void ShareImage(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"image");
        intent.putExtra(Intent.EXTRA_TEXT,originalUrl);
        startActivity(Intent.createChooser(intent,"Share via"));

    }

    public void DownloadWallpaperEvent(View view) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri =Uri.parse(originalUrl);
        DownloadManager.Request request =new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
        TastyToast.makeText(this,"Downloading Started",TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

    }

}