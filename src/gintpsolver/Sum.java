package gintpsolver;

import java.util.*;

/**
 * The sum expression
 * Created by xavierwoo on 2015/4/25.
 */
public class Sum extends Expression {
    protected HashMap<Expression, Double> exp_elems = new HashMap<>();
    private double value = 0;

    protected double c = 0;

    //protected Random rand;

    protected Sum(Gintpsolver s) {
        super(s);
    }

    /**
     * One expression can only have one constant element.
     * Doing this will override the value that is added before.
     * @param constant the constant element
     */
    public void add_element(double constant){
        c = constant;
    }

    /**
     * Add an subexpression
     * @param e subexpression
     * @param para parameter
     */
    public void add_element(Expression e, double para) {
        if (para != 0) {
            exp_elems.put(e, para);
            e.in_exp.add(this);
        }
    }


    private void calc_value(){
        value = c;
        for(Map.Entry<Expression, Double> entry : exp_elems.entrySet()){
            value += entry.getValue() * entry.getKey().get_value();
        }
        is_dirty = false;
    }

    @Override
    public double get_value() {
        if(is_dirty){
            calc_value();
        }
        return value;
    }



    @Override
    public String toString(){
        String str = "";
        for (Map.Entry<Expression, Double> entry : exp_elems.entrySet()){
            Expression exp = entry.getKey();
            double para = entry.getValue();
            if(para == 1){
                if(!str.isEmpty()){
                    str += " +";
                }
                str += exp.getClass() == Variable.class ? " " + exp.toString() : " ( " + exp.toString() + " )";
            }else if(para == -1){
                str += exp.getClass() == Variable.class ? " -" + exp.toString() : " -( " + exp.toString() + " )";
            }else if(para > 0){
                if(!str.isEmpty()){
                    str += " +";
                }
                str += exp.getClass() == Variable.class ? " " + para + " " + exp.toString() : " " + para + "( " + exp.toString() + " )";
            }else if(para < 0){
                str += exp.getClass() == Variable.class ? " " + para + " " + exp.toString() : " " + para + "( " + exp.toString() + " )";
            }
        }
        return str;
    }
}
