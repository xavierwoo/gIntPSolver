package gintpsolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The constraint not equal
 * Created by xavierwoo on 2015/5/4.
 */
public class ConstraintNEQ extends Constraint {

    protected ConstraintNEQ(Gintpsolver s, Expression le, double constant) {
        super(s, le, constant);
    }


    @Override
    protected void calc_penalty() {
        if (!is_dirty) {
            return;
        }

        double left_value = left_exp.get_value();
        penalty = Double.compare(left_value, c) == 0 ? 1 : 0;
        if (Double.compare(penalty, 0) == 0) {
            penalty = 0;
        }
        is_dirty = false;
    }

    @Override
    protected Move find_move() {
        Move mv_i = left_exp.find_move(1);
        Move mv_d = left_exp.find_move(-1);
        int cmp = Move.CompareMove(mv_i, mv_d);
        if(cmp > 0){
            return mv_i;
        }else if(cmp < 0){
            return mv_d;
        }else{
            return solver.rand.nextBoolean() ? mv_i : mv_d;
        }
    }

    @Override
    protected int get_delta(double d) {
        if(is_satisfied()){
            return Double.compare(left_exp.get_value() + d, c  ) != 0 ? 0 : 1;
        }else{
            return Double.compare(left_exp.get_value() + d, c  ) != 0 ? -1 : 0;
        }
    }

    @Override
    public String toString(){
        return left_exp.toString() + " != " + c;
    }
}
