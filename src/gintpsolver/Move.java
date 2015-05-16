package gintpsolver;

/**
 * A move to increase or decrease the value of a variable
 * Created by xavierwoo on 2015/4/25.
 */
public class Move {
    Variable var;
    int delta;

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
    public int hashCode() {
        int result = var.hashCode();
        result = 31 * result + delta;
        return result;
    }
}
