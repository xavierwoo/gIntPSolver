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

    protected Variable(String n, int min, int max, Random r) {
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
    public String toString(){
        return name + "(" + value +")";
    }
}
