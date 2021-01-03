package com.github.adrian83.trends.domain.favorite.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonSerialize
@JsonDeserialize
@Document(collection = FavoriteDoc.COLLECTION)
@Builder
@Data
@ToString
@EqualsAndHashCode
public class FavoriteDoc {

  public static final String COLLECTION = "favorites";

  public static final String ID = "id";
  public static final String TWEET_ID = "tweet_id";
  public static final String USERNAME = "username";
  public static final String UPDATED = "updated";
  public static final String COUNT = "count";

  @Id
  @Field(ID)
  private String id;

  @Field(TWEET_ID)
  private String tweetId;
  
  @Field(USERNAME)
  private String username;

  @Field(COUNT)
  private long count;

  @Field(UPDATED)
  private Long updated;
}
