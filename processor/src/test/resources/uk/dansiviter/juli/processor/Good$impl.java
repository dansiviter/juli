package uk.dansiviter.juli.processor;

import java.lang.Override;
import java.lang.String;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import javax.annotation.processing.Generated;
import uk.dansiviter.juli.BaseLog;
import uk.dansiviter.juli.annotations.Log;
import uk.dansiviter.juli.annotations.Message;

@Generated(
    value = "uk.dansiviter.juli.processor.LogProcessor",
    comments = "https://juli.dansiviter.uk/"
)
public final class Good$impl implements BaseLog, Good {
  private static final AtomicBoolean ONCE__foo = new AtomicBoolean();

  private final Log log;

  public final String key;

  private final Logger delegate;

  public Good$impl(String name, String key) {
    this.log = Good.class.getAnnotation(Log.class);
    this.key = key;
    this.delegate = delegate(name);
  }

  /**
   * @returns the delegate logger.
   */
  @Override
  public final Logger delegate() {
    return this.delegate;
  }

  /**
   * @returns the annotation instance.
   */
  @Override
  public final Log log() {
    return this.log;
  }

  @Override
  public void foo(String world) {
    if (!isLoggable(Message.Level.INFO)) {
      return;
    }
    if (ONCE__foo.getAndSet(true)) {
      return;
    }
    logp(Message.Level.INFO, "hello {0}", world);
  }
}
