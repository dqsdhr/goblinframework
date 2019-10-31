package org.goblinframework.database.mongo.reactor;

import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.extra.processor.TopicProcessor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

final public class SingleResultPublisher<T> implements Publisher<T> {

  private final AtomicReference<TopicProcessor<T>> topic = new AtomicReference<>();
  private final Flux<T> flux;
  private final AtomicBoolean completed = new AtomicBoolean();

  public SingleResultPublisher() {
    TopicProcessor<T> processor = TopicProcessor.create();
    this.topic.set(processor);
    this.flux = Flux.from(processor).publishOn(MongoSchedulerManager.INSTANCE.getScheduler());
  }

  public void complete(@Nullable T value, @Nullable Throwable cause) {
    TopicProcessor<T> processor = this.topic.getAndSet(null);
    if (processor != null) {
      if (cause != null) {
        processor.onError(cause);
        return;
      }
      if (value != null) {
        processor.onNext(value);
      }
      processor.onComplete();
    }
  }

  @Override
  public void subscribe(Subscriber<? super T> s) {
    Flux.from(flux).subscribe(s);
  }
}