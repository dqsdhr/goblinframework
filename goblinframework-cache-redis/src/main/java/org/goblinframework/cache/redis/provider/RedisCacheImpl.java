package org.goblinframework.cache.redis.provider;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.SetArgs;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.async.RedisKeyAsyncCommands;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import org.goblinframework.cache.core.cache.*;
import org.goblinframework.cache.redis.client.RedisClient;
import org.goblinframework.core.exception.GoblinExecutionException;
import org.goblinframework.core.exception.GoblinInterruptedException;
import org.goblinframework.core.mbean.GoblinManagedBean;
import org.goblinframework.core.util.NumberUtils;
import org.goblinframework.core.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ExecutionException;

@GoblinManagedBean(type = "CACHE.REDIS")
final class RedisCacheImpl extends GoblinCacheImpl {

  private final RedisClient client;

  RedisCacheImpl(@NotNull String name, @NotNull RedisClient client) {
    super(new CacheSystemLocation(CacheSystem.RDS, name));
    this.client = client;
  }

  void destroy() {
    unregisterIfNecessary();
    logger.debug("REDIS cache [{}] closed", getName());
  }

  @NotNull
  @Override
  public RedisClient getNativeCache() {
    return client;
  }

  @NotNull
  @Override
  public <T> GetResult<T> get(@Nullable String key) {
    if (key == null) {
      return new GetResult<>(null);
    }
    RedisStringAsyncCommands<String, Object> commands = client.getRedisCommands().async().getRedisStringAsyncCommands();
    RedisFuture<Object> future = commands.get(key);
    Object cached;
    try {
      cached = future.get();
    } catch (InterruptedException ex) {
      throw new GoblinInterruptedException(ex);
    } catch (ExecutionException ex) {
      logger.error("RDS.get({})", key, ex);
      throw new GoblinExecutionException(ex);
    }
    if (cached == null) {
      return new GetResult<>(key);
    }
    GetResult<T> gr = new GetResult<>(key);
    gr.cas = 0;
    gr.hit = true;
    if (cached instanceof CacheValueWrapper) {
      gr.wrapper = true;
      gr.uncheckedSetValue(((CacheValueWrapper) cached).getValue());
    } else {
      gr.uncheckedSetValue(cached);
    }
    return gr;
  }

  @NotNull
  @Override
  public <T> Map<String, GetResult<T>> gets(@Nullable Collection<String> keys) {
    if (keys == null || keys.isEmpty()) {
      return Collections.emptyMap();
    }
    String[] ids = keys.stream().distinct().toArray(String[]::new);
    RedisStringAsyncCommands<String, Object> commands = client.getRedisCommands().async().getRedisStringAsyncCommands();
    RedisFuture<List<KeyValue<String, Object>>> future = commands.mget(ids);
    List<KeyValue<String, Object>> kvs;
    try {
      kvs = future.get();
    } catch (InterruptedException ex) {
      throw new GoblinInterruptedException(ex);
    } catch (ExecutionException ex) {
      logger.error("RDS.gets({})", StringUtils.join(ids, " "), ex);
      throw new GoblinExecutionException(ex);
    }
    Map<String, GetResult<T>> result = new LinkedHashMap<>();
    for (KeyValue<String, Object> kv : kvs) {
      String id = kv.getKey();
      if (!kv.hasValue()) {
        result.put(id, new GetResult<>(id));
      } else {
        Object cached = kv.getValue();
        GetResult<T> gr = new GetResult<>(id);
        gr.cas = 0;
        gr.hit = true;
        if (cached instanceof CacheValueWrapper) {
          gr.wrapper = true;
          gr.uncheckedSetValue(((CacheValueWrapper) cached).getValue());
        } else {
          gr.uncheckedSetValue(cached);
        }
        result.put(id, gr);
      }
    }
    return result;
  }

  @Override
  public boolean delete(@Nullable String key) {
    if (key == null) {
      return false;
    }
    RedisKeyAsyncCommands<String, Object> commands = client.getRedisCommands().async().getRedisKeyAsyncCommands();
    RedisFuture<Long> future = commands.del(key);
    Long count;
    try {
      count = future.get();
    } catch (InterruptedException ex) {
      throw new GoblinInterruptedException(ex);
    } catch (ExecutionException ex) {
      logger.error("RDS.delete({})", key, ex);
      throw new GoblinExecutionException(ex);
    }
    return count != null && count > 0;
  }

