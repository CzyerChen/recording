package springall.mvc.demo.service;

import springall.mvc.framework.annotation.MyService;

@MyService
public class TestServiceImpl implements TestService{

    @Override
    public String sayHi(String name){
        return  "say hi!"+name;
    }
}
