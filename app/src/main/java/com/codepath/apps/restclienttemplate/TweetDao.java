package com.codepath.apps.restclienttemplate;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.apps.restclienttemplate.models.SampleModel;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.List;

@Dao
public interface TweetDao {
    @Query("SELECT " +
                "Tweet.body as tweet_body, " +
                "Tweet.createdAt as tweet_createdAt, " +
                "Tweet.id as tweet_id, user.*" +
            " FROM Tweet " +
                "INNER JOIN User " +
            "ON Tweet.userId = User.id " +
            "ORDER BY createdAt " +
            "DESC LIMIT 10")
    List<TweetWithUser> recentItems();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);



}
