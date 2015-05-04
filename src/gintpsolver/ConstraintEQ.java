package gintpsolver;

import java.util.ArrayList;

/**
 * Created by xavierwoo on 2015/5/4.
 */
public class ConstraintEQ extends Constraint{
    protected ConstraintEQ(Expression le, double constant) {
        super(le, constant);
    }


    @Override
    protected void calc_penalty() {
        if (!is_dirty) {
            return;
        }

        double left_value = left_exp.get_value();
        penalty = Math.abs(left_value - c);
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
        return Double.compare(left_exp.get_value(), c) > 0 ?
                left_exp.find_dec_mv() : left_exp.find_inc_mv();
    }

    @Override
    protected ArrayList<Move> find_all_ease_moves() {
        if (is_satisfied()) {
            return new ArrayList<Move>();
        }
        return Double.compare(left_exp.get_value(), c) > 0 ?
                left_exp.find_mv(c - left_exp.get_value(), 0)
                : left_exp.find_mv(0, c - left_exp.get_value());
    }
}
