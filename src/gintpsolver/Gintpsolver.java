package gintpsolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * the general integer problem solver
 * Created by xavierwoo on 2015/4/25.
 */
public class Gintpsolver {

    private Random rand = new Random();

    private ArrayList<Variable> vars = new ArrayList<>();
    private HashMap<String, Variable> vars_map = new HashMap<>();
    private ArrayList<Constraint> constraints = new ArrayList<>();
    private ArrayList<Expression> non_var_exps = new ArrayList<>();
    private HashSet<Constraint> unsat_constraints = new HashSet<>();
    private int obj_type = 0;
    private Expression obj = null;


    private String problem_name;
    private long iter_count = 0;
    private double best_obj = Double.NaN;
    private int backup_unsat_c_num;

    private long start_time;
    private long last_time_point;

    private int num_log_printed = 0;
    /**
     * Create a solver
     * @param pn the problem name
     */
    public Gintpsolver(String pn){
        problem_name = pn;
        File dir = new File(pn);
        if(!dir.mkdir()){
            System.out.println("WARNING : The folder \"" + pn + "\" may already exist!");
        }
    }

    private void backup(){
        for(Variable v : vars){
            v.backup();
        }
        backup_unsat_c_num = unsat_constraints.size();
    }

    private void rollback(){
        for(Variable v : vars){
            v.rollback();
        }
        for(Expression exp : non_var_exps){
            exp.is_dirty = true;
        }
        unsat_constraints.clear();
        for (Constraint c : constraints) {
            c.is_dirty = true;
            if (!c.is_satisfied()) unsat_constraints.add(c);
        }
    }

    private void write_solution() throws IOException {
        String solution_file = obj_type != 0 ?
                problem_name + "/solution_" + obj.get_value() + ".csv"
                : problem_name + "/solution" + ".csv";
        FileWriter outFile = new FileWriter(solution_file);
        PrintWriter printWriter = new PrintWriter(outFile);

        if(obj_type != 0){
            printWriter.println("Objective:," + obj.get_value());
            printWriter.println();
        }

        printWriter.println();
        printWriter.println("Variable,Value");
        for(Variable v : vars){
            printWriter.println(v.name + "," + v.value);
        }

        if(unsat_constraints.isEmpty()){
            outFile.close();
            return;
        }

        printWriter.println();
        printWriter.println();
        printWriter.println();
        printWriter.println("Unsatisfied constraints:");

        for(Constraint c : unsat_constraints){
            printWriter.println(c);
        }
        outFile.close();
    }

    /**
     * Set the objective to be maximization
     *
     * @param e the objective function
     */
    public void set_max_objective(Expression e) {
        obj = e;
        obj_type = 1;
        best_obj = Double.NEGATIVE_INFINITY;
    }

    public void set_min_objective(Expression e){
        obj = e;
        obj_type = -1;
        best_obj = Double.POSITIVE_INFINITY;
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
        if (vars_map.get(name)!=null) {
            throw new UnsupportedOperationException("Variable " + name + " already exists!");
        }
        Variable var = new Variable(name, min, max, rand);
        vars.add(var);
        vars_map.put(name, var);
        return var;
    }

