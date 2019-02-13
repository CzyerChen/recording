package springall.mvc.framework.servlet;

import java.util.Map;

public class MyModelAndView {
    private String view;

    private Map<String,Object> model;


    public MyModelAndView(String view, Map<String,Object> model){
        this.view = view;
        this.model = model;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }

}
