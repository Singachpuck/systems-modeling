package com.kpi.modeling.model;

import java.util.function.Function;

public class Distribution {

    private final DistEnum dist;

    private final Double[] params;

    public Distribution(DistEnum dist, Double... params) {
        this.dist = dist;
        this.params = params;
    }

    public double getValue() {
        return Math.abs(dist.retrieveRandValue(params));
    }

    public enum DistEnum {
        UNIFORM(params -> FunRand.Unif(params[0], params[1])),
        EXP(params -> FunRand.Exp(params[0])),
        NORMAL(params -> FunRand.Norm(params[0], params[1]));

        private final Function<Double[], Double> calculateFunc;

        DistEnum(Function<Double[], Double> calculateFunc) {
            this.calculateFunc = calculateFunc;
        }

        public double retrieveRandValue(Double... params) {
            return calculateFunc.apply(params);
        }
    }
}
