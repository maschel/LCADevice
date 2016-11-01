package com.maschel;

import com.maschel.lca.device.Argument;

import java.util.List;

public class Arguments {

    public static class MotorSpeedArgument extends Argument {
        private Double speed;

        @Override
        public void parseRawArguments(List<Object> args) throws IllegalArgumentException {
            this.speed = (Double)args.get(0);
        }

        public Double getSpeed() {
            return speed;
        }
    }

}
