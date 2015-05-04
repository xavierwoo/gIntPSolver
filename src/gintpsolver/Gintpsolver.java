package gintpsolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * the general integer problem solver
 * Created by xavierwoo on 2015/4/25.
 */
public class Gintpsolver {

    private Random rand = new Random(2);

    private ArrayList<Variable> vars = new ArrayList<>();
    private ArrayList<Constraint> constraints = new ArrayList<>();
    private ArrayList<Expression> non_var_exps = new ArrayList<>();
    private HashSet<Constraint> unsat_constraints = new HashSet<>();

    private int obj_type = 0;
    private Expression obj = null;

    private Move mv_just_made = null;


    /**
     * Set the objective to be maximization
     *
     * @param e the objective function
     */
    public void set_max_objective(Expression e) {
        obj = e;
        obj_type = 1;
    }

    /**
     * Generate a sum expression
     *
     * @return the expression generated
     */
    public Sum gen_sum() {
        Sum sum = new Sum(rand);
        non_var_exps.add(sum);
        return sum;
    }

    /**
     * Generate a integer decision variable
     *
     * @param name name of the variable
     * @param min  lower bound of the variable
     * @param max  upper bound of the variable
     * @return the variable generated
     */
    public Variable gen_variable(String name, int min, int max) {
        if (is_variable_exist(name)) {
            throw new UnsupportedOperationException("Variable " + name + " already exists!");
        }
        Variable var = new Variable(name, min, max, rand);
        vars.add(var);
        return var;
    }

    /**
     * Generate a boolean decision variable
     *
     * @param name name of the variable
     * @return the variable generated
     */
    public Variable gen_variable(String name) {
        return gen_variable(name, 0, 1);
    }

    private boolean is_variable_exist(String name) {
        for (Variable v : vars) {
            if (v.name.equals(name)) return true;
        }
        return false;
    }

    /**
     * Generate a constraint
     *
     * @param lp the expression on the left
     * @param t  the constraint type
     * @param c  the constant on the right
     * @return the constraint generated
     */
    public Constraint gen_constraint(Expression lp, Constraint.Type t, double c) {
        Constraint constraint = new Constraint(lp, t, c, rand);
        constraints.add(constraint);
        return constraint;
    }

    private void initialization() {
        for (Constraint c : constraints) {
            if (!c.is_satisfied()) unsat_constraints.add(c);
        }
    }

    private Constraint get_random_un_sat_c() {
        int index = rand.nextInt(unsat_constraints.size());
        int i = 0;
        for (Constraint c : unsat_constraints) {
            if (i == index) {
                return c;
            }
            i++;
        }
        throw new UnsupportedOperationException("get_random_un_sat_c() error!");
    }

    private void recursive_calc_exp(Expression exp, Delta delta) {
        exp.is_dirty = true;
        if (exp.in_constraint != null) {
            exp.in_constraint.is_dirty = true;
            if (exp.in_constraint.is_satisfied() && unsat_constraints.contains(exp.in_constraint)) {
                unsat_constraints.remove(exp.in_constraint);
                delta.delta_unsat_c--;
            } else if (!exp.in_constraint.is_satisfied() && !unsat_constraints.contains(exp.in_constraint)) {
                unsat_constraints.add(exp.in_constraint);
                delta.delta_unsat_c++;
            }
        }
        for (Expression e : exp.in_exp) {
            recursive_calc_exp(e, delta);
        }
    }

    /**
     * implement a move and return the delta value
     * @param mv move
     * @return the change value that the move caused
     */
    private Delta make_move(Move mv) {

        Delta delta = new Delta();

        delta.delta_obj = 0-obj.get_value();
        mv.var.value += mv.delta;
        recursive_calc_exp(mv.var, delta);
        delta.delta_obj += obj.get_value();

        mv_just_made = mv;
        return delta;
    }

    private void undo_move() {
        mv_just_made.var.value -= mv_just_made.delta;
        Delta delta = new Delta();
        recursive_calc_exp(mv_just_made.var, delta);
        mv_just_made = null;
    }

    private MovePack eject_chain(Move head_mv,int depth, int max_depth) {
        MovePack mvp = new MovePack();
        mvp.mvs.add(head_mv);

        //make move
        mvp.delta = make_move(head_mv);

        if(depth < max_depth) {


            //Add code to generate the ejection chain


        }

        //roll back move
        undo_move();

        return mvp;
    }


    /**
     * Find moves that can comfort unsatisfied constraints
     * @return the moves
     */
    private ArrayList<Move> find_ease_c_move() {
        ArrayList<Move> mvs;
        Constraint c_to_comfort = get_random_un_sat_c();
        mvs = c_to_comfort.find_all_ease_moves();

        MovePack best_mvp = new MovePack();
        best_mvp.delta = new Delta();
        int count = 1;
        for(Move mv : mvs){
            MovePack mvp = eject_chain(mv, 1, 1);

            double cmp_v = MovePack.compare(best_mvp, mvp, obj_type);
            if(cmp_v < 0){
                best_mvp = mvp;
                count = 1;
            }else if(cmp_v == 0 && rand.nextInt(++count) == 0){
                best_mvp = mvp;
            }
        }

        return best_mvp.mvs;
    }

    /**
     * Find moves that can improve the objective value
     * @return the moves
     */
    private ArrayList<Move> find_improving_move(){
        if(obj_type == 0){
            return null;
        }
        ArrayList<Move> mvs;
        mvs = obj_type == 1 ? obj.find_all_inc_1_mv() : obj.find_all_dec_1_mv();

        MovePack best_mvp = new MovePack();
        best_mvp.delta = new Delta();
        int count = 1;
        for(Move mv : mvs){
            MovePack mvp = eject_chain(mv, 1, 1);

            double cmp_v = MovePack.compare(best_mvp, mvp, obj_type);
            if(cmp_v < 0){
                best_mvp = mvp;
                count = 1;
            }else if (cmp_v == 0 && rand.nextInt(++count)==0){
                best_mvp = mvp;
            }
        }

        return best_mvp.mvs;
    }

    private void improve_obj(){
        ArrayList<Move> mvs;
        mvs = find_improving_move();

        for(Move mv : mvs){
            make_move(mv);
        }
    }

    private void ease_constraint() {
        ArrayList<Move> mvs;
        while (!unsat_constraints.isEmpty()) {
            mvs = find_ease_c_move();

            for(Move mv : mvs){
                make_move(mv);
            }
        }
    }

    private int count_boolean_vars() {
        int num_b = 0;
        for (Variable v : vars) {
            if (v.is_boolean()) num_b++;
        }
        return num_b;
    }

    private void print_problem_summary() {
        String str = "******************************\nProblem Overview:\n";
        if (obj_type == 1) {
            str += "\tA Maximize Problem\n";
        } else if (obj_type == -1) {
            str += "\tA Minimize Problem\n";
        } else {
            str += "\tA Constraint Satisfaction problem\n";
        }

        int num_b = count_boolean_vars();
        if (num_b > 0) {
            str += "\tBoolean Variables : " + num_b;
        }
        if (num_b != vars.size()) {
            str += "\tInteger Variables : " + (vars.size() - num_b);
        }
        str += "\n\tExpression Nodes : " + non_var_exps.size();
        str += "\n******************************\n";
        System.out.println(str);
    }

    /**
     * Solve the problem
     */
    public void solve() {
        initialization();
        print_problem_summary();
        ease_constraint();
        improve_obj();
    }
}
