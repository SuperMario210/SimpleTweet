package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.GlideApp;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.etStatus) EditText etStatus;
    @BindView(R.id.btnTweet) TextView btnTweet;
    @BindView(R.id.btnCancel) ImageView btnCancel;
    @BindView(R.id.tvRemainingCharacters) TextView tvRemainingCharacters;
    @BindView(R.id.tvInReplyTo) TextView tvInReplyTo;

    TwitterClient client;
    boolean isReply;
    long replyId;
    String replyScreenName;

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

        replyScreenName = getIntent().getStringExtra("replyScreenName");
        replyId = getIntent().getLongExtra("replyTweetId", 0);
        isReply = replyScreenName != null;

        if(isReply) {
            btnTweet.setText("Reply");
            etStatus.setHint("Tweet your reply");
            tvInReplyTo.setVisibility(View.VISIBLE);
            SpannableStringBuilder builder = new SpannableStringBuilder(String.format("Replying to @%s", replyScreenName));

            int color = ContextCompat.getColor(this, R.color.link);
            builder.setSpan(new ForegroundColorSpan(color), 12, builder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            tvInReplyTo.setText(builder);
        } else {
            tvInReplyTo.setVisibility(View.GONE);
        }

        GlideApp.with(this).load(R.drawable.avatar).transform(new CircleCrop()).into(ivProfileImage);
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void updateStatus(View view) {
        Log.d("ComposeActivity", "updateStatus()");
        String status = etStatus.getText().toString();

        if(isReply) {
            status = String.format("@%s %s", replyScreenName, status);
        }

        client.updateStatus(status, replyId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("ComposeActivity", response.toString());

                try {
                    Tweet tweet = Tweet.fromJson(response);
                    TwitterApp app = (TwitterApp) ComposeActivity.this.getApplication();
                    int position = app.getTweetDataHolder().addTweet(tweet, true);

                    Intent i = new Intent();
                    i.putExtra("position", position);
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
