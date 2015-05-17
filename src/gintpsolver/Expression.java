package gintpsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Expression object
 * Created by xavierwoo on 2015/4/25.
 */
public abstract class Expression {

    protected boolean is_dirty = true;
    protected ArrayList<Expression> in_exp = new ArrayList<>();
    protected Constraint in_constraint = null;

    abstract public double get_value();


    /**
     * @return A random move that can decrease the expression value
     */
    abstract protected Move find_dec_mv(List<Move> except_mvs, int iter);

    /**
     * Find moves that decrease the value
     * @return the moves found
     */
    abstract protected ArrayList<Move> find_all_dec_1_mv();

    /**
     * @return A random move that can increase the expression value
     */
    abstract protected Move find_inc_mv(List<Move> except_mvs, int iter);

    /**
     * Find moves that increase the value
     * @return the moves found
     */
    abstract protected ArrayList<Move> find_all_inc_1_mv();

    /**
     * Find move that can change value from min_delta to max_delta
     *
     * @param min_delta minimum change value
     * @param max_delta maximum change value
     * @return moves found
     */
    abstract protected ArrayList<Move> find_mv(double min_delta, double max_delta);


    abstract protected  ArrayList<Variable> get_all_variables();

    abstract  protected double get_delta(Expression exp, double delta);
}
