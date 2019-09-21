package com.ziyin.rabbitmq.springboot.consumer.conusmer;

import cn.hutool.core.lang.Console;
import com.rabbitmq.client.Channel;
import com.ziyin.rabbitmq.entity.Order;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class RabbitReceiver {

	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "queue-1",durable = "true"),
			exchange = @Exchange(value = "exchange-1", durable = "true", type = "topic", ignoreDeclarationExceptions = "true"),
			key = "springboot.*"
	))
	@RabbitHandler
	public void onMessage(Message message, Channel channel) throws IOException {
		Console.error("消费端收到消费体内容: {}",message.getPayload());
		Long deliverTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
		Console.error("DELIVERY_TAG: {}", deliverTag);
		// 手工ACK
		channel.basicAck(deliverTag,false);
	}


	/**
	 * spring.rabbitmq.listener.order.queue.name=queue-2
	 * spring.rabbitmq.listener.order.queue.durable=true
	 * spring.rabbitmq.listener.order.exchange.name=exchange-2
	 * spring.rabbitmq.listener.order.exchange.durable=true
	 * spring.rabbitmq.listener.order.exchange.type=topic
	 * spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions=true
	 * spring.rabbitmq.listener.order.key=springboot.*
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",durable = "${spring.rabbitmq.listener.order.queue.durable}"),
			exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}", durable = "${spring.rabbitmq.listener.order.exchange.durable}", type = "${spring.rabbitmq.listener.order.exchange.type}", ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
			key = "${spring.rabbitmq.listener.order.key}"
	))
	@RabbitHandler
	public void onOrderMessage(@Payload Order order, @Headers Map<String,Object> headers, Channel channel) throws IOException {
		Console.error("消费端收到消费体内容: {}",order);
		Long deliverTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
		Console.error("DELIVERY_TAG: {}", deliverTag);
		// 手工ACK
		channel.basicAck(deliverTag,false);
	}

}
