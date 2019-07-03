package com.codepath.apps.restclienttemplate.models;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.etStatus) EditText etStatus;
    @BindView(R.id.btnTweet) TextView btnTweet;
    @BindView(R.id.btnCancel) ImageView btnCancel;
    @BindView(R.id.tvRemainingCharacters) TextView tvRemainingCharacters;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);
        ButterKnife.bind(this);

        etStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int remainingCharacters = 280 - s.toString().length();
                String st = "" + Math.max(0, remainingCharacters);
                tvRemainingCharacters.setText(st);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        GlideApp.with(this).load(R.drawable.avatar).transform(new CircleCrop()).into(ivProfileImage);
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void updateStatus(View view) {
        Log.d("ComposeActivity", "updateStatus()");
        String status = etStatus.getText().toString();

        client.updateStatus(status, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("ComposeActivity", response.toString());

                try {
                    Tweet tweet = Tweet.fromJson(response);

                    Intent i = new Intent();
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    // Activity finished ok, return the data
                    setResult(RESULT_OK, i); // set result code and bundle data for response
                    finish(); // closes the activity, pass data to parent
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ComposeActivity.this, "Couldn't parse tweet", Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(errorResponse != null)
                    Log.d("ComposeActivity", errorResponse.toString(), throwable);
                throwable.printStackTrace();
                Toast.makeText(ComposeActivity.this, "Couldn't post tweet", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if(errorResponse != null)
                    Log.d("ComposeActivity", errorResponse.toString(), throwable);
                throwable.printStackTrace();
                Toast.makeText(ComposeActivity.this, "Couldn't post tweet", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("ComposeActivity", responseString, throwable);
                throwable.printStackTrace();
                Toast.makeText(ComposeActivity.this, "Couldn't post tweet", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }
}
