package com.ziyin.rabbitmq.spring;

import cn.hutool.core.util.IdUtil;
import com.ziyin.rabbitmq.spring.adapter.MessageDelegate;
import com.ziyin.rabbitmq.spring.convert.ImageMessageConverter;
import com.ziyin.rabbitmq.spring.convert.PDFMessageConverter;
import com.ziyin.rabbitmq.spring.convert.TextMessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author ziyin
 * @create 2019-09-14 14:54
 */
@Configuration
@ComponentScan({"com.ziyin.rabbitmq.spring.*"})
public class RabbitMqConfig {

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses("192.168.174.131:5672");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setVirtualHost("/");
		return connectionFactory;
	}

	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		rabbitAdmin.setAutoStartup(true);
		return rabbitAdmin;
	}

	/**
	 * 针对消费者配置
	 * 1. 设置交换机类型
	 * 2. 将队列绑定到交换机
	 * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
	 * HeadersExchange ：通过添加属性key-value匹配
	 * DirectExchange:按照routingkey分发到指定队列
	 * TopicExchange:多关键字匹配
	 */
	@Bean
	public TopicExchange exchange001() {
		return new TopicExchange("topic001", true, false);
	}

	@Bean
	public Queue queue001() {
		return new Queue("queue001", true); //队列持久
	}

	@Bean
	public Binding binding001() {
		return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
	}

	@Bean
	public TopicExchange exchange002() {
		return new TopicExchange("topic002", true, false);
	}

	@Bean
	public Queue queue002() {
		return new Queue("queue002", true); //队列持久
	}

	@Bean
	public Binding binding002() {
		return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
	}

	@Bean
	public Queue queue003() {
		return new Queue("queue003", true); //队列持久
	}

	@Bean
	public Binding binding003() {
		return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
	}

	@Bean
	public Queue queue_image() {
		return new Queue("image_queue", true); //队列持久
	}

	@Bean
	public Queue queue_pdf() {
		return new Queue("pdf_queue", true); //队列持久
	}


	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		return rabbitTemplate;
	}


	@Bean
	public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setQueues(queue001(), queue002(), queue003(), queue_image(), queue_pdf());
		container.setConcurrentConsumers(1);
		container.setMaxConcurrentConsumers(5);
		// 不重回队列
		container.setDefaultRequeueRejected(false);
		// 设置签收模式
		container.setAcknowledgeMode(AcknowledgeMode.AUTO);
		container.setExposeListenerChannel(true);
		// 消费端标签策略
		container.setConsumerTagStrategy(new ConsumerTagStrategy() {
			@Override
			public String createConsumerTag(String queue) {
				return queue + "_" + IdUtil.objectId();
			}
		});

		// 注册消息监听器
/*		container.setMessageListener(new ChannelAwareMessageListener() {
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				String msg = StrUtil.str(message.getBody(), CharsetUtil.UTF_8);
				System.out.println("---消费者---: " + msg);
			}
		});*/

		/**
		 *
		 * 1.适配器方式,默认是有自己的方法名字的: handleMessage
		 * 可以指定自己的名字adapter.setDefaultListenerMethod("consumeMessage");
		 * 也可以添加类型转换器: 从字节数组转换为字符串,注意转换规则,
		 *
		 * 消息-->消息转换器-->自定义的handleMessage方法
		 */

	/*	MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
	//	adapter.setDefaultListenerMethod("consumeMessage");

		adapter.setMessageConverter(new MessageConverter() {

			*//**
			 *	Java对象转换为Message对象
			 * @param object
			 * @param messageProperties
			 * @return
			 * @throws MessageConversionException*//*

			@Override
			public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {

				return new Message(StrUtil.bytes(object.toString(), CharsetUtil.UTF_8), messageProperties);
			}

			*//**
			 * Message对象转换为Java对象
			 * @param message
			 * @return
			 * @throws MessageConversionException*//*

			@Override
			public Object fromMessage(Message message) throws MessageConversionException {
				String contentType = message.getMessageProperties().getContentType();
				if (contentType != null && contentType.contains("text")) {
					return StrUtil.str(message.getBody(), CharsetUtil.UTF_8);
				}
				return message.getBody();
			}
		});


		*//**
		 * 2.适配器方式: 我们的队列名称和方法名称也可以一一匹配
		 *//*
		Map<String,String> queueOrTagToMethodName = new HashMap<String,String>();
		queueOrTagToMethodName.put("queue001","method1");
		queueOrTagToMethodName.put("queue002","method2");
		adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);


		*/


		// 1.1 支持json格式的转换器

		/*MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
		adapter.setDefaultListenerMethod("consumeMessage");

		Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
		adapter.setMessageConverter(jackson2JsonMessageConverter);

		container.setMessageListener(adapter);*/



		// 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换

		/* MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
		 adapter.setDefaultListenerMethod("consumeMessage");

		 Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();

		 DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
		 jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);

		 adapter.setMessageConverter(jackson2JsonMessageConverter);
		 container.setMessageListener(adapter);*/


		//1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象多映射转换

//		 MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//		 adapter.setDefaultListenerMethod("consumeMessage");
//		 Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//		 DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
//
//		 Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
//		 idClassMapping.put("order", Order.class);
//		 idClassMapping.put("packaged", Packaged.class);
//
//		 javaTypeMapper.setIdClassMapping(idClassMapping);
//
//		 jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
//		 adapter.setMessageConverter(jackson2JsonMessageConverter);
//		 container.setMessageListener(adapter);

		//1.4 ext convert

		MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
		adapter.setDefaultListenerMethod("consumeMessage");

		//全局的转换器:
		ContentTypeDelegatingMessageConverter convert = new ContentTypeDelegatingMessageConverter();

		TextMessageConverter textConvert = new TextMessageConverter();
		convert.addDelegate("text", textConvert);
		convert.addDelegate("html/text", textConvert);
		convert.addDelegate("xml/text", textConvert);
		convert.addDelegate("text/plain", textConvert);

		Jackson2JsonMessageConverter jsonConvert = new Jackson2JsonMessageConverter();
		convert.addDelegate("json", jsonConvert);
		convert.addDelegate("application/json", jsonConvert);

		ImageMessageConverter imageConverter = new ImageMessageConverter();
		convert.addDelegate("image/png", imageConverter);
		convert.addDelegate("image", imageConverter);

		PDFMessageConverter pdfConverter = new PDFMessageConverter();
		convert.addDelegate("application/pdf", pdfConverter);


		adapter.setMessageConverter(convert);
		container.setMessageListener(adapter);


		return container;





	}


}