    public Variable get_variable(String name){
        return vars_map.get(name);
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

    /**
     * Create a constraint that lp is greater than c
     * @param lp the expression
     * @param c the constant
     * @return the constraint created
     */
    public Constraint subject_to_GEQ(Expression lp, double c){
        Constraint constraint = new ConstraintGEQ(lp, c);
        constraints.add(constraint);
        return constraint;
    }

    /**
     * Create a constraint that lp is equal to c
     * @param lp the expression
     * @param c the constant
     * @return the constraint created
     */
    public Constraint subject_to_EQ(Expression lp, double c){
        Constraint constraint = new ConstraintEQ(lp, c);
        constraints.add(constraint);
        return constraint;
    }

    /**
     * Create a constraint that lp is less than c
     * @param lp the expression
     * @param c the constant
     * @return the constraint created
     */
    public Constraint subject_to_LEQ(Expression lp, double c){
        Constraint constraint = new ConstraintLEQ(lp, c);
        constraints.add(constraint);
        return constraint;
    }

    /**
     * Create a constraint that lp is not equal to c
     * @param lp the expression
     * @param c the constant
     * @return the constraint created
     */
    public Constraint subject_to_NEQ(Expression lp, double c){
        Constraint constraint = new ConstraintNEQ(lp, c, rand);
        constraints.add(constraint);
        return constraint;
    }

    private void initialization() {
        for (Constraint c : constraints) {
            if (!c.is_satisfied()) unsat_constraints.add(c);
        }
        backup_unsat_c_num = unsat_constraints.size();
        if(obj_type == 0){
            obj = gen_sum();
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

        return delta;
    }

    private void make_all_move(ArrayList<Move> mvs){
        for(Move mv : mvs){
            make_move(mv);
        }
        iter_count++;

        if(obj_type!= 0 && unsat_constraints.isEmpty()){
            if(obj_type == 1 && obj.get_value() > best_obj
                    || obj_type == -1 && obj.get_value() < best_obj){
                best_obj = obj.get_value();
            }
        }

        long curr_time = System.currentTimeMillis();
        if(curr_time - last_time_point >= 5000){
            last_time_point = curr_time;
            if(num_log_printed > 20) {
                print_log_head();
            }
            print_log();
        }

    }


    private void print_log_head(){
//        String str = unsat_constraints.isEmpty() && obj_type != 0 ?
//                "Iters\tBest\tObject" :
//                "Iters\tUn-sat";
//
//
//        System.out.println(str);

        if(unsat_constraints.isEmpty() && obj_type != 0){
            System.out.format("%15s%15s%15s%15s\n", "Iters", "Best", "Objective", "Elapsed");
        }else{
            System.out.format("%15s%15s%15s%15s\n", "Iters", "Un-sat", "Lest unsat", "Elapsed");
        }
        num_log_printed = 0;
    }
    private void print_log(){

        if(unsat_constraints.isEmpty() && obj_type != 0){
            System.out.format("%15s%15s%15s%15s\n", iter_count, best_obj, obj.get_value(), (System.currentTimeMillis() - start_time)/1000);
        }else{
            System.out.format("%15s%15s%15s%15s\n", iter_count, unsat_constraints.size(), backup_unsat_c_num, (System.currentTimeMillis() - start_time)/1000);
        }
        ++num_log_printed;
    }

    private void undo_move(Move mv) {
        mv.var.value -= mv.delta;
        Delta delta = new Delta();
        recursive_calc_exp(mv.var, delta);
    }

    private MovePack eject_chain(Move head_mv,int depth, int max_depth, ArrayList<Move> except_mvs) {
        MovePack mvp = new MovePack();
        mvp.mvs.add(head_mv);

        //make move
        mvp.delta = make_move(head_mv);
        except_mvs.add(head_mv.reverse());
        if(depth < max_depth && !unsat_constraints.isEmpty()) {
            Constraint unsat_c = get_random_un_sat_c();
            Move mv = unsat_c.find_ease_move_randomly(except_mvs);
            MovePack nmp = eject_chain(mv, depth + 1, max_depth, except_mvs);
            if(MovePack.compare(nmp, MovePack.NOTHING, obj_type) > 0){
                mvp.merge(nmp);
            }
        }
        except_mvs.remove(except_mvs.size()-1);

        //roll back move
        undo_move(head_mv);

        return mvp;
    }

    private ArrayList<Constraint> unsat_c_rand_order(){
        ArrayList<Constraint> cs = new ArrayList<>(unsat_constraints);
        Collections.shuffle(cs, rand);
        return cs;
    }

    private int calc_perturb_strength(){
        int similarity = calc_similarity();

        //double perturb_percent = 0.9 * ((double)similarity/100) - 0.4;
        double perturb_percent = 1.3 * ((double)similarity/100) - 0.6;
        perturb_percent = Math.max(perturb_percent, 0.05);
        return (int) ((double)vars.size() * perturb_percent);
    }

    private int calc_similarity(){
        int same_var_num = 0;
        for(Variable v : vars){
            if(v.get_value() == v.getBackup_value()){
                same_var_num++;
            }
        }
        return same_var_num * 100 / vars.size();
    }

    /**
     * Find moves that can comfort unsatisfied constraints
     * @return the moves
     */
    private MovePack find_ease_c_move(int depth) {

        MovePack best_mvp = null;

        int count = 0;
        ArrayList<Constraint> cs = unsat_c_rand_order();
        for(Constraint c_to_comfort : cs) {
            ArrayList<Move> mvs = c_to_comfort.find_all_ease_moves();
            for (Move mv : mvs) {
                MovePack mvp = eject_chain(mv, 1, depth, new ArrayList<>());

                int cmp_value = MovePack.compare(mvp, MovePack.NOTHING, obj_type);
                if( cmp_value > 0){
                    return mvp;
                }
                if(best_mvp == null){
                    best_mvp = mvp;
                    count = 1;
                }else {
                    int cmp_v = MovePack.compare(best_mvp, mvp, obj_type);
                    if (cmp_v < 0 || count == 0) {
                        best_mvp = mvp;
                        count = 1;
                    } else if (cmp_v == 0 && rand.nextInt(++count) == 0) {
                        best_mvp = mvp;
                    }
                }
            }
        }
        return best_mvp;
    }

    /**
     * Find moves that can improve the objective value
     * @return the moves
     */
    private ArrayList<Move> find_improving_move(){
        ArrayList<Move> mvs = obj_type == 1 ? obj.find_all_inc_1_mv() : obj.find_all_dec_1_mv();

        MovePack best_mvp = new MovePack();
        best_mvp.delta = new Delta();
        int count = 1;
        for(Move mv : mvs){
            MovePack mvp = eject_chain(mv, 1, 1, new ArrayList<>());

            int cmp_v = MovePack.compare(best_mvp, mvp, obj_type);
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
        ArrayList<Move> mvs = find_improving_move();

        make_all_move(mvs);
    }

    private void ease_constraint() {
        int max_depth = Math.max(1, vars.size() / 50);
        int depth = 1;
        while (!unsat_constraints.isEmpty()) {
            MovePack mvp = find_ease_c_move(depth);

            int cmp_value = MovePack.compare(mvp, MovePack.NOTHING, obj_type);
            if( cmp_value <= 0 && depth >= max_depth){
                int perturb_strength = calc_perturb_strength();
                //System.out.println("perturb strength : " +perturb_strength);
                rollback();
                perturbation(perturb_strength);
                depth = 1;
                continue;
            }else if(cmp_value <=0){
                ++depth;
            }

            make_all_move(mvp.mvs);
            if(unsat_constraints.size() <= backup_unsat_c_num){
                backup();
            }
        }
    }

    private void perturbation(int strength){
        ArrayList<Move> mvs = new ArrayList<>();
        while(mvs.size() < strength){
            Constraint c = get_random_un_sat_c();
            ArrayList<Variable> vs = c.get_all_variables();
            Variable v = vs.get(rand.nextInt(vs.size()));
            Move mvi = v.find_inc_mv(null);
            Move mvd = v.find_dec_mv(null);
            if(mvi == null && mvd != null){
                mvs.add(mvd);
            }else if(mvi != null && mvd == null){
                mvs.add(mvi);
            }else if(mvi != null){
                mvs.add(rand.nextInt(2)==0 ? mvi : mvd);
            }
        }
        make_all_move(mvs);
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
            str += "\tA Constraints Satisfaction Problem\n";
        }

        int num_b = count_boolean_vars();
        if (num_b > 0) {
            str += "\tBoolean Variables : " + num_b;
        }
        if (num_b != vars.size()) {
            str += "\tInteger Variables : " + (vars.size() - num_b);
        }
        str += "\n\tExpression Nodes : " + non_var_exps.size();

        str += "\n\tConstraints : " + constraints.size();
        str += "\n******************************\n";
        System.out.println(str);
    }

    /**
     * Solve the problem
     */
    public void solve() throws IOException {
        start_time = System.currentTimeMillis();
        last_time_point = start_time;
        initialization();
        print_problem_summary();
        print_log_head();
        ease_constraint();
        write_solution();
        if (obj_type == 0) {
            System.out.println("Problem Solved! Elapsed time : " + (System.currentTimeMillis() - start_time)/1000 + " seconds." );
        } else {
            improve_obj();
            write_solution();
        }
    }
}
