package main;

import gintpsolver.Gintpsolver;
import gintpsolver.Sum;
import gintpsolver.Variable;

import java.io.*;

/**
 * This file tells you how to use the solver
 * Created by xavierwoo on 2015/4/25.
 */
public class Main {

    /**
     * a----b
     * |    | \
     * |    |  e
     * |    |/
     * c----d
     * Color this graph using 4 colors
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

        solver.solve(10000);
    }


    static public void solve_graph_coloring(String filename, int color_num) throws IOException {
        Gintpsolver solver = new Gintpsolver("graph_coloring");

        //read instance file
        FileInputStream fstream = new FileInputStream(filename);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        int[][] net = new int[501][501];
        for (int i = 0; i < net.length; i++) {
            for (int j = 0; j < net[i].length; j++) {
                net[i][j] = 0;
            }
        }
        String strLine;
        while ((strLine = br.readLine()) != null) {
            String[] line = strLine.split(" ");

            if (!line[0].equals("e")) {
                continue;
            }
            Variable a = solver.get_variable(line[1]);
            if (a == null) {
                a = solver.gen_variable(line[1], 1, color_num);
            }
            Variable b = solver.get_variable(line[2]);
            if (b == null) {
                b = solver.gen_variable(line[2], 1, color_num);
            }
            Sum edge_c = solver.gen_sum();
            edge_c.add_element(a, 1);
            edge_c.add_element(b, -1);
            solver.subject_to_NEQ(edge_c, 0);
            Integer ai = Integer.parseInt(line[1]);
            Integer bi = Integer.parseInt(line[2]);
            net[ai][bi] = 1;
        }
        solver.solve(100);
        for (int i = 0; i < net.length; i++) {
            for (int j = 0; j < net[i].length; j++) {
                if(net[i][j] == 1){
                    if(solver.get_variable(String.valueOf(i)).get_value() == solver.get_variable(String.valueOf(j)).get_value()){
                        throw new UnknownError(i+ " "+j);
                    }
                }
            }
        }
    }

    static public void main(String[] args) throws IOException {

        solve_graph_coloring("graph_coloring_instances/DSJC250.5.col", 28);
        //graph_coloring_demo();
    }
}
