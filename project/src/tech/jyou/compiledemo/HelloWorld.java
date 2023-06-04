package tech.jyou.compiledemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.jyou.compiledemo.service.IGreetingService;
import tech.jyou.compiledemo.service.impl.AlienGreetingService;
import tech.jyou.compiledemo.service.impl.CatGreetingService;
import tech.jyou.compiledemo.service.impl.DogGreetingService;
import tech.jyou.compiledemo.service.impl.HumanGreetingService;

public class HelloWorld {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorld.class);

    public static void main(String[] args) {
        IGreetingService greetingService = null;

        if (args.length <= 0) {
            logger.error("Nobody at home!");
            return;
        }

        String name = args[0];
        switch (name) {
            case "XiaoBai":
                greetingService = new CatGreetingService();
                break;
            case "DaHuang":
                greetingService = new DogGreetingService();
                break;
            case "XiaoMing":
                greetingService = new HumanGreetingService();
                break;
            default:
                greetingService = new AlienGreetingService();
                break;
        }
        greetingService.greet(name);
    }

}