  @Override
  public <T> boolean add(@Nullable String key, int expirationInSeconds, @Nullable T value) {
    if (key == null || expirationInSeconds < 0 || value == null) {
      return false;
    }
    RedisStringAsyncCommands<String, Object> commands = client.getRedisCommands().async().getRedisStringAsyncCommands();
    boolean ret;
    if (expirationInSeconds > 0) {
      SetArgs args = new SetArgs().ex(expirationInSeconds).nx();
      String response;
      try {
        response = commands.set(key, value, args).get();
      } catch (InterruptedException ex) {
        throw new GoblinInterruptedException(ex);
      } catch (ExecutionException ex) {
        logger.error("RDS.add({})", key, ex);
        throw new GoblinExecutionException(ex);
      }
      ret = "OK".equalsIgnoreCase(response);
    } else {
      try {
        ret = commands.setnx(key, value).get();
      } catch (InterruptedException ex) {
        throw new GoblinInterruptedException(ex);
      } catch (ExecutionException ex) {
        logger.error("RDS.add({})", key, ex);
        throw new GoblinExecutionException(ex);
      }
    }
    return ret;
  }

  @Override
  public <T> boolean set(@Nullable String key, int expirationInSeconds, @Nullable T value) {
    if (key == null || expirationInSeconds < 0 || value == null) {
      return false;
    }
    RedisStringAsyncCommands<String, Object> commands = client.getRedisCommands().async().getRedisStringAsyncCommands();
    String ret;
    if (expirationInSeconds > 0) {
      try {
        ret = commands.setex(key, expirationInSeconds, value).get();
      } catch (InterruptedException ex) {
        throw new GoblinInterruptedException(ex);
      } catch (ExecutionException ex) {
        logger.error("RDS.set({})", key, ex);
        throw new GoblinExecutionException(ex);
      }
    } else {
      try {
        ret = commands.set(key, value).get();
      } catch (InterruptedException ex) {
        throw new GoblinInterruptedException(ex);
      } catch (ExecutionException ex) {
        logger.error("RDS.set({})", key, ex);
        throw new GoblinExecutionException(ex);
      }
    }
    return "OK".equalsIgnoreCase(ret);
  }

  @Override
  public <T> boolean replace(@Nullable String key, int expirationInSeconds, @Nullable T value) {
    if (key == null || value == null || expirationInSeconds < 0) {
      return false;
    }
    RedisStringAsyncCommands<String, Object> commands = client.getRedisCommands().async().getRedisStringAsyncCommands();
    String ret;
    if (expirationInSeconds > 0) {
      SetArgs args = new SetArgs().ex(expirationInSeconds).xx();
      try {
        ret = commands.set(key, value, args).get();
      } catch (InterruptedException ex) {
        throw new GoblinInterruptedException(ex);
      } catch (ExecutionException ex) {
        logger.error("RDS.replace({})", key, ex);
        throw new GoblinExecutionException(ex);
      }
    } else {
      SetArgs args = new SetArgs().xx();
      try {
        ret = commands.set(key, value, args).get();
      } catch (InterruptedException ex) {
        throw new GoblinInterruptedException(ex);
      } catch (ExecutionException ex) {
        logger.error("RDS.replace({})", key, ex);
        throw new GoblinExecutionException(ex);
      }
    }
    return "OK".equalsIgnoreCase(ret);
  }

  @Override
  public <T> boolean append(@Nullable String key, @Nullable T value) {
    if (key == null || !(value instanceof CharSequence)) {
      return false;
    }
    String s = ((CharSequence) value).toString();
    if (s.isEmpty()) {
      return false;
    }
    RedisStringAsyncCommands<String, Object> commands = client.getRedisCommands().async().getRedisStringAsyncCommands();
    Long ret;
    try {
      ret = commands.append(key, s).get();
    } catch (InterruptedException ex) {
      throw new GoblinInterruptedException(ex);
    } catch (ExecutionException ex) {
      logger.error("RDS.append({})", key, ex);
      throw new GoblinExecutionException(ex);
    }
    return NumberUtils.toLong(ret) > s.length();
  }

