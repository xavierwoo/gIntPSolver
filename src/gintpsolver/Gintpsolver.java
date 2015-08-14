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

    private Random rand = new Random(0);

    private ArrayList<Variable> vars = new ArrayList<>();
    private HashMap<String, Variable> vars_map = new HashMap<>();
    private ArrayList<Constraint> constraints = new ArrayList<>();
    private ArrayList<Expression> non_var_exps = new ArrayList<>();
    private HashSet<Constraint> unsat_constraints = new HashSet<>();


    private String problem_name;
    private int iter_count = 0;
    private int backup_unsat_c_num;

    private long start_time;
    private long last_time_point;

    private int num_log_printed = 0;


    /**
     * Create a solver
     *
     * @param pn the problem name
     */
    public Gintpsolver(String pn) {
        problem_name = pn;
        File dir = new File(pn);
        if (!dir.mkdir()) {
            System.out.println("WARNING : The folder \"" + pn + "\" may already exist!");
        }
    }

    private void backup() {
        for (Variable v : vars) {
            v.backup();
        }
        backup_unsat_c_num = unsat_constraints.size();
    }

    private void rollback() {
        for (Variable v : vars) {
            v.rollback();
        }
        for (Expression exp : non_var_exps) {
            exp.is_dirty = true;
        }
        unsat_constraints.clear();
        for (Constraint c : constraints) {
            c.is_dirty = true;
            if (!c.is_satisfied()) unsat_constraints.add(c);
        }
    }

    private void write_solution() throws IOException {
        String solution_file =problem_name + "/solution" + ".csv";
        FileWriter outFile = new FileWriter(solution_file);
        PrintWriter printWriter = new PrintWriter(outFile);


        printWriter.println("Variable,Value");
        for (Variable v : vars) {
            printWriter.println(v.name + "," + v.value);
        }

        if (unsat_constraints.isEmpty()) {
            outFile.close();
            return;
        }

        printWriter.println();
        printWriter.println();
        printWriter.println();
        printWriter.println("Unsatisfied constraints:");

        for (Constraint c : unsat_constraints) {
            printWriter.println(c);
        }
        outFile.close();
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
        if (vars_map.get(name) != null) {
            throw new UnsupportedOperationException("Variable " + name + " already exists!");
        }
        Variable var = new Variable(name, min, max, rand);
        vars.add(var);
        vars_map.put(name, var);
        return var;
    }

    public Variable get_variable(String name) {
        return vars_map.get(name);
    }


    /**
     * Create a constraint that lp is not equal to c
     *
     * @param lp the expression
     * @param c  the constant
     * @return the constraint created
     */
    public Constraint subject_to_NEQ(Expression lp, double c) {
        Constraint constraint = new ConstraintNEQ(lp, c, rand);
        constraints.add(constraint);
        return constraint;
    }

    private void initialization() {
        for (Constraint c : constraints) {
            if (!c.is_satisfied()) unsat_constraints.add(c);
        }
        backup_unsat_c_num = unsat_constraints.size();
    }


    private void print_log_head() {

        System.out.format("%15s%15s%15s%15s\n", "Iters", "Un-sat", "Lest unsat", "Elapsed");

        num_log_printed = 0;
    }

    private void print_log() {


        System.out.format("%15s%15s%15s%15s\n", iter_count, unsat_constraints.size(), backup_unsat_c_num, (System.currentTimeMillis() - start_time) / 1000);

        ++num_log_printed;
    }


    private int is_better_solution() {
        if (unsat_constraints.size() > backup_unsat_c_num) {
            return -1;
        } else if (unsat_constraints.size() < backup_unsat_c_num) {
            return 1;
        } else {
            return 0;
        }
    }


    private void print_problem_summary() {
        String str = "******************************\nProblem Overview:\n"
            + "\tA Constraints Satisfaction Problem\n"
            + "\tInteger Variables : " + vars.size()
            + "\n\tExpression Nodes : " + non_var_exps.size()
            + "\n\tConstraints : " + constraints.size()
            + "\n******************************\n";
        System.out.println(str);
    }


    private void local_search_ease_constraint(){

    }

    /**
     * Solve the problem
     */
    public void solve(long time) throws IOException {
        start_time = System.currentTimeMillis();
        last_time_point = start_time;
        initialization();
        print_problem_summary();
        print_log_head();
        print_log();
        //ease_constraint();

        write_solution();
    }
}
