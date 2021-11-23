package kr.co.dpm.agent.device.service;

import org.springframework.context.support.GenericXmlApplicationContext;

public class Executor {
    public static void main(String[] args) {
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:spring-config.xml");

        AgentService agent = ctx.getBean("AgentServiceImpl", AgentServiceImpl.class);

        try {
            agent.sendId();
            agent.sendDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
