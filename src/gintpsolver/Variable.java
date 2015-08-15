package gintpsolver;

import java.util.*;

/**
 * The decision variable, can only take integer values
 * Created by xavierwoo on 2015/4/25.
 */
public class Variable extends Expression {

    protected String name;
    protected int value;

    private int backup_value;
    private int min_value;
    private int max_value;

    HashMap<Integer, Integer> tabu_table = new HashMap<>();

    protected int getBackup_value(){
        return backup_value;
    }

    protected Variable(Gintpsolver s, String n, int min, int max, Random r) {
        super(s);
        name = n;
        min_value = min;
        max_value = max;
        value = min_value + r.nextInt(max_value - min_value + 1);
        backup_value = value;
    }

    protected void backup(){
        backup_value = value;
    }

    protected void rollback(){
        value = backup_value;
    }

    @Override
    public double get_value() {
        return value;
    }

    @Override
    protected Move find_move(int delta_dir) {
        Move best_mv = null;
        int same_count = 0;

        for(int d = delta_dir;
            d <= max_value - value && d >= min_value - value;
            d+=delta_dir){
            Move mv = new Move(this, d);
            solver.evaluate_move(mv);
            int cmp = Move.CompareMove(best_mv, mv);
            if(cmp < 0){
                best_mv = mv;
                same_count = 1;
            }else if(cmp == 0 && solver.rand.nextInt(++same_count)==0){
                best_mv = mv;
            }
        }

        return best_mv;
    }

    @Override
    public String toString(){
        return name + "(" + value +")";
    }
}
