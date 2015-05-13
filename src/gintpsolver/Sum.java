package gintpsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The sum expression
 * Created by xavierwoo on 2015/4/25.
 */
public class Sum extends Expression {

    private double value = 0;


    protected HashMap<Expression, Double> exp_elems = new HashMap<>();

    protected double c = 0;

    protected Random rand;

    protected Sum(Random r) {
        rand = r;
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
    protected double get_value() {
        if(is_dirty){
            calc_value();
        }
        return value;
    }


    @Override
    protected Move find_dec_mv() {
        Move mv = null;
        int count = 0;
        for (Map.Entry<Expression, Double> entry : exp_elems.entrySet()){
            if(entry.getValue() > 0){
                Move m = entry.getKey().find_dec_mv();
                if (m != null && rand.nextInt(++count) == 0) {
                    mv = m;
                }
            }else{
                Move m = entry.getKey().find_inc_mv();
                if (m != null && rand.nextInt(++count) == 0) {
                    mv = m;
                }
            }
        }
        return mv;
    }

    @Override
    protected ArrayList<Move> find_all_dec_1_mv() {
        ArrayList<Move> mvs = new ArrayList<>();
        for(Map.Entry<Expression, Double> entry : exp_elems.entrySet()){
            Expression exp = entry.getKey();
            double para = entry.getValue();
            ArrayList<Move> submvs;
            submvs = para > 0 ? exp.find_all_dec_1_mv() : exp.find_all_inc_1_mv();
            mvs.addAll(submvs);
        }
        return mvs;
    }

    @Override
    protected Move find_inc_mv() {
        Move mv = null;
        int count = 0;
        for (Map.Entry<Expression, Double> entry :exp_elems.entrySet()){
            if(entry.getValue() > 0){
                Move m = entry.getKey().find_inc_mv();
                if(m!=null && rand.nextInt(++count) == 0){
                    mv = m;
                }
            }else{
                Move m = entry.getKey().find_dec_mv();
                if(m!=null && rand.nextInt(++count) == 0){
                    mv = m;
                }
            }
        }
        return mv;
    }

    @Override
    protected ArrayList<Move> find_all_inc_1_mv() {
        ArrayList<Move> mvs = new ArrayList<>();
        for(Map.Entry<Expression, Double> entry : exp_elems.entrySet()){
            Expression exp = entry.getKey();
            double para = entry.getValue();
            ArrayList<Move> submvs;
            submvs = para > 0 ? exp.find_all_inc_1_mv() : exp.find_all_dec_1_mv();
            mvs.addAll(submvs);
        }
        return mvs;
    }

    @Override
    protected ArrayList<Move> find_mv(double min_delta, double max_delta) {
        ArrayList<Move> mvs = new ArrayList<>();
        for(Map.Entry<Expression, Double> entry : exp_elems.entrySet()){
            Expression e = entry.getKey();
            double para = entry.getValue();
            ArrayList<Move> sub_mvs;
            sub_mvs = para > 0 ?
                    e.find_mv(min_delta / para, max_delta / para)
                    : e.find_mv(max_delta / para, min_delta / para);

            mvs.addAll(sub_mvs);
        }
        return mvs;
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
