package tech.jyou.compiledemo.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jyou.compiledemo.service.IGreetingService;

public class HumanGreetingService implements IGreetingService {
    private static final Logger logger = LoggerFactory.getLogger(HumanGreetingService.class);

    @Override
    public void greet(String name) {
        logger.info("{} is saying hello: Ni Chou Sha!", name);
    }
}
