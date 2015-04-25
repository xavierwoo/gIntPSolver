package gintpsolver;

/**
 * A move to increase or decrease the value of a variable
 * Created by xavierwoo on 2015/4/25.
 */
public class Move {
    Variable var;
    int delta;

    protected Move(Variable v, int d){
        var = v;
        delta = d;
    }
}
