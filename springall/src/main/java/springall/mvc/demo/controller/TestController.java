package springall.mvc.demo.controller;

import springall.mvc.demo.service.TestServiceImpl;
import springall.mvc.framework.annotation.*;
import springall.mvc.framework.servlet.MyModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@MyController
@MyRequestMapping("/test")
public class TestController {
    @MyAutowired
    private TestServiceImpl testService;

    @MyRequestMapping("/method1.json")
    public MyModelAndView testMethod(HttpServletRequest req, HttpServletResponse rep, @MyRequestParam String name){
        String result = testService.sayHi(name);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", name);
        model.put("content", result);
        return new MyModelAndView("first.emml", model);
    }


    @MyRequestMapping("/method2.json")
    public void testMethod2(HttpServletRequest req, HttpServletResponse rep){
        String result = testService.sayHi("test");
        try {
            rep.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
