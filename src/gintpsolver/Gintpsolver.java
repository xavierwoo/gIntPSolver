package gintpsolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * the general integer problem solver
 * Created by xavierwoo on 2015/4/25.
 */
public class Gintpsolver {

    private Random rand = new Random(0);

    private ArrayList<Variable> vars = new ArrayList<>();
    private ArrayList<Constraint> constraints = new ArrayList<>();
    private ArrayList<Expression> non_var_exps = new ArrayList<>();
    private HashSet<Constraint> unsatisfied_constraints = new HashSet<>();

    private Expression max_obj = null;
    private Expression min_obj = null;

    /**
     * Set the objective to be maximization
     * @param e the objective function
     */
    public void set_max_objective(Expression e){
        max_obj = e;
        min_obj = null;
    }

    /**
     * Generate a sum expression
     * @return the expression generated
     */
    public Sum gen_sum(){
        Sum sum = new Sum(rand);
        non_var_exps.add(sum);
        return sum;
    }

    /**
     * Generate a integer decision variable
     * @param name name of the variable
     * @param min lower bound of the variable
     * @param max upper bound of the variable
     * @return the variable generated
     */
    public Variable gen_variable(String name, int min, int max){
        if(is_variable_exist(name)){
            throw new UnsupportedOperationException("Variable " + name + " already exists!");
        }
        Variable var = new Variable(name, min, max, rand);
        vars.add(var);
        return var;
    }

    /**
     * Generate a boolean decision variable
     * @param name name of the variable
     * @return the variable generated
     */
    public Variable gen_variable(String name){
        return gen_variable(name,0,1);
    }

    private boolean is_variable_exist(String name){
        for (Variable v : vars){
            if(v.name.equals(name)){
                return true;
            }
        }
        return false;
    }

    /**
     * Generate a constraint
     * @param lp the expression on the left
     * @param t the constraint type
     * @param c the constant on the right
     * @return the constraint generated
     */
    public Constraint gen_constraint(Expression lp, Constraint.Type t, double c){
        Constraint constraint = new Constraint(lp, t, c, rand);
        constraints.add(constraint);
        return  constraint;
    }

    private void initialization(){
        for(Constraint c : constraints){
            if(!c.is_satisfied()){
                unsatisfied_constraints.add(c);
            }
        }
    }

    private void ease_constraint(){
        for(Constraint c : unsatisfied_constraints){

        }
    }

    /**
     * Solve the problem
     */
    public void solve(){
        initialization();

    }
}
