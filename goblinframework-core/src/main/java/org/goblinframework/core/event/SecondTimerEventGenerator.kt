package org.goblinframework.core.event

import org.goblinframework.api.core.Singleton
import org.goblinframework.api.event.EventBus
import org.goblinframework.core.schedule.CronConstants
import org.goblinframework.core.schedule.CronTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

@Singleton
class SecondTimerEventGenerator private constructor() : CronTask {

  companion object {
    @JvmField val INSTANCE = SecondTimerEventGenerator()
  }

  private val sequence = AtomicLong()

  override fun name(): String {
    return "SecondTimerEventGenerator"
  }

  override fun cronExpression(): String {
    return CronConstants.SECOND_TIMER
  }

  override fun concurrent(): Boolean {
    return true
  }

  override fun flight(): Boolean {
    return false
  }

  override fun execute() {
    val next = sequence.getAndIncrement()
    val event = GoblinTimerEvent(TimeUnit.SECONDS, next)
    EventBus.publish("/goblin/timer", event)
  }
}