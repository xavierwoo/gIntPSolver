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
    protected Move find_ease_move_randomly(List<Move> except_mvs, int iter) {
        if (is_satisfied()) {
            return null;
        }
        //return rand.nextInt(2) == 0 ? left_exp.find_dec_mv() : left_exp.find_inc_mv();

        Move mvd = left_exp.find_dec_mv(except_mvs, iter);
        Move mvi = left_exp.find_inc_mv(except_mvs, iter);
        if(mvd == null && mvi != null){
            return mvi;
        }else if(mvd != null && mvi == null){
            return mvd;
        }else{
            return rand.nextInt(2) == 0 ? mvd : mvi;
        }
    }

    @Override
    protected ArrayList<Move> find_all_ease_moves() {
        if (is_satisfied()) {
            return new ArrayList<>();
        }
        ArrayList<Move> mvs = new ArrayList<>();
        mvs.addAll(left_exp.find_mv(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
        return mvs;
    }

    @Override
    public String toString(){
        return left_exp.toString() + " != " + c;
    }
}
