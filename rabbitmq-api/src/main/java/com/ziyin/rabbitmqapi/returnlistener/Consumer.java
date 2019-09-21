package com.ziyin.rabbitmqapi.returnlistener;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author ziyin
 * @create 2019-09-13 19:52
 */
public class Consumer {
	public static void main(String[] args) throws IOException, TimeoutException {
		//1 创建一个ConnectionFactory, 并进行配置
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.174.131");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");

		//2 通过连接工厂创建连接
		Connection connection = connectionFactory.newConnection();

		//3.通过Connection创建channel
		Channel channel = connection.createChannel();


		String exchangeName = "test_return_exchange";
		String routingKey = "return.#";
		String routingKeyError = "abc.save";
		String queueName = "test_return_queue";

		// 声明交换机和队列,绑定路由key
		channel.exchangeDeclare(exchangeName, "topic", true);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);

		DefaultConsumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String msg = StrUtil.str(body, CharsetUtil.UTF_8);
				Console.log("消费消息: " + msg);
			}
		};

		channel.basicConsume(queueName, true, consumer);

	}
}
