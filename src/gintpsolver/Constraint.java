package gintpsolver;

import java.util.Random;

/**
 * The constraint
 * Created by xavierwoo on 2015/4/25.
 */
public class Constraint {
    static public enum Type {
        GEQ, EQ, LEQ, NEQ
    }

    private Expression left_exp;
    private double c;
    private Type t;

    private Random rand;
    private double penalty;
    protected boolean is_dirty = true;

    protected Constraint(Expression le, Type type, double constant, Random r) {
        left_exp = le;
        left_exp.in_constraint = this;
        t = type;
        c = constant;
        rand = r;
    }

    private void calc_penalty() {
        if (!is_dirty) {
            return;
        }

        double left_value = left_exp.get_value();
        switch (t) {
            case GEQ:
                penalty = Math.max(0, c - left_value);
                break;
            case EQ:
                penalty = Math.abs(left_value - c);
                break;
            case LEQ:
                penalty = Math.max(0, left_value - c);
                break;
            case NEQ:
                penalty = Double.compare(left_value, c) == 0 ? 1 : 0;
                break;
            default:
                throw new UnsupportedOperationException("WTF!");
        }

        if (Double.compare(penalty, 0) == 0) {
            penalty = 0;
        }
        is_dirty = false;
    }

    protected double get_penalty() {
        if (is_dirty) {
            calc_penalty();
        }
        return penalty;
    }

    protected boolean is_satisfied() {
        return get_penalty() == 0;
    }

    protected Move find_ease_move() {
        if (is_satisfied()) {
            return null;
        }
        switch (t) {
            case GEQ:
                return left_exp.find_inc_mv();
            case EQ:
                double cmp = Double.compare(left_exp.get_value(), c);
                if (cmp > 0) {
                    return left_exp.find_dec_mv();
                } else {
                    return left_exp.find_inc_mv();
                }
            case LEQ:
                return left_exp.find_dec_mv();
            case NEQ:
                if(rand.nextInt(2) == 0){
                    return left_exp.find_dec_mv();
                }else{
                    return left_exp.find_inc_mv();
                }
        }
        return null;
    }
}
