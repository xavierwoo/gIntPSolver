package gintpsolver;

import java.util.ArrayList;

/**
 * The constraint great equal than
 * Created by xavierwoo on 2015/5/4.
 */
public class ConstraintGEQ extends Constraint {

    protected ConstraintGEQ(Expression le, double constant) {
        super(le, constant);
    }

    @Override
    protected void calc_penalty() {
        if (!is_dirty) {
            return;
        }

        double left_value = left_exp.get_value();
        penalty = Math.max(0, c - left_value);
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
        return left_exp.find_inc_mv();
    }

    @Override
    protected ArrayList<Move> find_all_ease_moves() {
        if (is_satisfied()) {
            return new ArrayList<>();
        }
        return left_exp.find_mv(0, c - left_exp.get_value());
    }

    @Override
    public String toString(){
        return left_exp.toString() + " >= " + c;
    }
}
