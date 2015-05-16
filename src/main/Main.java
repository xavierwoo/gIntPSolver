package main;

import gintpsolver.*;

import java.io.*;

/**
 * This file tells you how to use the solver
 * Created by xavierwoo on 2015/4/25.
 */
public class Main {

    static public void general_test() throws IOException {
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

    /**
     * a----b
     * |    | \
     * |    |  e
     * |    |/
     * c----d
     *  Color this graph using 4 colors
     */
    static public void graph_coloring_demo() throws IOException {
        Gintpsolver solver = new Gintpsolver("graph_coloring");
        Variable a = solver.gen_variable("a", 1, 4);
        Variable b = solver.gen_variable("b", 1, 4);
        Variable c = solver.gen_variable("c", 1, 4);
        Variable d = solver.gen_variable("d", 1, 4);
        Variable e = solver.gen_variable("e", 1, 4);

        Sum edge = solver.gen_sum();
        edge.add_element(a, 1);
        edge.add_element(b, -1);
        solver.subject_to_NEQ(edge, 0);

        edge = solver.gen_sum();
        edge.add_element(a, 1);
        edge.add_element(c, -1);
        solver.subject_to_NEQ(edge, 0);

        edge = solver.gen_sum();
        edge.add_element(d, 1);
        edge.add_element(b, -1);
        solver.subject_to_NEQ(edge, 0);

        edge = solver.gen_sum();
        edge.add_element(c, 1);
        edge.add_element(d, -1);
        solver.subject_to_NEQ(edge, 0);

        edge = solver.gen_sum();
        edge.add_element(e, 1);
        edge.add_element(b, -1);
        solver.subject_to_NEQ(edge, 0);

        edge = solver.gen_sum();
        edge.add_element(e, 1);
        edge.add_element(d, -1);
        solver.subject_to_NEQ(edge, 0);

        solver.solve();
    }


    static public void solve_graph_coloring(String filename, int color_num) throws IOException {
        Gintpsolver solver = new Gintpsolver("graph_coloring");

        //read instance file
        FileInputStream fstream = new FileInputStream(filename);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        while((strLine = br.readLine()) != null){
            String[] line = strLine.split(" ");
            if(!line[0].equals("e")){
                continue;
            }
            Variable a = solver.get_variable("v"+line[1]);
            if(a==null){
                a = solver.gen_variable("v"+line[1], 1, color_num);
            }
            Variable b = solver.get_variable("v" + line[2]);
            if(b==null){
                b = solver.gen_variable("v" + line[2], 1, color_num);
            }
            Sum edge_c = solver.gen_sum();
            edge_c.add_element(a, 1);
            edge_c.add_element(b, -1);
            solver.subject_to_NEQ(edge_c, 0);
        }
        solver.solve();
    }

    static public void main(String[] args) throws IOException {

        solve_graph_coloring("graph_coloring_instances/DSJC250.5.col", 28);

    }
}
