package com.ziyin.rabbitmqapi.exchange.direct;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer4DirectExchange {

	public static void main(String[] args) throws Exception {
		
		
        ConnectionFactory connectionFactory = new ConnectionFactory() ;  
        
        connectionFactory.setHost("192.168.174.131");
        connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		// 设置自动重连
        connectionFactory.setAutomaticRecoveryEnabled(true);
        // 每三秒自动重连
        connectionFactory.setNetworkRecoveryInterval(3000);
        Connection connection = connectionFactory.newConnection();
        
        Channel channel = connection.createChannel();  
		//4 声明
		String exchangeName = "test_direct_exchange";
		String exchangeType = "direct";
		String queueName = "test_direct_queue";
		String routingKey = "test.direct";
		
		//表示声明了一个交换机
		channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
		//表示声明了一个队列
		channel.queueDeclare(queueName, false, false, false, null);
		//建立一个绑定关系:
		channel.queueBind(queueName, exchangeName, routingKey);
		
        //durable 是否持久化消息
        DefaultConsumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String bodyStr = StrUtil.str(body, CharsetUtil.UTF_8);
				Console.log("消费端: " + bodyStr);
			}
		};
        //参数：队列名称、是否自动ACK、Consumer
        channel.basicConsume(queueName, true, consumer);


		/**
		 * 使用QueueingConsumer的方式, 4.x的时候该方式废弃,推荐使用继承DefaultConsumer方式
		//循环获取消息
		while(true){
            //获取消息，如果没有消息，这一步将会一直阻塞  
            Delivery delivery = consumer.nextDelivery();  
            String msg = new String(delivery.getBody());
            System.out.println("收到消息：" + msg);
        }
		 */
	}
}
