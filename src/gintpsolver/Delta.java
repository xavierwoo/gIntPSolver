package gintpsolver;

/**
 * The change values for moves
 * Created by xavierwoo on 2015/4/28.
 */
public class Delta {
    protected int delta_unsat_c = 0;
    protected double delta_obj = 0;

    public void merge(Delta d){
        delta_obj += d.delta_obj;
        delta_unsat_c += d.delta_unsat_c;
    }

    static int compare(Delta a, Delta b, int obj_type){
        if(a.delta_unsat_c < b.delta_unsat_c){
            return 1;
        }else if( a.delta_unsat_c > b.delta_unsat_c){
            return -1;
        }else{
            if(obj_type == 1){
                int cmpv = Double.compare(a.delta_obj , b.delta_obj);
                if(cmpv > 0){
                    return 1;
                }else if(cmpv < 0){
                    return -1;
                }else{
                    return 0;
                }
            }else if(obj_type == -1){
                int cmpv = Double.compare(a.delta_obj , b.delta_obj);
                if(cmpv < 0){
                    return 1;
                }else if(cmpv > 0){
                    return -1;
                }else{
                    return 0;
                }
            }else{
                return 0;
            }
        }
    }
}
