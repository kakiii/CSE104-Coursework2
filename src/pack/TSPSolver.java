package pack;

import java.awt.desktop.SystemEventListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class TSPSolver {
    public static ArrayList<City> readFile(String filename) {
        ArrayList<City> cities = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line = null;
            while((line = in.readLine()) != null) {
                String[] blocks = line.trim().split("\\s+");
                if (blocks.length == 3) {
                    City c = new City();
                    c.city = Integer.parseInt(blocks[0]);
                    c.x = Double.parseDouble(blocks[1]);
                    c.y = Double.parseDouble(blocks[2]);
                    //System.out.printf("City %s %f %f\n", c.city, c.x, c.y);
                    cities.add(c);
                } else {
                    continue;
                }
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        City.distances = new double[cities.size()][cities.size()];
        for (int i = 0; i < cities.size(); i++) {
            City ci = cities.get(i);
            for (int j = i; j < cities.size(); j++) {
                City cj = cities.get(j);
                City.distances[i][j] = City.distances[j][i] = Math.sqrt(Math.pow((ci.x - cj.x),2) + Math.pow((ci.y - cj.y),2));
            }
        }
        return cities;
    }

    public static ArrayList<City> solveProblem(ArrayList<City> citiesToVisit) {
        ArrayList<City> routine = new ArrayList<City>();
        City start = null;
        City current = null;
        // get city 0;
        for (int i = 0; i < citiesToVisit.size(); i++) {
            if (citiesToVisit.get(i).city == 0) {
                start = current = citiesToVisit.remove(i);
                routine.add(current);
                break;
            }
        }
        if (current == null) {
            System.out.println("Your problem instance is incorrect! Exiting...");
            System.exit(0);
        }
        // visit cities
        while (!citiesToVisit.isEmpty()) {
            double minDist = Double.MAX_VALUE;
            int index = -1;
            for (int i = 0; i < citiesToVisit.size(); i++) {
                double distI = current.distance(citiesToVisit.get(i));
                // index == -1 is needed in case the distance is really Double.MAX_VALUE.
                if (index == -1 || distI < minDist) {
                    index = i;
                    minDist = distI;
                }
            }
            //int index = 0;

            current = citiesToVisit.remove(index);
            routine.add(current);
        }
        routine.add(start); // go back to 0
        return routine;
    }

    public static double printSolution(ArrayList<City> routine) {
        double totalDistance = 0.0;
        for (int i = 0; i < routine.size(); i++) {
            if (i != routine.size() - 1) {
                System.out.print(routine.get(i).city + "->");
                totalDistance += routine.get(i).distance(routine.get(i+1));
            } else {
                System.out.println(routine.get(i).city);
            }
        }
        return totalDistance;
    }

    /*
        Just evaluate the total distance. A simplified version of printSolution()
     */
    public static double evaluateRoutine(ArrayList<City> routine) {
        double totalDistance = 0.0;
        for (int i = 0; i < routine.size() - 1; i++) {
            totalDistance += routine.get(i).distance(routine.get(i+1));
        }
        return totalDistance;
    }

    /*
        Moves the city at index "from" to index "to" inside the routine
     */
    public static void moveCity(ArrayList<City> routine, int from, int to) {
        // provide your code here.
        if(to-from==1||to==from) return;
        City temp = routine.get(from);
        routine.add(to,temp);
        if(to>from) routine.remove(from);
        else routine.remove(from+1);

    }

    /*
        Evaluate the relocation of city and returns the change in total distance.
        The return value is (old total distance - new total distance).
        As a result, a positive value means that the relocation of city results in routine improvement;
        a negative value means that the relocation leads to worse routine. A zero value means same quality.
     */
    public static double evalMove(ArrayList<City> routine, int from, int to) {
        if(to-from==1||to==from) return 0;
        //System.out.println(diff);
        return routine.get(from - 1).distance(routine.get(from)) + routine.get(from).distance(routine.get(from + 1))
                + routine.get(to - 1).distance(routine.get(to)) - routine.get(from - 1).distance(routine.get(from + 1))
                - routine.get(to - 1).distance(routine.get(from)) - routine.get(from).distance(routine.get(to));
    }

    public static boolean moveFirstImprove(ArrayList<City> routine) {
        // your implementation goes here
        for (int i = 1; i < routine.size() - 1; i++) {
            for (int j = 1; j < routine.size() - 1; j++) {
                //System.out.println("test "+i+" to "+j);
                double diff = evalMove(routine, i, j);
                if (diff - 0.00001 > 0) { // I really mean diff > 0 here
                    //System.out.println("move "+i+" to "+j+ ", Diff is "+ diff);
                    moveCity(routine, i, j);

                    return true;
                }
            }
        }
        return false;
    }


    public static void swapCity(ArrayList<City> routine, int index1, int index2) {
        City temp = routine.get(index1);
        routine.set(index1,routine.get(index2));
        routine.set(index2,temp);
        // your implementation goes here
    }

    /*
        Can you improve the performance of this method?
        You are allowed to change the implementation of this method and add other methods.
        but you are NOT allowed to change its method signature (parameters, name, return type).
     */
    public static double evalSwap(ArrayList<City> routine, int index1, int index2) {
        /*double oldDistance = evaluateRoutine(routine);
        swapCity(routine, index1, index2);
        double newDistance = evaluateRoutine(routine);
        swapCity(routine, index1, index2);
        System.out.println("swap difference is "+(oldDistance - newDistance));
        return oldDistance - newDistance;*/
        if(Math.abs(index1-index2)==1){
        return routine.get(index1).distance(routine.get(index1-1))+routine.get(index2).distance(routine.get(index2+1))
                    -routine.get(index2).distance(routine.get(index1-1))-routine.get(index1).distance(routine.get(index2+1));}

        return routine.get(index1).distance(routine.get(index1-1))+routine.get(index1).distance(routine.get(index1+1))
                +routine.get(index2).distance(routine.get(index2-1))+routine.get(index2).distance(routine.get(index2+1))
                -routine.get(index1).distance(routine.get(index2-1))-routine.get(index1).distance(routine.get(index2+1))
                -routine.get(index2).distance(routine.get(index1-1))-routine.get(index2).distance(routine.get(index1+1));
        //System.out.println("swap difference is "+dif);

    }

    /*
        This function iterate through all possible swapping positions of cities.
            if a city swap is found to lead to shorter travelling distance, that swap action
            will be applied and the function will return true.
            If there is no good city swap found, it will return false.
     */
    public static boolean swapFirstImprove(ArrayList<City> routine) {
        for (int i = 1; i < routine.size() - 1; i++) {
            for (int j = i + 1; j < routine.size() - 1; j++) {
                //System.out.println("test "+i+" and "+j);
                double diff = evalSwap(routine, i, j);
                if (diff - 0.00001 > 0) { // I really mean diff > 0 here
                    //System.out.println("swap "+i+" and "+j+ ", Diff is "+ diff);
                    swapCity(routine, i, j);

                    return true;
                }
            }
        }
        return false;
    }
    // You know 2-opt, right?
    public static void twoOptCity(ArrayList<City> routine,int index1,int index2){
        ArrayList<City> temp = new ArrayList<>();
        for(int i=index1;i<=index2;i++){
            temp.add(routine.get(i));
        }
        Collections.reverse(temp);
        for(int i=0;i<temp.size();i++){
            routine.set(index1+i,temp.get(i));
        }
    }

    public static double evalTwoOpt(ArrayList<City> routine,int index1,int index2){
        if(index1==index2||Math.abs(index1-index2)==1) return evalSwap(routine,index1,index2);

        return routine.get(index1).distance(routine.get(index1-1))+routine.get(index2).distance(routine.get(index2+1))
                -routine.get(index2).distance(routine.get(index1-1))-routine.get(index1).distance(routine.get(index2+1));
    }

    public static boolean twoOptFirstImprove(ArrayList<City> routine) {
        for (int i = 1; i < routine.size() - 1; i++) {
            for (int j = i + 1; j < routine.size() - 1; j++) {
                //System.out.println("test "+i+" and "+j);
                double diff = evalTwoOpt(routine, i, j);
                if (diff - 0.00001 > 0) { // I really mean diff > 0 here
                    //System.out.println("swap "+i+" and "+j+ ", Diff is "+ diff);
                    twoOptCity(routine, i, j);

                    return true;
                }
            }
        }
        return false;
    }
    //Create 3 neighborhoods to query possible improvement in turn.
    // N1 using swap, N2 using move, N3 using 2-opt
    public static ArrayList<City> neighborHood1(ArrayList<City> routine){
        int max=20;
        int count;
        for(count=0;count<max;count++){
            count=swapFirstImprove(routine)?0:count;

        }
        //System.out.println("Neighborhood 1: "+count+" times");
        return routine;
    }

    public static ArrayList<City> neighborHood2(ArrayList<City> routine){
        int max=20;
        int count;
        for(count=0;count<max;count++){
            count=moveFirstImprove(routine)?0:count;

        }
        //System.out.println("Neighborhood 2: "+count+" times");
        return routine;
    }

    public static ArrayList<City> neighborHood3(ArrayList<City> routine){
        int max=20;
        int count;
        for(count=0;count<max;count++){
            count=twoOptFirstImprove(routine)?0:count;

        }
        //System.out.println("Neighborhood 3: "+count+" times");
        return routine;
    }

    public static ArrayList<City> VNSearch(ArrayList<City> routine){
        int l=1;
        while(l<4){
            switch (l){
                case 1:
                    ArrayList<City> temp1=neighborHood1(routine);
                    if(evaluateRoutine(temp1)<evaluateRoutine(routine)){
                        routine=temp1;
                        l=1;
                    }else l+=1;
                    break;
                case 2:
                    ArrayList<City> temp2=neighborHood2(routine);
                    if(evaluateRoutine(temp2)<evaluateRoutine(routine)){
                        routine=temp2;
                        l=1;
                    }else l+=1;
                    break;
                case 3:
                    ArrayList<City> temp3=neighborHood3(routine);
                    if(evaluateRoutine(temp3)<evaluateRoutine(routine)){
                        routine=temp3;
                        l=1;
                    }else l+=1;
                    break;
            }


        }
        return routine;
    }

    public static ArrayList<City> shaking(ArrayList<City> routine){

        Random rd= new Random();
        int pos1=1+ rd.nextInt(routine.size()/4);
        int pos2= pos1+1+rd.nextInt(routine.size()/4);
        int pos3= pos2+1+rd.nextInt(routine.size()/4);
        ArrayList<City> newRoutine =  new ArrayList<City>();
        for(int i=0;i<pos1;i++){
            newRoutine.add(routine.get(i));
        }

        for(int i=pos3;i<routine.size()-1; i++){
            newRoutine.add(routine.get(i));
        }
        for(int i=pos2;i<pos3;i++){
            newRoutine.add(routine.get(i));
        }
        for(int i=pos1;i<pos2;i++){
            newRoutine.add(routine.get(i));
        }
        newRoutine.add(routine.get(0));
        return newRoutine;
    }

    public static ArrayList<City> improveRoutine(ArrayList<City> routine) {
        //Using VNS
            int max=10;
            int count =0;
        long startTime = System.currentTimeMillis();
        do{
            count++;
            ArrayList<City> temp= shaking(routine);
            temp=VNSearch(temp);
            if(evaluateRoutine(temp)<evaluateRoutine(routine)){
                routine=temp;
                count=0;
            }

        } while(count<=max);
        long endTime = System.currentTimeMillis();
        System.out.println("Time: "+(endTime-startTime)/1000+"s");
        return routine;
    }


}
