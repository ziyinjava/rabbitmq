package com.ziyin.rabbitmqapi.confirm;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author ziyin
 * @create 2019-09-13 19:52
 */
public class Producer {
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

		// 指定消息投递模式, 消息的确认模式
		channel.confirmSelect();

		String exchangeName = "test_confirm_exchange";
		String routingKey = "confirm.save";

		// 发送消息
		String msg = "send confirm message";
		channel.basicPublish(exchangeName, routingKey,null,StrUtil.bytes(msg, CharsetUtil.UTF_8));

		//添加确认监听
		channel.addConfirmListener(new ConfirmListener() {


			/**
			 *StrUtil.bytes("hello rabbitmq" + i, CharsetUtil.UTF_8)
			 * @param deliveryTag 消息的唯一标识
			 * @param multiple
			 * @throws IOException
			 */
			@Override
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				System.out.println("-------------ack-----------------");
			}

			// 磁盘写满,rabbitmq异常,queue容量达到上限,都有可能nack
			// 如果因为网络原因,导致生产者连nack都没有收到,就要到消息表中,定时抓取中间状态的消息重发
			@Override
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				System.out.println("-------------no ack-----------------");
			}
		});

	}
}
