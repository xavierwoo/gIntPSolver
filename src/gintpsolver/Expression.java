package gintpsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Expression object
 * Created by xavierwoo on 2015/4/25.
 */
public abstract class Expression {
    protected Gintpsolver solver;
    protected boolean is_dirty = true;
    protected ArrayList<Expression> in_exp = new ArrayList<>();
    protected Constraint in_constraint = null;

    abstract public double get_value();
    abstract protected Move find_move(int delta_dir);

    public Expression(Gintpsolver s){
        solver = s;
    }
}
