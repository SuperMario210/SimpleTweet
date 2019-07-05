package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetViewHolder;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    Tweet mTweet;
    TwitterClient mClient = TwitterApp.getRestClient(this);

//    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
//    @BindView(R.id.tvUserName) TextView tvUserName;
//    @BindView(R.id.tvScreenName) TextView tvScreenName;
//    @BindView(R.id.tvBody) TextView tvBody;
//    @BindView(R.id.tvTime) TextView tvTime;
//    @BindView(R.id.tvDate) TextView tvDate;
//    @BindView(R.id.ivMedia) ImageView ivMedia;
//    @BindView(R.id.ivFavoriteAnim) ImageView ivFavoriteAnim;
//    @BindView(R.id.ivFavorite) ImageView ivFavorite;
//    @BindView(R.id.tvFavorite) TextView tvFavorite;
//    @BindView(R.id.ivRetweet) ImageView ivRetweet;
//    @BindView(R.id.tvRetweet) TextView tvRetweet;
//    @BindView(R.id.ivShare) ImageView ivShare;
//
//    @BindView(R.id.ivRetweeted) ImageView ivRetweeted;
//    @BindView(R.id.tvRetweeted) TextView tvRetweeted;
//    @BindView(R.id.tvInReplyTo) TextView tvInReplyTo;
//    @BindView(R.id.ivReply) ImageView ivReply;
//    @BindView(R.id.ivVerified) ImageView ivVerified;
    @BindView(R.id.root) View root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        long tweetUid = getIntent().getLongExtra("tweetUid", 0);
        mTweet = ((TwitterApp) getApplication()).getTweetDataHolder().getTweetByUid(tweetUid);

        ButterKnife.bind(this);

//        Toolbar myToolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(myToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        if(mTweet.isRetweet) {
//            ivRetweeted.setVisibility(View.VISIBLE);
//            tvRetweeted.setVisibility(View.VISIBLE);
//            tvRetweeted.setText(String.format("%s Retweeted", mTweet.retweeter.screenName));
//        }
//
//        tvUserName.setText(mTweet.user.name);
//        tvScreenName.setText(String.format(Locale.getDefault(), "@%s", mTweet.user.screenName));
//        tvBody.setText(mTweet.getBody(this));
//        tvBody.setMovementMethod(LinkMovementMethod.getInstance());
//        tvFavorite.setText(String.format(Locale.getDefault(), "%d", mTweet.favorites));
//        tvRetweet.setText(String.format(Locale.getDefault(), "%d", mTweet.retweets));
//        tvTime.setText(Tweet.getTime(mTweet.createdAt));
//        tvDate.setText(Tweet.getDate(mTweet.createdAt));
//
//        if(mTweet.isReply) {
//            tvInReplyTo.setVisibility(View.VISIBLE);
//            SpannableStringBuilder builder = new SpannableStringBuilder(String.format("Replying to @%s", mTweet.replyToUserName));
//
//            int color = ContextCompat.getColor(this, R.color.link);
//            builder.setSpan(new ForegroundColorSpan(color), 12, builder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//
//            tvInReplyTo.setText(builder);
//        } else {
//            tvInReplyTo.setVisibility(View.GONE);
//        }
//
//        if(mTweet.isFavorited) {
//            ivFavoriteAnim.setVisibility(View.VISIBLE);
//            ivFavoriteAnim.setImageResource(R.drawable.tile028);
//            ivFavorite.setAlpha(0f);
//        } else {
//            ivFavoriteAnim.setVisibility(View.INVISIBLE);
//            ivFavorite.setAlpha(1f);
//            ivFavorite.setImageResource(R.drawable.ic_vector_heart);
//        }
//
//        if(mTweet.isRetweeted) {
//            ivRetweet.setImageResource(R.drawable.ic_vector_retweet_highlight);
//        } else {
//            ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
//        }
//
//        if(mTweet.user.isVerified) {
//            ivVerified.setVisibility(View.VISIBLE);
//        } else {
//            ivVerified.setVisibility(View.GONE);
//        }
//
//        ivFavorite.setOnClickListener((View v) -> {
//            if(!mTweet.isFavorited) {
//                ivFavorite.setAlpha(0f);
//                ivFavoriteAnim.setVisibility(View.VISIBLE);
//                mTweet.favorites++;
//                tvFavorite.setText(String.format(Locale.getDefault(), "%d", mTweet.favorites));
//                ivFavoriteAnim.setImageResource(R.drawable.anim_heart);
//                AnimationDrawable favAnim = (AnimationDrawable) ivFavoriteAnim.getDrawable();
//                favAnim.start();
//                mTweet.favorite(mClient);
//                mTweet.isFavorited = true;
//            } else {
//                ivFavoriteAnim.setVisibility(View.INVISIBLE);
//                ivFavorite.setAlpha(1f);
//                ivFavorite.setImageResource(R.drawable.ic_vector_heart);
//                mTweet.favorites--;
//                tvFavorite.setText(String.format(Locale.getDefault(), "%d", mTweet.favorites));
//                mTweet.unfavorite(mClient);
//                mTweet.isFavorited = false;
//            }
//        });
//
//        ivRetweet.setOnClickListener((View v) -> {
//            if(!mTweet.isRetweeted) {
//                mTweet.retweets++;
//                tvRetweet.setText(String.format(Locale.getDefault(), "%d", mTweet.retweets));
//                ivRetweet.setImageResource(R.drawable.ic_vector_retweet_highlight);
//                mTweet.retweet(mClient);
//                mTweet.isRetweeted = true;
//            } else {
//                mTweet.retweets--;
//                tvRetweet.setText(String.format(Locale.getDefault(), "%d", mTweet.retweets));
//                ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
//                mTweet.unRetweet(mClient);
//                mTweet.isRetweeted = false;
//            }
//        });
//
//        ivShare.setOnClickListener((View v) -> {
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, mTweet.getUrl());
//            startActivity(Intent.createChooser(shareIntent, "Share link using"));
//        });
//
//        ivReply.setOnClickListener((View v) -> {
//            Intent i = new Intent(DetailActivity.this, ComposeActivity.class);
//            i.putExtra("replyScreenName", mTweet.user.screenName);
//            i.putExtra("replyTweetId", mTweet.uid);
//            startActivity(i);
//        });
//
//        GlideApp.with(this)
//                .load(mTweet.user.profileImageUrl)
//                .transform(new CircleCrop())
//                .error(R.drawable.avatar)
//                .placeholder(R.drawable.avatar)
//                .into(ivProfileImage);
//
//        if(!mTweet.media.isEmpty()) {
//            ivMedia.setVisibility(View.VISIBLE);
//            Media media = mTweet.media.get(0);
//
//            GlideApp.with(this)
//                    .load(media.mediaUrl)
//                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(100)))
//                    .into(ivMedia);
//        }
        TweetViewHolder viewHolder = new TweetViewHolder(root);
        viewHolder.bindTweet(mTweet, this, mClient, false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
