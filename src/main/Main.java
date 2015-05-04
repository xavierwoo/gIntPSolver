package main;

import gintpsolver.Constraint;
import gintpsolver.Gintpsolver;
import gintpsolver.Sum;
import gintpsolver.Variable;

import java.io.IOException;

/**
 * Created by xavierwoo on 2015/4/25.
 */
public class Main {

    static public void main(String[] args) throws IOException {

        Gintpsolver solver = new Gintpsolver("test");

        Variable x1 = solver.gen_variable("x1");
        Variable x2 = solver.gen_variable("x2");
        Variable x3 = solver.gen_variable("x3");

        Sum exp1 = solver.gen_sum();
        exp1.add_element(x2, 1);
        exp1.add_element(x3, 1);
        solver.subject_to_LEQ(exp1, 1);

        Sum exp2 = solver.gen_sum();
        exp2.add_element(x1, 1);
        exp2.add_element(x3, 1);
        solver.subject_to_LEQ(exp2, 1);

        Sum exp3 = solver.gen_sum();
        exp3.add_element(x1, 1);
        exp3.add_element(x2, 1);
        exp3.add_element(x3, -1);
        solver.subject_to_GEQ(exp3, 0);

        Sum obj = solver.gen_sum();
        obj.add_element(x1, 1);
        obj.add_element(exp1, 1);

        solver.set_max_objective(obj);

        solver.solve();

    }
}
