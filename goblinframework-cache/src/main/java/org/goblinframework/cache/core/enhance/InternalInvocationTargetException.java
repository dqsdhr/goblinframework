package org.goblinframework.cache.core.enhance;

import org.goblinframework.api.cache.GoblinCacheException;

public class InternalInvocationTargetException extends GoblinCacheException {
  private static final long serialVersionUID = 7820802986696337666L;

  public InternalInvocationTargetException(Throwable cause) {
    super(cause);
  }
}