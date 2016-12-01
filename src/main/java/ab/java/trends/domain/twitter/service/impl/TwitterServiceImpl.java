package ab.java.trends.domain.twitter.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import ab.java.trends.config.TwitterConfig;
import ab.java.trends.domain.twitter.domain.TwitterAuth;
import ab.java.trends.domain.twitter.service.TwitterService;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

@Service
public class TwitterServiceImpl implements TwitterService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterServiceImpl.class);

	private TwitterStream twitterStream;

    @Autowired
    private TwitterConfig twitterConfig;
	
    
	@PostConstruct
	public void connect() {
		
		TwitterAuth auth = twitterConfig.getAuhentication();
		
		AccessToken accessToken = new AccessToken(auth.getToken(), auth.getSecret());

		twitterStream = new TwitterStreamFactory().getInstance();
		
		
		twitterStream.setOAuthConsumer(auth.getCustomerKey(), auth.getCustomerSecret());
		twitterStream.setOAuthAccessToken(accessToken);
		
		twitterStream.addListener(new StatusAdapter());
		
		twitterStream.sample();
		
		LOGGER.info("Twitter client connected");
	}

	@PreDestroy
	public void disconnect() {
		
		twitterStream.clearListeners();
		twitterStream.cleanUp();
		
		LOGGER.info("Twitter client disconnected");
	}

	@Override
	public Observable<Status> getTwitts() {

		return Observable.create(new MyOnSubscribe2(twitterStream));
	}

}


class MyOnSubscribe2 implements OnSubscribe<Status> {

	TwitterStream twitterStream;

	public MyOnSubscribe2(TwitterStream twitterStream) {
		this.twitterStream = twitterStream;
	}

	@Override
	public void call(Subscriber<? super Status> subscriber) {

		twitterStream.addListener(new StatusAdapter() {
			public void onStatus(Status status) {
				subscriber.onNext(status);
			}

			public void onException(Exception ex) {
				subscriber.onError(ex);
			}
		});

	}

}
