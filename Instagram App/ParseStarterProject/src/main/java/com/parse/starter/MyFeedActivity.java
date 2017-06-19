package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyFeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feed);

        final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayout);

        String username = ParseUser.getCurrentUser().getUsername();

        setTitle("My Uploads");

        // get all images by the username
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");
        query.whereEqualTo("username", username);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject object : objects){
                            ParseFile file = (ParseFile) object.get("image"); // not downloaded yet
                            final Date fileDate = (Date) object.getCreatedAt();
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null && data != null){
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                        ImageView imageView = new ImageView(getApplicationContext());
                                        imageView.setImageBitmap(bitmap);

                                        LinearLayout.LayoutParams imageviewDimens = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                        imageviewDimens.topMargin = 10;
                                        imageView.setLayoutParams(imageviewDimens);

                                        imageView.setAdjustViewBounds(true);
                                        linearLayout.addView(imageView);

                                        TextView dateTextView = new TextView(getApplicationContext());
                                        dateTextView.setTextColor(Color.BLACK);
                                        dateTextView.setText("Posted at " + new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(fileDate));
                                        LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                        layoutParamsTextView.bottomMargin = 150;
                                        dateTextView.setLayoutParams(layoutParamsTextView);
                                        linearLayout.addView(dateTextView);

                                        View horizontalRule = new View(getApplicationContext());
                                        horizontalRule.setLayoutParams(new LinearLayout.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT
                                        ));
                                        horizontalRule.setMinimumHeight(4);
                                        horizontalRule.setBackgroundColor(Color.DKGRAY);
                                        linearLayout.addView(horizontalRule);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

    }
}
