package com.kpi.modeling;

import com.kpi.modeling.model.Model;

public class DiscreteModel {

    public static void main(String[] args) {
        Model model = new Model(2, 1, 5);
        model.simulate(1000);
    }
}
