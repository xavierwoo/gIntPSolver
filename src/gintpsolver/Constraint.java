package gintpsolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The constraint
 * Created by xavierwoo on 2015/4/25.
 */
public abstract class Constraint {

    protected Gintpsolver solver;

    protected Expression left_exp;
    protected double c;

    protected double penalty;
    protected boolean is_dirty = true;

    protected Constraint(Gintpsolver s, Expression le, double constant) {
        solver = s;
        left_exp = le;
        left_exp.in_constraint = this;
        c = constant;
        //rand = r;
    }

    protected abstract void calc_penalty();

    protected  abstract Move find_move();

    protected double get_penalty() {
        if (is_dirty) {
            calc_penalty();
        }
        return penalty;
    }


    protected boolean is_satisfied() {
        return get_penalty() == 0;
    }

    /**
     * if the left expression changed value by d, how is the constraint?
     * @param d the change value of left expression
     * @return 1 if constraint changed to bu unsatisfied, -1 if changed to satisfied, 0 otherwise
     */
    protected abstract int get_delta(double d);
}
