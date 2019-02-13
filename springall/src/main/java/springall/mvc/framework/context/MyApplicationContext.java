package springall.mvc.framework.context;

import springall.mvc.framework.annotation.MyAutowired;
import springall.mvc.framework.annotation.MyController;
import springall.mvc.framework.annotation.MyService;

import javax.servlet.ServletConfig;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    private Properties properties = new Properties();
    private Map<String ,Object> instancesMapping= new ConcurrentHashMap<>();
    private List<String> classNames = new ArrayList<>();

    public MyApplicationContext(String location){
        //加载配置,读取配置文件中配置
        loadProperties(location);
        //扫描相关类
        classesScanner(properties.getProperty("scanPackage"));
        //扫描注解，初始化相关类，放入IOC容器
        initInstance();
        //依赖注入
        initDI();


    }

    private void initDI() {
        if(instancesMapping.isEmpty())return;

        for(Map.Entry<String ,Object> entry:instancesMapping.entrySet()){
            Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();

            for(Field field : declaredFields){
                if(!field.isAnnotationPresent(MyAutowired.class)){
                    continue;
                }

                MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
                String id = myAutowired.value().trim();
                if("".equals(id)){
                    id = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(),instancesMapping.get(id));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void loadProperties(String location){
        InputStream ins = null;
        ins = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            properties.load(ins);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != ins){
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void classesScanner(String packageName){
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File dir = new File(url.getFile());
        for(File file :dir.listFiles()){
            if(file.isDirectory()){
                classesScanner(packageName+"."+file.getName());
            }else {
                classNames.add(packageName+"."+file.getName().replace(".class","").trim());
            }
        }
    }


    private void initInstance() {
        if(classNames.size() ==0) return;

        for(String className :classNames){
            try {
                Class<?> aClass = Class.forName(className);
                if(aClass.isAnnotationPresent(MyController.class)){
                    String id = lowerFirstChar(aClass.getSimpleName());
                    instancesMapping.put(id,aClass.newInstance());
                }else  if(aClass.isAnnotationPresent(MyService.class)){
                    MyService myService = aClass.getAnnotation(MyService.class);
                    String id = myService.value();
                    if(!"".equals(id.trim())){
                        instancesMapping.put(id,aClass.newInstance());
                    }
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for(Class<?> aInterface : interfaces){
                        instancesMapping.put(aInterface.getSimpleName(),aClass.newInstance());
                    }
                }else {
                    continue;
                }

            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将首字母小写
     * @param str
     * @return
     */
    private String lowerFirstChar(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }


    public Map<String,Object> getAll(){
        return instancesMapping;
    }

    public Properties getProperty(){
        return  this.properties;
    }
}
