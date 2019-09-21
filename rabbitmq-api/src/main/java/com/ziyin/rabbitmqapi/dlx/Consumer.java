package com.ziyin.rabbitmqapi.dlx;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author ziyin
 * @create 2019-09-13 8:17
 */
public class Consumer {
	public static void main(String[] args) throws IOException, TimeoutException {
		// 创建ConnectionFactory 并进行配置
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.174.131");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		// 通过连接工厂创建连接
		Connection connection = connectionFactory.newConnection();
		// 通过connection创建一个channel
		Channel channel = connection.createChannel();

		// 声明一个队列
		// exclusive:独占,true-->表示这个队列只有一个channel去监听,保证消息的顺序消费,
		// autoDelete: 队列和exchange没有绑定关系,就会自动删除

		// 正常队列,交换机的声明
		String exchangeName = "test_dlx_exchange";
		String routingKey = "dlx.#";
		String queueName = "test_dlx_queue";
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);

		Map<String,Object> arguments = new HashMap<String,Object>();
		// 死信交换机名字可以自己取
		arguments.put("x-dead-letter-exchange","dlx.exchange");
		// arguments要声明的队列上
		channel.queueDeclare(queueName,true,false,false,arguments);
		channel.queueBind(queueName, exchangeName, routingKey);

		// 死信队列的声明
		channel.exchangeDeclare("dlx.exchange", "topic", true, false, false, null);
		channel.queueDeclare("dlx.queue",true,false,false,null);
		channel.queueBind("dlx.queue", "dlx.exchange", "#");

		DefaultConsumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				// 获取消息
				String bodyStr = StrUtil.str(body, CharsetUtil.UTF_8);
				Console.log("消费端: " + bodyStr);
				// 消息唯一性处理或者消息签收的时候的都会用到deliveryTag
				// deliveryTag 每个消费者开始监听,deliveryTag从1开始增长, 重启回归1开始
				long deliveryTag = envelope.getDeliveryTag();
				Console.log("deliveryTag: " + deliveryTag);

				/**
				 * 手动签收消息
				 * multiple: 是否批量签收, 这里prefetchCount设置为1
				 * 			所以没有必要批量签收,如果大于1,可以批量签收
				 */
				channel.basicAck(deliveryTag,false);
			}
		};

		/**
		 * 限流方式:
		 * 1. 必须把autoAck设置为false
		 * 2. channel.basicAck 手动签收消息
		 */
		channel.basicQos(0,1,false);
		// rabbitmq Broker 发送给消费者消息后, 需消费者发送ack确认消息, 确认后会从队列中删除消息
		channel.basicConsume(queueName,false,consumer);

//		channel.close();
//		connection.close();
	}
}
