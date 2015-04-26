package gintpsolver;

import java.util.ArrayList;

/**
 * Created by xavierwoo on 2015/4/25.
 */
public abstract class Expression {

    protected ArrayList<Expression> in_exp = new ArrayList<>();
    protected Constraint in_constraint = null;

    abstract protected double get_value();

    /**
     *
     * @return A random move that can decrease the expression value
     */
    abstract protected Move find_dec_mv();

    /**
     * Find moves that can decrease the value more than delta
     * @param delta the change value
     * @return
     */
    abstract protected ArrayList<Move> find_dec_mv(double delta);

    /**
     *
     * @return A random move that can increase the expression value
     */
    abstract protected Move find_inc_mv();

    /**
     * Find moves that can increase the value more than delta;
     * @param delta the change value
     * @return moves found
     */
    abstract protected ArrayList<Move> find_inc_mv(double delta);
}
