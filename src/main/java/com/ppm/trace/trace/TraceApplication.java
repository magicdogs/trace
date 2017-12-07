package com.ppm.trace.trace;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;
import com.alibaba.fastjson.JSON;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.web.servlet.ServletPlugin;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.security.ProtectionDomain;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
@SpringBootApplication
@EnableAsync
//@EnableEurekaClient
//@EnableFeignClients
public class TraceApplication extends SpringBootServletInitializer {

	Logger logger = LoggerFactory.getLogger(TraceApplication.class);

	/*@Autowired
	SimpleApplicationEventMulticaster simpleApplicationEventMulticaster;
	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	EurekaClient eurekaClient;*/

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TraceApplication.class);
	}


	public static void main(String[] args) {
		Stagemonitor.init();
		SpringApplication.run(TraceApplication.class, args);

	}

	@EventListener(Date.class)
	public void eventRecive(Date dt){
		logger.info(dt.toString());
	}

	//@Scheduled(cron = "0/1 * * * * ? ")
	@PostConstruct
	public void testEvent(){
		//AsynExcutorService threadPoolExecutor = new AsynExcutorService();
		//simpleApplicationEventMulticaster.setTaskExecutor(threadPoolExecutor);
		//applicationContext.publishEvent(new Date());
		//logger.info("xxxxxxsssssss");
		/*Application application = eurekaClient.getApplication("TRACE-SERVER");
		System.out.println(JSON.toJSONString(application));
		application = eurekaClient.getApplication("APPLICATION-CENTER-SERVICE");
		System.out.println(JSON.toJSONString(application));
		application = eurekaClient.getApplication("NIRVANA-CUSTOMER-USER-SERVICE");
		System.out.println(JSON.toJSONString(application));
		application = eurekaClient.getApplication("CELEBI-CONFIG-CENTER-SERVER");
		System.out.println(JSON.toJSONString(application));*/
	}

	// only necessary for Spring Boot versions < 1.4
	@Component
	public static class StagemonitorEnabler implements EmbeddedServletContainerCustomizer {
		@Override
		public void customize(ConfigurableEmbeddedServletContainer container) {
			container.addInitializers(new ServletContextInitializer() {
				@Override
				public void onStartup(ServletContext servletContext) throws ServletException {
					new ServletPlugin().onStartup(null, servletContext);
				}
			});
		}
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean(){
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		LogMDCServletFilter mdcFilter = new LogMDCServletFilter();
		filterRegistrationBean.setFilter(mdcFilter);
		return filterRegistrationBean;
	}

}
