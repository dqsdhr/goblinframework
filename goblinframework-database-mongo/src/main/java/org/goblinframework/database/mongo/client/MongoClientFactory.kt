package org.goblinframework.database.mongo.client

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.connection.netty.NettyStreamFactoryFactory
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.goblinframework.core.util.StringUtils
import org.goblinframework.database.mongo.module.config.MongoConfig
import java.util.concurrent.TimeUnit

internal object MongoClientFactory {

  internal fun build(config: MongoConfig): MongoClient {
    val addresses = StringUtils.split(config.getServers(), " ")
        .map { ServerAddress(it) }.toList()
    val builder = MongoClientSettings.builder()
        .applyToClusterSettings {
          it.hosts(addresses)
        }
        .applyToConnectionPoolSettings {
          it.maxSize(config.getMaxSize())
          it.minSize(config.getMinSize())
          it.maxWaitQueueSize(config.getMaxWaitQueueSize())
          it.maxWaitTime(config.getMaxWaitTimeMS(), TimeUnit.MILLISECONDS)
          it.maxConnectionLifeTime(config.getMaxConnectionLifeTimeMS(), TimeUnit.MILLISECONDS)
          it.maxConnectionIdleTime(config.getMaxConnectionIdleTimeMS(), TimeUnit.MILLISECONDS)
          it.maintenanceInitialDelay(config.getMaintenanceInitialDelayMS(), TimeUnit.MILLISECONDS)
          it.maintenanceFrequency(config.getMaintenanceFrequencyMS(), TimeUnit.MILLISECONDS)
        }
    if (config.getStream() == "netty") {
      builder.streamFactoryFactory(NettyStreamFactoryFactory.builder().build())
    }
    config.getCredential()?.run {
      val password = this.mapper.password?.toCharArray() ?: CharArray(0)
      val credential = MongoCredential.createCredential(this.getUsername(), this.getDatabase(), password)
      builder.credential(credential)
    }
    val settings = builder.build()
    return MongoClients.create(settings)
  }
}