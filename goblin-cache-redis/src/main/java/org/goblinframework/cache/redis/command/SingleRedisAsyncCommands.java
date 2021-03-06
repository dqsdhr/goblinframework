package org.goblinframework.cache.redis.command;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.*;
import org.jetbrains.annotations.NotNull;

public class SingleRedisAsyncCommands extends RedisAsyncCommands {

  private final StatefulRedisConnection<String, Object> connection;

  public SingleRedisAsyncCommands(@NotNull StatefulRedisConnection<String, Object> connection) {
    this.connection = connection;
  }

  @Override
  public RedisHashAsyncCommands<String, Object> getRedisHashAsyncCommands() {
    return connection.async();
  }

  @Override
  public RedisKeyAsyncCommands<String, Object> getRedisKeyAsyncCommands() {
    return connection.async();
  }

  @Override
  public RedisStringAsyncCommands<String, Object> getRedisStringAsyncCommands() {
    return connection.async();
  }

  @Override
  public RedisListAsyncCommands<String, Object> getRedisListAsyncCommands() {
    return connection.async();
  }

  @Override
  public RedisSetAsyncCommands<String, Object> getRedisSetAsyncCommands() {
    return connection.async();
  }

  @Override
  public RedisSortedSetAsyncCommands<String, Object> getRedisSortedSetAsyncCommands() {
    return connection.async();
  }

  @Override
  public RedisScriptingAsyncCommands<String, Object> getRedisScriptingAsyncCommands() {
    return connection.async();
  }

  @Override
  public RedisServerAsyncCommands<String, Object> getRedisServerAsyncCommands() {
    return connection.async();
  }

  @Override
  public RedisHLLAsyncCommands<String, Object> getRedisHLLAsyncCommands() {
    return connection.async();
  }

  @Override
  public RedisGeoAsyncCommands<String, Object> getRedisGeoAsyncCommands() {
    return connection.async();
  }
}
