package tech.jyou.compiledemo;

import tech.jyou.compiledemo.service.HelloService;

public class HelloWorld {
    public static void main(String[] args) {
        HelloService hService = new HelloService();
        hService.printHello();
    }
}
