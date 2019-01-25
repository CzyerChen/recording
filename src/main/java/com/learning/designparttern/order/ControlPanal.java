package com.learning.designparttern.order;

import java.util.ArrayList;
import java.util.List;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 19:59
 */
public class ControlPanal {

    private List<Command> list = new ArrayList();
    private int index;
    private Command command;

    public void setControl(int index,Command command){
        this.index = index;
        this.command = command;
        list.add(index,command);
    }

    public void keyPressed(int index)
    {
        list.get(index).execute();
    }

}
