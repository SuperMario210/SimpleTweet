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

import java.util.Locale;

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

    // Store reply information
    boolean isReply;
    String replyScreenName;
    long replyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);
        ButterKnife.bind(this);

        // Extract reply information from intent
        replyScreenName = getIntent().getStringExtra("replyScreenName");
        replyId = getIntent().getLongExtra("replyTweetId", 0);
        isReply = replyScreenName != null;

        formatView();
    }

    /**
     * Sets up the activity view
     */
    private void formatView() {
        etStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int remChars = 280 - s.toString().length();
                tvRemainingCharacters.setText(String.format(Locale.getDefault(), "%s", remChars));

                // Set text color to red if we are over the character limit
                if(remChars < 0) {
                    tvRemainingCharacters.setTextColor(
                            ContextCompat.getColor(ComposeActivity.this, R.color.favorite));
                } else {
                    tvRemainingCharacters.setTextColor(
                            ContextCompat.getColor(ComposeActivity.this, R.color.dark_gray));
                }

                // Gray out the tweet button if the length of the post is invalid
                if(remChars == 280 || remChars < 0) {
                    btnTweet.setClickable(false);
                    btnTweet.setBackgroundResource(R.drawable.button_disabled);
                } else {
                    btnTweet.setClickable(true);
                    btnTweet.setBackgroundResource(R.drawable.button);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Change styling if the tweet is a reply
        if(isReply) {
            btnTweet.setText("Reply");
            etStatus.setHint("Tweet your reply");
            tvInReplyTo.setVisibility(View.VISIBLE);
            SpannableStringBuilder builder = new SpannableStringBuilder(
                    String.format("Replying to @%s", replyScreenName));
            // Highlight the user name
            builder.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(this, R.color.link)),
                    12,
                    builder.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tvInReplyTo.setText(builder);
        } else {
            tvInReplyTo.setVisibility(View.GONE);
        }

        // Load the user's avatar
        GlideApp.with(this)
                .load(R.drawable.avatar)
                .transform(new CircleCrop())
                .into(ivProfileImage);
    }

    /**
     * Called when the user taps the cancel button
     */
    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Called when the user taps the tweet button.  Makes a network call to post the tweet
     */
    public void updateStatus(View view) {
        String status = etStatus.getText().toString();

        // Add a mention to the original poster if this is a reply
        if(isReply) {
            status = String.format("@%s %s", replyScreenName, status);
        }

        client.updateStatus(status, replyId, new JsonHttpResponseHandler() {
            /**
             * Create a new tweet object from the response json then return to the previous activity
             */
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Create a new tweet object and add it to the database
                    Tweet tweet = Tweet.fromJson(response);
                    TwitterApp app = (TwitterApp) ComposeActivity.this.getApplication();
                    int position = app.getTweetDataHolder().addTweet(tweet, true);

                    // Return to the original activity
                    Intent i = new Intent();
                    i.putExtra("position", position); // Pass the position of the tweet
                    setResult(RESULT_OK, i);
                    finish();
                } catch (JSONException e) {
                    Log.e("ComposeActivity", "Couldn't parse tweet", e);
                    Toast.makeText(ComposeActivity.this, "Couldn't parse tweet", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handleFailure(throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                handleFailure(throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handleFailure(throwable);
            }

            /**
             * Notify the user that the tweet could not be posted
             * @param throwable
             */
            private void handleFailure(Throwable throwable) {
                Log.d("ComposeActivity", "Couldn't post", throwable);
                Toast.makeText(
                        ComposeActivity.this,
                        "Couldn't post tweet",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}
