package org.goblinframework.cache.core.cache

import java.lang.management.PlatformManagedObject

interface CacheBuilderMXBean : PlatformManagedObject {

  fun getCacheSystem(): CacheSystem

  fun getCacheList(): Array<CacheMXBean>

}