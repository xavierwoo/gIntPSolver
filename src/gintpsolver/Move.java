package gintpsolver;

/**
 * A move to increase or decrease the value of a variable
 * Created by xavierwoo on 2015/4/25.
 */
public class Move {
    Variable var;
    int delta;
    Delta c_o_delta;

    protected Move(Variable v, int d){
        var = v;
        delta = d;
    }

    protected Move reverse(){
        return new Move(var, 0-delta);
    }

    @Override
    public boolean equals(Object o){
        if(o==null || o.getClass()!=this.getClass()){
            return false;
        }
        Move mv_o = (Move) o;
        return var==mv_o.var && delta == mv_o.delta;
    }


    @Override
    public String toString(){
        return var.toString() + " " + delta;
    }

    public boolean is_tabu(int iter){
        Integer tt = var.tabu_table.get(var.value + delta);
        return ! (tt==null || tt <iter);
    }

    @Override
    public int hashCode() {
        int result = var != null ? var.hashCode() : 0;
        result = 31 * result + delta;
        result = 31 * result + (c_o_delta != null ? c_o_delta.hashCode() : 0);
        return result;
    }

    public boolean is_improving(int obj_type){
        if(obj_type == 0){
            if(c_o_delta.delta_unsat_c < 0){
                return true;
            }else{
                return false;
            }
        }else{
            throw new UnsupportedOperationException("write later!");
        }
    }
}
