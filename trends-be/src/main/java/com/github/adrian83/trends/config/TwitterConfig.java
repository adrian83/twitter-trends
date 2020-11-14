package com.github.adrian83.trends.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

@Configuration
public class TwitterConfig {

  @Value("${twitter.apiKey}")
  private String apiKey;

  @Value("${twitter.apiSecretKey}")
  private String apiSecretKey;

  @Value("${twitter.accessToken}")
  private String accessToken;

  @Value("${twitter.accessTokenSecret}")
  private String accessTokenSecret;

  @Bean
  public TwitterStream createTwitterStream() {

    var twitterStream = new TwitterStreamFactory().getInstance();

    twitterStream.setOAuthConsumer(apiKey, apiSecretKey);
    twitterStream.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));

    twitterStream.addListener(
        new StatusAdapter() {
          @Override
          public void onStatus(Status status) {}

          @Override
          public void onException(Exception ex) {}
        });

    twitterStream.sample();

    return twitterStream;
  }
}
