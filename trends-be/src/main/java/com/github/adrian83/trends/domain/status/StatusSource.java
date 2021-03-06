package com.github.adrian83.trends.domain.status;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.List;
import java.util.Optional;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.adrian83.trends.domain.common.StatusCleaner;
import com.github.adrian83.trends.domain.common.StatusProcessor;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;

@Service
public class StatusSource {

  private static final Logger LOGGER = LoggerFactory.getLogger(StatusSource.class);

  @Autowired private TwitterStream twitterStream;
  @Autowired private List<StatusProcessor> processors;
  @Autowired private List<StatusCleaner> cleaners;

  @Value("${cleaning.olderThanSec}")
  private int cleaningIntervalSec;

  @PreDestroy
  public void disconnect() {

    twitterStream.clearListeners();
    twitterStream.cleanUp();

    LOGGER.info("Twitter client disconnected");
  }

  @PostConstruct
  public void init() {
    processors.forEach(p -> p.processStatusses(twittsFlux()));
  }

  @Scheduled(
      fixedDelayString = "${cleaning.fixedRateMs}",
      initialDelayString = "${cleaning.initialDelayMs}")
  public void clean() {
    cleaners.forEach(c -> c.removeOlderThanSec(cleaningIntervalSec));
  }

  public Flux<Status> twittsFlux() {
    return Flux.from(new StatusPublisher(twitterStream)).subscribeOn(Schedulers.parallel());
  }

  private class StatusPublisher implements Publisher<Status> {

    private TwitterStream twitterStream;

    public StatusPublisher(TwitterStream twitterStream) {
      super();
      this.twitterStream = twitterStream;
    }

    @Override
    public void subscribe(Subscriber<? super Status> subscriber) {
      this.twitterStream.addListener(new TrendsStatusAdapter(subscriber));
    }
  }

  private class TrendsStatusAdapter extends StatusAdapter {

    private Subscriber<? super Status> subscriber;

    public TrendsStatusAdapter(Subscriber<? super Status> subscriber) {
      super();
      this.subscriber = subscriber;
    }

    @Override
    public void onStatus(Status status) {
      Optional.ofNullable(status)
          .filter(s -> this.subscriber != null)
          .ifPresent(s -> this.subscriber.onNext(s));
    }

    @Override
    public void onException(Exception ex) {
      Optional.ofNullable(ex)
          .filter(e -> this.subscriber != null)
          .ifPresent(
              e -> {
                e.printStackTrace();
                this.subscriber.onError(e);
              });
    }
  }
}
