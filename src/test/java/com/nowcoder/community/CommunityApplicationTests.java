package com.nowcoder.community;

import com.nowcoder.community.config.Alphaconfig;
import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.servers.AlphaServers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class CommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Test
	public void testApplicationContext() {
		System.out.println(applicationContext);

		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());

		alphaDao = applicationContext.getBean("Hibernate",AlphaDao.class);
		System.out.println(alphaDao.select());
	}

	@Test
	public void testBeanManagement(){
		AlphaServers alohaService = applicationContext.getBean(AlphaServers.class);
		System.out.println(alohaService);

		alohaService = applicationContext.getBean(AlphaServers.class);
		System.out.println(alohaService);
	}

	@Test
	public void TestSimpleDate(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired
	@Qualifier("Hibernate")
	AlphaDao alphaDao;

	@Autowired
	AlphaServers alphaServers;

	@Autowired
	SimpleDateFormat simpleDateFormat;

	@Test
	public void testDI(){
		System.out.println(alphaDao);
		System.out.println(alphaServers);
		System.out.println(simpleDateFormat);
	}
}
