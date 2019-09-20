package org.goblinframework.cache.core.cache;

import org.goblinframework.cache.core.annotation.CacheSystem;
import org.jetbrains.annotations.NotNull;

public interface CacheSystemLocationProvider {

  @NotNull
  CacheSystemLocation getCacheSystemLocation();

  @NotNull
  default CacheSystem getCacheSystem() {
    return getCacheSystemLocation().getSystem();
  }

  @NotNull
  default String getName() {
    return getCacheSystemLocation().getName();
  }
}
