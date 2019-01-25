package com.learning.designparttern.order;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 19:55
 */
public class TurnOnComputer implements Command{
    private Computer computer;

    TurnOnComputer(Computer computer){
        this.computer = computer;
    }

    @Override
    public void execute() {
        computer.turnOn();
    }
}
