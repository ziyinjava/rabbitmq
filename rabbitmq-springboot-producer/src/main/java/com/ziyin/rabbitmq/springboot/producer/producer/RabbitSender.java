package com.ziyin.rabbitmq.springboot.producer.producer;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.IdUtil;
import com.ziyin.rabbitmq.entity.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class RabbitSender {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	private final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback(){

		/**
		 * confirm回调函数
		 * @param correlationData
		 * @param ack
		 * @param cause
		 */
		@Override
		public void confirm(CorrelationData correlationData, boolean ack, String cause) {
			Console.error("correlationData : " + correlationData);
			Console.error("ack : " + ack);
			Console.error("cause : " + cause);
			if (!ack) {
				Console.error("异常处理");
			}

			// ack 就更新数据库消息的状态,发送成功
		}
	};

	final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {

		/**
		 * return 回调函数
		 * @param message
		 * @param replyCode
		 * @param replyText
		 * @param exchange
		 * @param routingKey
		 */
		@Override
		public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText, String exchange, String routingKey) {
			Console.error("return exchange: {}, routingKey: {}, replyCode: {}, replyText: {}, message: {}",exchange,routingKey,replyCode,replyText,message);
		}
	};

	/**
	 * 发送消息方法
	 * @param message
	 * @param properties
	 */
	public void send(Object message, Map<String,Object> properties) {
		MessageHeaders messageHeaders = new MessageHeaders(properties);
		Message msg = MessageBuilder.createMessage(message,messageHeaders);
		rabbitTemplate.setConfirmCallback(confirmCallback);
		rabbitTemplate.setReturnCallback(returnCallback);
		CorrelationData correlationData = new CorrelationData();
		correlationData.setId(IdUtil.objectId()); // 消息唯一id 保证全局唯一
		rabbitTemplate.convertAndSend("exchange-1","spring.hello",msg,correlationData);
	}


	public void sendOrder(Order order) {
		rabbitTemplate.setConfirmCallback(confirmCallback);
		rabbitTemplate.setReturnCallback(returnCallback);
		CorrelationData correlationData = new CorrelationData();
		correlationData.setId(IdUtil.objectId()); // 消息唯一id 保证全局唯一
		rabbitTemplate.convertAndSend("exchange-2","springboot.hello",order,correlationData);
	}

}
