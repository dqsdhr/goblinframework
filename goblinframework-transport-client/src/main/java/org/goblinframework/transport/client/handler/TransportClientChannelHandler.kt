package org.goblinframework.transport.client.handler

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.goblinframework.transport.client.manager.TransportClientImpl
import org.slf4j.LoggerFactory

class TransportClientChannelHandler(private val client: TransportClientImpl)
  : SimpleChannelInboundHandler<Any>() {

  companion object {
    private val logger = LoggerFactory.getLogger(TransportClientChannelHandler::class.java)
  }

  override fun channelRead0(ctx: ChannelHandlerContext, msg: Any) {

  }

  override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable?) {
    logger.error("Exception caught at channel {}", ctx.channel(), cause)
    ctx.close()
  }
}