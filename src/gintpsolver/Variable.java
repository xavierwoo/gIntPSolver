package gintpsolver;

import java.util.Random;

/**
 * The decision variable, can only take integer values
 * Created by xavierwoo on 2015/4/25.
 */
public class Variable extends Expression {

    String name;
    int value;
    int min_value;
    int max_value;


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
    public String toString(){
        return name;
    }
}
