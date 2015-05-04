package gintpsolver;

import java.util.ArrayList;
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
    protected Move find_ease_move_randomly() {
        if (is_satisfied()) {
            return null;
        }
        return rand.nextInt(2) == 0 ? left_exp.find_dec_mv() : left_exp.find_inc_mv();
    }

    @Override
    protected ArrayList<Move> find_all_ease_moves() {
        if (is_satisfied()) {
            return new ArrayList<>();
        }
        ArrayList<Move> mvs = new ArrayList<>();

        mvs.addAll(left_exp.find_all_dec_1_mv());
        mvs.addAll(left_exp.find_all_inc_1_mv());
        return mvs;
    }

    @Override
    public String toString(){
        return left_exp.toString() + "!=" + c;
    }
}
