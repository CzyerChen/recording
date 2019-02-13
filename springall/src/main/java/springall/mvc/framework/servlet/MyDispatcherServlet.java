package springall.mvc.framework.servlet;

import springall.mvc.framework.annotation.MyController;
import springall.mvc.framework.annotation.MyRequestMapping;
import springall.mvc.framework.annotation.MyRequestParam;
import springall.mvc.framework.context.MyApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyDispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String LOCATION ="contextConfigLocation";


    private List<Handler> handlerMappings = new ArrayList<>();

    private Map<Handler,HandlerAdaptor> adaptorMapping = new HashMap<>();

    private List<ViewResolver> viewResolvers = new ArrayList<>();

    public MyDispatcherServlet(){
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        MyApplicationContext applicationContext = new MyApplicationContext(config.getInitParameter(LOCATION));
        Map<String, Object> ioc = applicationContext.getAll();
        System.out.println(ioc.get("testController"));

        initMultipartResolver(applicationContext);
        initLocaleResolver(applicationContext);
        initViewResolver(applicationContext);


        //解析url和method之间的关系
        initHandlerMapping(applicationContext);
        //适配器匹配
        initAdaptorMapping(applicationContext);

        //异常解析
        initExceptionResolver(applicationContext);
        //视图转发
        initRequestToViewNameTranslator(applicationContext);

        //解析模板内容
        initViewResolver(applicationContext);

        System.out.println("Spring MVC is started.");

    }

    private void initFlashMapManager(MyApplicationContext context){}

    private void initRequestToViewNameTranslator(MyApplicationContext applicationContext) {
    }

    private void initExceptionResolver(MyApplicationContext applicationContext) {
    }

    private void initAdaptorMapping(MyApplicationContext applicationContext) {
        if(handlerMappings.isEmpty()) return;

        Map<String,Integer> paramMapping = null;
        for(Handler handler : handlerMappings){
            paramMapping = new HashMap<>();
            Class<?>[] parameterTypes = handler.method.getParameterTypes();

            Annotation[][] parameterAnnotations = handler.method.getParameterAnnotations();

            for(int i = 0 ; i< parameterAnnotations.length ; i++){
                Class<?> type = parameterTypes[i];
                if(type == HttpServletRequest.class  || type == HttpServletResponse.class){
                    paramMapping.put(type.getName(),i);
                    continue;
                }
                for(Annotation annotation : parameterAnnotations[i]){
                    if( annotation instanceof MyRequestParam){
                        String paramName = ((MyRequestParam) annotation).value();
                        if(! "".equals(paramName.trim())){
                            paramMapping.put(paramName,i);
                        }
                    }
                }
            }
            adaptorMapping.put(handler,new HandlerAdaptor(paramMapping));

        }
    }

    private void initHandlerMapping(MyApplicationContext applicationContext) {
        Map<String,Object> ioc = applicationContext.getAll();

        if(ioc.isEmpty()) return;

        for(Map.Entry<String,Object> entry : ioc.entrySet()){
            Class<?> aClass = entry.getValue().getClass();
            if(!aClass.isAnnotationPresent(MyController.class)){
                continue;
            }
            String url ="";
            if(aClass.isAnnotationPresent(MyRequestMapping.class)){
                MyRequestMapping requestMapping = aClass.getAnnotation(MyRequestMapping.class);
                url = requestMapping.value();
            }

            Method[] methods = aClass.getMethods();
            for(Method method: methods){
                if(!method.isAnnotationPresent(MyRequestMapping.class)){
                   continue;
                }
                MyRequestMapping myRequestMapping = method.getAnnotation(MyRequestMapping.class);
                //url += myRequestMapping.value();
                //spring handlerMapping 正则匹配
                String regex = (url+myRequestMapping.value()).replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regex);
                handlerMappings.add(new Handler(entry.getValue(),method,pattern));
                System.out.println("Mapping: " + regex + " " + method.toString());
            }


        }
    }

    private class Handler{
        protected  Object controller;
        protected  Method method;
        protected Pattern pattern;

        public  Handler(Object controller,Method method,Pattern pattern){
            this.controller = controller;
            this.method = method;
            this.pattern = pattern;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Handler handler = (Handler) o;

            return Objects.equals(controller,handler.controller) &&
                    Objects.equals(method,handler.method) &&
                    Objects.equals(pattern,handler.pattern);
            /*if (controller != null ? !controller.equals(handler.controller) : handler.controller != null) return false;
            if (method != null ? !method.equals(handler.method) : handler.method != null) return false;
            return pattern != null ? pattern.equals(handler.pattern) : handler.pattern == null;
    */    }

        @Override
        public int hashCode() {
           /* int result = controller != null ? controller.hashCode() : 0;
            result = 31 * result + (method != null ? method.hashCode() : 0);
            result = 31 * result + (pattern != null ? pattern.hashCode() : 0);
         */   return Objects.hash(controller,method,pattern);
        }
    }

    private void initMultipartResolver(MyApplicationContext applicationContext){

  }

  private void initLocaleResolver(MyApplicationContext applicationContext){

  }

  private void initViewResolver(MyApplicationContext applicationContext){
      //检查模板文件的位置，加载模板的个数，存储到缓存，避免语法错误
      String templateRoot = applicationContext.getProperty().getProperty("templateRoot");
      String path = this.getClass().getClassLoader().getResource(templateRoot).getFile();
      File roorDir = new File(path);
      for(File template : roorDir.listFiles()){
          viewResolvers.add(new ViewResolver(template.getName(),template));
      }
  }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("调用");
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception, Msg :" + Arrays.toString(e.getStackTrace()));
        }
    }

    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception{

        //先取出来一个 Handler， 从 HandlerMapping 中取
        Handler handler = getHandler(request);
        if (handler == null) {
            response.getWriter().write("404 Not Found");
        }

        //再取出来一个适配器
        HandlerAdaptor ha = getHandlerAdapter(handler);


        if (ha != null) {
            //再由适配器调用具体方法
            MyModelAndView mv = ha.handle(request, response, handler);
            applyDefaultViewName(response, mv);
        }

    }

    private Handler getHandler(HttpServletRequest request) {
        //循环handlerMapping
        if (handlerMappings.isEmpty()) {
            return null;
        }
        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        //Spring HandlerMapping 的经典正则匹配
        for (Handler handler : handlerMappings) {
            Matcher matcher = handler.pattern.matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    private HandlerAdaptor getHandlerAdapter(Handler handler) {
        if (adaptorMapping.isEmpty()) {
            return null;
        }

        return adaptorMapping.get(handler);
    }

    public void applyDefaultViewName(HttpServletResponse response, MyModelAndView mv) throws Exception {
        if (null == mv) {
            return;
        }

        if (viewResolvers.isEmpty()) {
            return;
        }

        for (ViewResolver viewResolver : viewResolvers) {
            if (!mv.getView().equals(viewResolver.getViewName())) {
                continue;
            }

            String r = viewResolver.parse(mv);

            if (r != null) {
                response.getWriter().write(r);
                break;
            }
        }
    }


    private class HandlerAdaptor {
      private Map<String,Integer> paramMapping;

      public HandlerAdaptor(Map<String,Integer> paramMapping){
          this.paramMapping = paramMapping;
      }

        /**
         * 主要目的是用反射调用url对应的method
         * @param request
         * @param response
         * @param handler
         */
        public MyModelAndView handle(HttpServletRequest request, HttpServletResponse response, MyDispatcherServlet.Handler handler) throws InvocationTargetException, IllegalAccessException {

            //为了给request、response赋值
            Class<?>[] parameterTypes = handler.method.getParameterTypes();

            //要想给参数赋值，只能通过索引号来找到具体的某个参数
            Object[] paramValues = new Object[parameterTypes.length];

            Map<String, String[]> params = request.getParameterMap();

            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                        .replaceAll(",\\s", ",");

                if (!this.paramMapping.containsKey(param.getKey())) {
                    continue;
                }

                Integer index = this.paramMapping.get(param.getKey());

                //单个赋值是不行的
                paramValues[index] = caseStringValue(value, parameterTypes[index]);
            }

            //request 和 response 要赋值
            String requestName = HttpServletRequest.class.getName();
            if (this.paramMapping.containsKey(requestName)) {
                Integer requestIndex = this.paramMapping.get(requestName);
                paramValues[requestIndex] = request;
            }
            String responseName = HttpServletResponse.class.getName();
            if (this.paramMapping.containsKey(responseName)) {
                Integer responseIndex = this.paramMapping.get(responseName);
                paramValues[responseIndex] = response;
            }

            boolean isModelAndView = handler.method.getReturnType() == MyModelAndView.class;
            Object r = handler.method.invoke(handler.controller, paramValues);
            if (isModelAndView) {
                return (MyModelAndView) r;
            } else {
                return null;
            }

        }

        /**
         * 转换参数类型
         * @param value
         * @param clazz
         * @return
         */
        private Object caseStringValue(String value, Class<?> clazz) {
            if (clazz == String.class) {
                return value;
            } else if (clazz == Integer.class) {
                return Integer.valueOf(value);
            } else if (clazz == int.class) {
                return Integer.valueOf(value);
            } else {
                return null;
            }
        }

    }

    private class  ViewResolver{
      private String viewName;
      private File file;

        // 在使用正则表达式时，利用好其预编译功能，可以有效加快正则匹配速度。
        // 说明：不要在方法体内定义：Pattern pattern = Pattern.compile(规则);
        //    public class XxxClass {
        //        // Use precompile
        //        private static Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
        //        public Pattern getNumberPattern() {
        //            // Avoid use Pattern.compile in method body.
        //            Pattern localPattern = Pattern.compile("[0-9]+");
        //            return localPattern;
        //        }
        //    }
        private Pattern pattern = Pattern.compile("@\\{(.+?)}", Pattern.CASE_INSENSITIVE);

        protected ViewResolver(String viewName, File file) {
            this.viewName = viewName;
            this.file = file;
        }

        protected String parse(MyModelAndView mv) throws Exception {

            StringBuffer sb = new StringBuffer();

            RandomAccessFile ra = new RandomAccessFile(this.file, "r");

            try {
                //模板框架的语法是非常复杂，但是，原理是一样的
                //无非都是用正则表达式来处理字符串而已
                String line = null;
                while (null != (line = ra.readLine())) {
                    Matcher matcher = matcher(line);
                    while (matcher.find()) {
                        for (int i = 1; i <= matcher.groupCount(); i++) {
                            String paramName = matcher.group(i);
                            Object paramValue = mv.getModel().get(paramName);
                            if (null == paramValue) {
                                continue;
                            }
                            line = line.replaceAll("@\\{" + paramName + "}", paramValue.toString());
                        }
                    }

                    sb.append(line);
                }
            } finally {
                ra.close();
            }

            return sb.toString();
        }

        private Matcher matcher(String str) {
            return pattern.matcher(str);
        }

        public String getViewName() {
            return viewName;
        }
    }


}
