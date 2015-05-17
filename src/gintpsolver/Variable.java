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

    protected boolean is_boolean() {
        return max_value - min_value == 1;
    }

    @Override
    protected double get_value() {
        return (double) value;
    }

    @Override
    protected Move find_dec_mv(List<Move> except_mvs, int iter) {

        for(int d = -1 ; value + d >= min_value ; --d){
            Integer tt = tabu_table.get(value+d);
            if(tt != null && tt > iter){
                continue;
            }

            Move mv = new Move(this, d);
            if(except_mvs == null || !except_mvs.contains(mv)){
                return mv;
            }
        }
        return null;
    }

    @Override
    protected ArrayList<Move> find_all_dec_1_mv() {
        ArrayList<Move> mvs = new ArrayList<>();
        Move mv = find_dec_mv(null, 0);
        if(mv != null){
            mvs.add(mv);
        }
        return mvs;
    }


    @Override
    protected Move find_inc_mv(List<Move> except_mvs, int iter) {
        for( int d = 1; value + d <= max_value; ++d ){
            Integer tt = tabu_table.get(value+d);
            if(tt!=null && tt > iter){
                continue;
            }
            Move mv = new Move(this, d);
            if(except_mvs == null || !except_mvs.contains(mv)){
                return  mv;
            }
        }
        return null;
    }

    @Override
    protected ArrayList<Move> find_all_inc_1_mv() {
        ArrayList<Move> mvs = new ArrayList<>();
        Move mv = find_inc_mv(null, 0);
        if(mv != null){
            mvs.add(mv);
        }
        return mvs;
    }


    @Override
    protected ArrayList<Move> find_mv(double min_delta, double max_delta) {
        ArrayList<Move> mvs = new ArrayList<>();
        int lb = Double.compare(min_delta, Double.NEGATIVE_INFINITY) == 0 ? min_value
                : Math.max((int)min_delta + value, min_value);
        int ub = Double.compare(max_delta, Double.POSITIVE_INFINITY) == 0 ? max_value
                : Math.min((int)max_delta + value, max_value);

        for (int new_value = lb; new_value <= ub; new_value++) {
            if (new_value - value != 0 && new_value >= min_value && new_value <= max_value) {
                mvs.add(new Move(this, new_value - value));
            }
        }

        return mvs;
    }

    @Override
    protected ArrayList<Variable> get_all_variables() {
        ArrayList<Variable> vars = new ArrayList<>();
        vars.add(this);
        return vars;
    }

    @Override
    public String toString() {
        return name;
    }

    public void check(){
        if(value < min_value || value > max_value){
            throw new UnknownError("variable " + name
                    +" error! value:" + value + " bound:[" + min_value + "," +max_value+"]");
        }
    }
}
