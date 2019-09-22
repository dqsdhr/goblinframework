package org.goblinframework.core.util;

import org.goblinframework.api.annotation.Singleton;
import org.goblinframework.api.annotation.ThreadSafe;
import org.goblinframework.api.service.GoblinServiceException;
import org.goblinframework.api.service.IServiceInstaller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Singleton
@ThreadSafe
final class ServiceLoaderImpl implements IServiceInstaller {

  static final ServiceLoaderImpl INSTANCE = new ServiceLoaderImpl();

  private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private static final Map<Class<?>, Object> buffer = new IdentityHashMap<>(64);

  @NotNull
  @Override
  @SuppressWarnings("unchecked")
  public <E> List<E> asList(@NotNull Class<E> serviceType) {
    if (!serviceType.isInterface()) {
      throw new GoblinServiceException("Service type must be interface");
    }
    lock.readLock().lock();
    try {
      Object cached = buffer.get(serviceType);
      if (cached != null) {
        return (List<E>) cached;
      }
    } finally {
      lock.readLock().unlock();
    }

    lock.writeLock().lock();
    try {
      Object cached = buffer.get(serviceType);
      if (cached != null) {
        return (List<E>) cached;
      }
      List<E> installed = new LinkedList<>();
      ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
      ServiceLoader.load(serviceType, classLoader).forEach(installed::add);
      installed.sort(Comparator.comparingInt(ObjectUtils::calculateOrder));
      installed = Collections.unmodifiableList(installed);
      buffer.put(serviceType, installed);
      return installed;
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Nullable
  @Override
  public <E> E firstOrNull(@NotNull Class<E> serviceType) {
    return asList(serviceType).stream().findFirst().orElse(null);
  }
}