package org.goblinframework.core.serialization

import org.bson.types.ObjectId
import org.junit.Assert.assertEquals
import org.junit.Test
import org.slf4j.LoggerFactory
import java.time.Instant

abstract class SerializerTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  abstract fun serializer(): Serializer

  @Test
  fun objectId() {
    val serializer = serializer()
    val before = ObjectId()
    val bs = serializer.serialize(before)
    logger.debug("${ObjectId::class.java.name} serialized: ${bs.size}")
    val after = serializer.deserialize(bs) as ObjectId
    assertEquals(before, after)
  }

  @Test
  fun instant() {
    val serializer = serializer()
    val before = Instant.now()
    val bs = serializer.serialize(before)
    logger.debug("${Instant::class.java.name} serialized: ${bs.size}")
    val after = serializer.deserialize(bs) as Instant
    assertEquals(before.toEpochMilli(), after.toEpochMilli())
  }
}