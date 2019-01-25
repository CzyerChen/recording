package com.learning.designparttern.order;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 19:58
 */
public class TurnOffComputer implements Command {
    private  Computer computer;

    TurnOffComputer(Computer computer){
        this.computer = computer;
    }

    @Override
    public void execute() {
        computer.turnOff();
    }
}
