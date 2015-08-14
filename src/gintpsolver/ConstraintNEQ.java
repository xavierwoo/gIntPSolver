package gintpsolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The constraint not equal
 * Created by xavierwoo on 2015/5/4.
 */
public class ConstraintNEQ extends Constraint {
    Random rand;
    protected ConstraintNEQ(Expression le, double constant, Random r) {
        super(le, constant);
        rand = r;
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
