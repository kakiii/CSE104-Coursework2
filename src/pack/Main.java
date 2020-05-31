package pack;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
	// write your code here

        ArrayList<City> cities = TSPSolver.readFile("res/C210_1.TXT");
        cities = TSPSolver.solveProblem(cities);
        System.out.printf("Distances: %f\n", TSPSolver.printSolution(cities));
        cities = TSPSolver.improveRoutine(cities);
        //TSPSolver.moveCity(cities,4,2);
        System.out.printf("After improving: %f\n", TSPSolver.printSolution(cities));

    }
}
