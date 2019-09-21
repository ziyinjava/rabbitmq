package com.ziyin.rabbitmq.springcloudstream.producer;

import cn.hutool.core.date.DateUtil;
import com.ziyin.rabbitmq.springcloudstream.producer.stream.RabbitmqSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqSpringcloudstreamProducerApplicationTests {


	@Autowired
	private RabbitmqSender rabbitmqSender;


	@Test
	public void sendMessageTest1() {
		for(int i = 0; i < 1; i ++){
			try {
				Map<String, Object> properties = new HashMap<String, Object>();
				properties.put("SERIAL_NUMBER", "12345");
				properties.put("BANK_NUMBER", "abc");
				properties.put("PLAT_SEND_TIME", DateUtil.now());
				rabbitmqSender.sendMessage("Hello, I am amqp sender num :" + i, properties);

			} catch (Exception e) {
				System.out.println("--------error-------");
				e.printStackTrace();
			}
		}
		//TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
	}
}
