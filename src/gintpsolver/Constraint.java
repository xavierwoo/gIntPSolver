package gintpsolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The constraint
 * Created by xavierwoo on 2015/4/25.
 */
public abstract class Constraint {

    protected Expression left_exp;
    protected double c;

    //private Random rand;
    protected double penalty;
    protected boolean is_dirty = true;

    protected Constraint(Expression le, double constant) {
        left_exp = le;
        left_exp.in_constraint = this;
        c = constant;
        //rand = r;
    }

    protected abstract void calc_penalty();

    protected double get_penalty() {
        if (is_dirty) {
            calc_penalty();
        }
        return penalty;
    }


    protected ArrayList<Variable> get_all_variables(){
        return left_exp.get_all_variables();
    }

    protected boolean is_satisfied() {
        return get_penalty() == 0;
    }

    protected abstract Move find_ease_move_randomly(List<Move> except_mvs) ;

    protected abstract ArrayList<Move> find_all_ease_moves() ;
}
