package com.ziyin.rabbitmqapi.message;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;

public class Consumer {

	public static void main(String[] args) throws Exception {
		
		//1 创建一个ConnectionFactory, 并进行配置
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.174.131");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		//2 通过连接工厂创建连接
		Connection connection = connectionFactory.newConnection();
		
		//3 通过connection创建一个Channel
		Channel channel = connection.createChannel();
		
		//4 声明（创建）一个队列
		String queueName = "test001";
		channel.queueDeclare(queueName, true, false, false, null);

		DefaultConsumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String bodyStr = StrUtil.str(body, CharsetUtil.UTF_8);
				Console.log("消费端: " + bodyStr);
				Map<String, Object> headers = properties.getHeaders();
				System.err.println("headers get my1 value: " + headers.get("my1"));
			}
		};

		//参数：队列名称、是否自动ACK、Consumer
		channel.basicConsume(queueName, true, consumer);
		
	/*	//5 创建消费者
		QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
		
		//6 设置Channel
		channel.basicConsume(queueName, true, queueingConsumer);
		
		while(true){
			//7 获取消息
			Delivery delivery = queueingConsumer.nextDelivery();
			String msg = new String(delivery.getBody());
			System.err.println("消费端: " + msg);
			Map<String, Object> headers = delivery.getProperties().getHeaders();
			System.err.println("headers get my1 value: " + headers.get("my1"));
			
			//Envelope envelope = delivery.getEnvelope();
		}*/
		
	}
}