  @Nullable
  @Override
  public Boolean touch(@Nullable String key, int expirationInSeconds) {
    if (key == null || expirationInSeconds < 0) {
      return false;
    }
    RedisKeyAsyncCommands<String, Object> commands = client.getRedisCommands().async().getRedisKeyAsyncCommands();
    Boolean ret;
    if (expirationInSeconds == 0) {
      try {
        ret = commands.persist(key).get();
      } catch (InterruptedException ex) {
        throw new GoblinInterruptedException(ex);
      } catch (ExecutionException ex) {
        logger.error("RDS.touch({})", key, ex);
        return null;
      }
    } else {
      try {
        ret = commands.expire(key, expirationInSeconds).get();
      } catch (InterruptedException ex) {
        throw new GoblinInterruptedException(ex);
      } catch (ExecutionException ex) {
        logger.error("RDS.touch({})", key, ex);
        return null;
      }
    }
    return ret;
  }

  @Nullable
  @Override
  public Long ttl(@Nullable String key) {
    if (key == null) {
      return null;
    }
    RedisKeyAsyncCommands<String, Object> commands = client.getRedisCommands().async().getRedisKeyAsyncCommands();
    try {
      return commands.ttl(key).get();
    } catch (InterruptedException ex) {
      throw new GoblinInterruptedException(ex);
    } catch (ExecutionException ex) {
      logger.error("RDS.ttl({})", key, ex);
      return null;
    }
  }

  @Nullable
  @Override
  public Long incr(@Nullable String key, long delta, long initialValue, int expirationInSeconds) {
    if (key == null || expirationInSeconds < 0) {
      return null;
    }
    if (delta < 0) {
      return decr(key, -delta, initialValue, expirationInSeconds);
    }
    long init = initialValue - delta;
    return client.executeTransaction(key, (id, connection) -> {
      connection.sync().multi();

      // execution 1
      if (expirationInSeconds > 0) {
        SetArgs args = new SetArgs().ex(expirationInSeconds).nx();
        connection.sync().set(id, Long.toString(init), args);
      } else {
        connection.sync().setnx(id, Long.toString(init));
      }

      // execution 2
      connection.sync().incrby(id, delta);

      TransactionResult tr = connection.sync().exec();
      if (tr.size() != 2) {
        // should not reach here
        return null;
      }
      Object last = tr.get(1);
      if (!(last instanceof Long)) {
        return null;
      }
      return (Long) last;
    });
  }

  @Nullable
  @Override
  public Long decr(@Nullable String key, long delta, long initialValue, int expirationInSeconds) {
    if (key == null || expirationInSeconds < 0) {
      return null;
    }
    if (delta < 0) {
      return incr(key, -delta, initialValue, expirationInSeconds);
    }
    long init = initialValue + delta;
    return client.executeTransaction(key, (id, connection) -> {
      connection.sync().multi();

      // execution 1
      if (expirationInSeconds > 0) {
        SetArgs args = new SetArgs().nx().ex(expirationInSeconds);
        connection.sync().set(id, Long.toString(init), args);
      } else {
        connection.sync().setnx(id, Long.toString(init));
      }

      // execution 2
      connection.sync().decrby(id, delta);

      TransactionResult tr = connection.sync().exec();
      if (tr.size() != 2) {
        // should not reach here
        return null;
      }
      Object last = tr.get(1);
      if (!(last instanceof Long)) {
        return null;
      }
      return (Long) last;
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Boolean cas(@Nullable String key, int expirationInSeconds,
                         @Nullable GetResult<T> getResult,  /* useless argument */
                         int maxTries, @Nullable CasOperation<T> casOperation) {
    if (key == null || expirationInSeconds < 0 || maxTries < 0 || casOperation == null) {
      return false;
    }
    int tries = 0;
    while (tries <= maxTries) {
      Boolean ret = client.executeTransaction(key, (id, connection) -> {
        connection.sync().watch(id);
        try {
          Object cached = connection.sync().get(id);
          if (cached == null) {
            return false;
          }
          Object current = cached;
          if (cached instanceof CacheValueWrapper) {
            current = ((CacheValueWrapper) cached).getValue();
          }
          Object modified = casOperation.changeCacheObject((T) current);
          if (modified == null) {
            modified = new CacheValueWrapper(null);
          }

          connection.sync().multi();
          if (expirationInSeconds > 0) {
            connection.sync().setex(id, expirationInSeconds, modified);
          } else {
            connection.sync().set(id, modified);
          }
          TransactionResult tr = connection.sync().exec();
          for (Object o : tr) {
            if (o != null && "OK".equalsIgnoreCase(o.toString())) {
              return true;
            }
          }
          return null;
        } finally {
          connection.sync().unwatch();
        }
      });
      if (ret != null) {
        return ret;
      }
      tries++;
    }
    return false;
  }
}