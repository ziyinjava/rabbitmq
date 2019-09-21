package com.ziyin.rabbitmqapi.quickstart;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author ziyin
 * @create 2019-09-13 8:16
 */
public class Producer {
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
		// 通过channel发送数据  param1: exchange, param2: routingkey param3: properties和boody
		// 消息组成就是properties和body
		for (int i = 0; i < 5; i++) {
			// 如果exchange为空,则默认使用AMQP default Exchange, direct模式, 会拿routingKey去匹配对应名字的队列,
			// 如果匹配不上任何队列就会删除消息
			channel.basicPublish("","test001",null, StrUtil.bytes("hello rabbitmq" + i, CharsetUtil.UTF_8));
		}
		channel.close();
		connection.close();
	}
}
