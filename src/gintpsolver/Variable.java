package gintpsolver;

import java.util.ArrayList;
import java.util.Random;

/**
 * The decision variable, can only take integer values
 * Created by xavierwoo on 2015/4/25.
 */
public class Variable extends Expression {

    protected String name;
    private int value;
    private int min_value;
    private int max_value;

    protected boolean is_boolean(){
        return max_value - min_value == 1;
    }

    protected Variable(String n, int min, int max, Random r){
        name = n;
        min_value = min;
        max_value = max;
        value = min_value + r.nextInt(max_value - min_value + 1);
    }

    @Override
    protected double get_value() {
        return (double) value;
    }

    @Override
    protected Move find_dec_mv(){
        if(value > min_value) {
            return new Move(this, -1);
        }else{
            return null;
        }
    }


    @Override
    protected Move find_inc_mv(){
        if(value < max_value){
            return new Move(this, 1);
        }else{
            return null;
        }
    }


    @Override
    protected ArrayList<Move> find_mv(double min_delta, double max_delta) {
        ArrayList<Move> mvs = new ArrayList<>();
        for(int d = (int)min_delta; d <= (int)max_delta; d++){
            if(value + d >= min_value && value +d <= max_value){
                mvs.add(new Move(this, d));
            }
        }
        if(mvs.isEmpty()){
            return null;
        }else{
            return mvs;
        }
    }

    @Override
    public String toString(){
        return name;
    }
}
