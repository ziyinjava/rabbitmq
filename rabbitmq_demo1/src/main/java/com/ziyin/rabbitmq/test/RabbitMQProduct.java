package com.ziyin.rabbitmq.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * @author ziyin
 * @create 2019-08-17 22:02
 */
public class RabbitMQProduct {

	private static final String QUEUE_NAME="demo.queue";
	private static final String EXCHANGE_NAME="demo.exchange";

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory (); // 连接工厂
		// 配置连接参数  信息
		factory.setUsername("rabbitstudy");
		factory.setPassword("123456");
		factory.setHost("127.0.0.1");
		factory.setPort(5672);
		Connection connection = factory.newConnection (); // 创建连接
		Channel channel = connection.createChannel (); // 创建信道 在信道上传递消息
		channel.exchangeDeclare (EXCHANGE_NAME, "direct"); // 创建交换器
		channel.queueDeclare (QUEUE_NAME, false, false, false, null); // 创建队列
		channel.queueBind (QUEUE_NAME, EXCHANGE_NAME, "ai.yunxi.Five"); // 通过路由键绑定交换器与队列
		// 发送消息给 RabbitMQ
		channel.basicPublish(EXCHANGE_NAME, "ai.yunxi.Five",
				MessageProperties.PERSISTENT_TEXT_PLAIN,
				"Hello 小五！".getBytes ());
		// 关闭资源
		channel.close();
		connection.close();
	}
}
