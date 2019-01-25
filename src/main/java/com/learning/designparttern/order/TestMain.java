package com.learning.designparttern.order;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 20:05
 */
public class TestMain {

    public static void main(String[] args){
        ControlPanal panal = new ControlPanal();
        Computer computer = new Computer();
        panal.setControl(0,new TurnOnComputer(computer));
        panal.setControl(1,new TurnOffComputer(computer));

        panal.keyPressed(0);
        panal.keyPressed(1);
    }
}
