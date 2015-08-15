package gintpsolver;

/**
 * A move to increase or decrease the value of a variable
 * Created by xavierwoo on 2015/4/25.
 */
public class Move {
    Variable var;
    int delta_value;
    int delta_unsat_c;

    protected Move(Variable v, int d){
        var = v;
        delta_value = d;
    }



    @Override
    public boolean equals(Object o){
        if(o==null || o.getClass()!=this.getClass()){
            return false;
        }
        Move mv_o = (Move) o;
        return var==mv_o.var && delta_value == mv_o.delta_value;
    }


    @Override
    public String toString(){
        return var.toString() + " " + delta_value;
    }

    public boolean is_tabu(int iter){
        Integer tt = var.tabu_table.get(var.value + delta_value);
        return ! (tt==null || tt <iter);
    }



    public boolean is_improving() {

        if (delta_unsat_c < 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = var.hashCode();
        result = 31 * result + delta_value;
        result = 31 * result + delta_unsat_c;
        return result;
    }

    protected static int CompareMove(Move mv1, Move mv2){
        if( mv1==null && mv2 == null){
            return 0;
        }else if(mv1 == null) {
            return -1;
        }else if(mv2 == null) {
            return 1;
        }else if(mv1.delta_unsat_c < mv2.delta_unsat_c){
            return 1;
        }else if(mv1.delta_unsat_c > mv2.delta_unsat_c){
            return -1;
        }else{
            return 0;
        }
    }
}
