package gintpsolver;


import java.util.ArrayList;

/**
 * pack to store both moves and the total change value
 * Created by xavierwoo on 2015/4/28.
 */
public class MovePack {
    protected ArrayList<Move> mvs = new ArrayList<>();
    protected Delta delta;


    /**
     * If move pack A is better, the return value will be positive.
     * If move pack B is better, the return value will be negative.
     * 0 otherwise.
     * @param mpA move pack A
     * @param mpB move pack B
     * @param  obj_type 1: maximize, -1: minimize
     * @return the return value
     */
    static protected double compare(MovePack mpA, MovePack mpB, int obj_type){
       if(mpA.delta.delta_unsat_c < mpB.delta.delta_unsat_c){
           return 1;
       }else if(mpA.delta.delta_unsat_c > mpB.delta.delta_unsat_c){
           return -1;
       }else{
           if(obj_type ==1){
               return Double.compare(mpA.delta.delta_obj, mpB.delta.delta_obj);
           }else if(obj_type == -1){
               return Double.compare(mpB.delta.delta_obj, mpA.delta.delta_obj);
           }
           else{
               return 0;
           }
       }
    }
}
