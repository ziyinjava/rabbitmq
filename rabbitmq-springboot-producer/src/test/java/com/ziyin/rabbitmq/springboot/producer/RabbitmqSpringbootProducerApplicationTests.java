package com.ziyin.rabbitmq.springboot.producer;

import cn.hutool.core.date.DateUtil;
import com.ziyin.rabbitmq.entity.Order;
import com.ziyin.rabbitmq.springboot.producer.producer.RabbitSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqSpringbootProducerApplicationTests {

	@Autowired
	private RabbitSender rabbitSender;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testSender() throws Exception{
		Map<String,Object> properties = new HashMap<String,Object>();
		properties.put("number", "12345");
		properties.put("send_time", DateUtil.now());
		rabbitSender.send("hello rabbitmq for springboot !",properties);
	}

	@Test
	public void testSender2() throws Exception{
		rabbitSender.sendOrder(new Order("001","first order"));
	}
}
