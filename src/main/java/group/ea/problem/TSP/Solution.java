package group.ea.problem.TSP;

import group.ea.problem.Problem;

import java.util.ArrayList;
import java.util.Map;


public class Solution extends Problem implements Cloneable {

    public double getImprovement;
    public City A1,A2,A3,A4,A5,A6;
    TSPParser _tsp;
    private ArrayList<City> solution = new ArrayList<>();
    private ArrayList<City> prevSolution = new ArrayList<>();

    private ArrayList<City> initialSolution = new ArrayList<>();
    public int optCase;

    public static void setGeneration(int i) {
        generationSolution = i;
    }
    boolean graphicsOn = true;
    ArrayList<ArrayList<City>> solutions = new ArrayList<ArrayList<City>>();

    @Override
    public double computeFitness(String string) {
        return computeFitness();
    }

    private void init() {
        for (Map.Entry<String, double[]> entry : _tsp.getTspCitiesDict().entrySet()) {
            int _id = Integer.parseInt(entry.getKey());
            City temp = new City(_id, entry.getValue());
            solution.add(temp);
        }
        initialSolution = new ArrayList<>(solution);

        solutions.add(solution);
    }
    public void restart(){
        solution = new ArrayList<>(initialSolution);
    }
    public String getName() {
        return name;
    }


    public Solution(TSPParser tsp) {
        name = "TSP";
        _tsp = tsp;
        init();
    }
    public void shuffle(int suffleAmount){
        for(int i = 0; i < suffleAmount; i++){
            int firstIndex = randomIndex();
            int secondIndex = randomIndex();

            while (firstIndex == secondIndex) {
                secondIndex = randomIndex();
            }

            City temp = solution.get(firstIndex);
            solution.set(firstIndex, solution.get(secondIndex));
            solution.set(secondIndex, temp);


        }
    }
    public Solution(){
        init();
    }

    public int getXSolution(int i) {
        return (int) solution.get(i).getX();
    }

    public int getYSolution(int i) {
        return (int) solution.get(i).getY();
    }

    public int getListLength() {
        return solution.size();
    }

    private int randomIndex() {
        return (int) (Math.random() * solution.size());
    }

    public ArrayList<City> getSolution() {
        return solution;
    }

    public TSPParser get_tsp(){
        return _tsp;
    }
    public void set_tsp(TSPParser tsp){
        _tsp = tsp;
    }

    public ArrayList<ArrayList<City>> getSolutions() {
        return solutions;
    }

    public void twoOptMutate() {
        prevSolution = new ArrayList<>(solution);
        if (solution.isEmpty()) {
            System.out.println("solution empty");
        }
        int firstIndex = randomIndex();
        int secondIndex = randomIndex();

        while (firstIndex == secondIndex) {
            secondIndex = randomIndex();
        }
        //reverseSegment(solution, firstIndex, secondIndex);
        //make_2_Opt_move(solution, firstIndex, secondIndex);


        City firstCity = solution.get(firstIndex);
        City secondCity = solution.get(secondIndex);
        //System.out.println("swapping " + firstIndex + "which is " + firstCity.getId() + " with " + secondIndex + "which " + secondCity.getId());
        double gains = gainFrom2Opt(firstCity,solution.get(((firstIndex+1)% solution.size())),solution.get(secondIndex),solution.get(((secondIndex+1) % solution.size())));

        if(graphicsOn){
            //System.out.println(gains + " gains er");
            if(gains > 0){
                //solutions.add(solution);
                // System.out.println("2-opt move optimal and edges saved" + gains);
                // TSPDATA tspdata = new TSPDATA(this, _sl.getSolution(), generationSolution,computeFitness(),gains,firstCity,solution.get(((firstIndex+1)% solution.size())),solution.get(secondIndex),solution.get(((secondIndex+1) % solution.size())), Optional.empty(),Optional.empty(),Optional.empty(),false);
              getImprovement = gains;
                A1 = firstCity;
                A2 = solution.get(((firstIndex+1)% solution.size()));
                A3 = solution.get(secondIndex);
                A4 = solution.get(((secondIndex+1) % solution.size()));

               // System.out.println(generationSolution + " " + computeFitness() + " " + gains + " " + firstCity + " " + solution.get(((firstIndex+1)% solution.size())) + " " + solution.get(secondIndex) + " " + solution.get(((secondIndex+1) % solution.size())));

            } else {
                A1 = null;
                A2 = null;
                A3 = null;
                A4 = null;
            }
        }

       // System.out.println(gains + " Fitness before + " + computeFitness());

        make_2_Opt_move(solution, firstIndex, secondIndex);


      //  System.out.println(gains + " Fitness after+ " + computeFitness());




    }


    public void deepCopy(Solution s){
        this.setSolution(s.getSolution());
        this.A1 = s.A1;
        this.A2 = s.A2;
        this.A3 = s.A3;
        this.A4 = s.A4;
        this.A5 = s.A5;
        this.A6 = s.A6;
        this.optCase = s.optCase;
        this.getImprovement = s.getImprovement;

    }


    public void random3Opt(){
        prevSolution = new ArrayList<>(solution);
        int N = solution.size();
        int i = randomIndex();
        int j = randomIndex();
        int k = randomIndex();
        while (i == j) {
            j = randomIndex();
        }
        while (k == i || k == j) {
            k = randomIndex();
        }
        //if any of them are the same then we need to change them
        if(i == j || k == i || k == j){
            System.out.println("Error: i,j,k are the same");
        }
        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }
        if (j > k) {
            int temp = j;
            j = k;
            k = temp;
        }
        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }
        City X1 = solution.get(i);
        City X2 = solution.get((i + 1) % N);

        City Y1 = solution.get(j);
        City Y2 = solution.get((j + 1) % N);

        City Z1 = solution.get(k);
        City Z2 = solution.get((k + 1) % N);

        Reconnection3OptCase bestOpt = Reconnection3OptCase.OPT3_CASE_0;
        double bestGain = 0;
        //int bestMove = bestMove(X1,X2,Y1,Y2,Z1,Z2);

        //make3OptMove(solution,i,j,k,Reconnection3OptCase.values()[bestMove]);

        for (Reconnection3OptCase optCase : Reconnection3OptCase.values()) {
            double gain = gainFrom3Opt(X1,X2,Y1,Y2,Z1,Z2,optCase);
            // int bestMove = bestMove(X1,X2,Y1,Y2,Z1,Z2);
            //int newFitness = makeBestMove(solution,i,j,k,optCase);
            if(gain > bestGain){
                //System.out.println("gain" + gain);
                bestGain = gain;
                bestOpt = optCase;
            }
        }
        if(graphicsOn && bestGain > 0){
            A1 = X1;
            A2 = X2;
            A3 = Y1;
            A4 = Y2;
            A5 = Z1;
            A6 = Z2;
            getImprovement = bestGain;
            optCase = bestOpt.ordinal();
            //System.out.println("best gain " + bestGain + " opt case "+optCase + "optcase er");

        } else {
            A5 = null;
            A6 = null;
        }
        make3OptMove(solution,i,j,k,bestOpt);



    }
    public void make_2_Opt_move(ArrayList<City> tour,int i,int j) {
        reverseSegment(tour,(i+1) % solution.size(),j);


        /*
        if(graphicsOn){
            double gains = gainFrom2Opt(solution.get(i),solution.get((i+1) % solution.size()),solution.get(j),solution.get((j+1) % solution.size()));
            if(gains < 0){
                System.out.println("2-opt move optimal and edges saved"+ solution.get(i).getId() + " " + solution.get((i+1) % solution.size()).getId() + " " + solution.get(j).getId() + " " + solution.get((j+1) % solution.size()).getId() + "gained+ " + gains);

            }
        }

         */

    }
    public static double gainFrom2Opt(City X1, City X2, City Y1, City Y2) {
        // Gain of tour length that can be obtained by performing given 2-opt move
        // Assumes: X2 == successor(X1); Y2 == successor(Y1)
        double delLength = X1.distanceToCity(X2) + Y1.distanceToCity(Y2);
        double addLength = X1.distanceToCity(Y1) + X2.distanceToCity(Y2);
        return delLength - addLength;
    }

    public int computeFitness() {
        if (solution.isEmpty()) {
            System.err.println("Error: Solution is empty.");
            return 0;
        }

        double fitness = 0;
        for (int i = 0; i < solution.size(); i++) {
            City start = solution.get(i);
            City end = (i + 1 < solution.size()) ? solution.get(i + 1) : solution.get(0);
            double distance = start.distanceToCity(end);
            //System.out.println("Distance from " + start.getX() + " to " + end.getX() + " is " + distance);
            fitness += distance;
        }

        //System.out.println("Total fitness: " + fitness);
        return (int) fitness;
    }

    public int computeFitness(ArrayList<City> solution) {
        if (solution.isEmpty()) {
            System.err.println("Error: Solution is empty.");
            return 0;
        }

        double fitness = 0;
        for (int i = 0; i < solution.size(); i++) {
            City start = solution.get(i);
            City end = (i + 1 < solution.size()) ? solution.get(i + 1) : solution.get(0);
            double distance = start.distanceToCity(end);
            //System.out.println("Distance from " + start.getX() + " to " + end.getX() + " is " + distance);
            fitness += distance;
        }

        //System.out.println("Total fitness: " + fitness);
        return (int) fitness;
    }

    /*
    public int computeFitness() {
        double fitness = 0;
        for (int i = 0; i < solution.size(); i++) {
            City start = solution.get(i);
            City end;
            if (i + 1 < solution.size()) {
                end = solution.get(i + 1);
            } else {
                end = solution.get(0);
            }
            //System.out.println("from start " + start.getId() + " to end: " + end.getId());
            fitness += start.distanceToCity(end);
        }
        return (int) fitness;
    }

     */


    public void revert() {
        solution = prevSolution;
    }

    public void printSolution() {
        for(City c : solution){
            System.out.println("Index " + c.getId() + " coords : " + c.getX() + " " + c.getY());
        }

    }

    public double distanceBetweenIndex(int start, int end){
        return solution.get(start).distanceToCity(solution.get(end));
    }

    public void threeOptMutate() {
        prevSolution = new ArrayList<>(solution);
        int i = randomIndex();
        int j = randomIndex();
        int k = randomIndex();

        while (i == j) {
            j = randomIndex();
        }
        while (k == i || k == j) {
            k = randomIndex();
        }
        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }
        if (j > k) {
            int temp = j;
            j = k;
            k = temp;
        }
        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }

        // Perform all possible 2-opt combinations of the 3-opt
        ArrayList<ArrayList<City>> permutations = new ArrayList<>();
        permutations.add(applyTwoOpt(i, j - 1));
        permutations.add(applyTwoOpt(j, k - 1));
        permutations.add(applyTwoOpt(k, i - 1));
        permutations.add(applyTwoOpt(i, j - 1, j, k - 1));
        permutations.add(applyTwoOpt(i, j - 1, k, i - 1));
        permutations.add(applyTwoOpt(j, k - 1, k, i - 1));
        permutations.add(applyTwoOpt(i, j - 1, j, k - 1, k, i - 1));
        // Select the best permutation
        double bestDistance = computeFitness();
        ArrayList<City> bestPermutation = new ArrayList<>(solution);

        for (ArrayList<City> perm : permutations) {
            solution = new ArrayList<>(perm);
            double distance = computeFitness();
            if (distance < bestDistance) {
                bestDistance = distance;
                bestPermutation = new ArrayList<>(perm);
            }
        }

        solution = new ArrayList<>(bestPermutation);
    }


    private ArrayList<City> applyTwoOpt(int i, int j) {
        ArrayList<City> newSolution = new ArrayList<>(solution);
        while (i < j) {
            City temp = newSolution.get(i);
            newSolution.set(i, newSolution.get(j));
            newSolution.set(j, temp);
            i++;
            j--;
        }
        return newSolution;
    }

    static int generationSolution = 0;


    private ArrayList<City> chooseBestTwoOptMutation() {
        ArrayList<City> bestTour = new ArrayList<>(solution);
        double bestDistance = computeFitness();

        for (int i = 0; i < solution.size() - 1; i++) {
            for (int j = i + 1; j < solution.size(); j++) {
                ArrayList<City> newTour = applyTwoOpt(i, j);
                double newDistance = computeFitness(newTour);


                if (newDistance < bestDistance) {
                    System.out.println("new distance: " + newDistance + " best distance: " + bestDistance + "generation" + generationSolution);
                    System.out.println("swapped");
                    bestTour = newTour;
                    bestDistance = newDistance;
                }
            }
        }

        return bestTour;
    }

    public void TwoOPTMutate() {
        prevSolution = new ArrayList<>(solution);
        double bestDistance = computeFitness();
        double newDist;
        boolean localOptimal = false;
        int iterations = 0;
        int counter_1 = 0;
        int N = solution.size();
        int i,j;

        City X1,X2,Y1,Y2;
        while (!localOptimal) {
            localOptimal = true;

            outerLoop:
            for (counter_1 = 0; counter_1 < solution.size() - 3; counter_1++) {
                i = counter_1;
                X1 = solution.get(i);
                X2 = solution.get((i + 1) % solution.size());

                int counter2limit = (i == 0) ? N - 2 : N - 1;
                for (int counter_2 = i + 2; counter_2 < counter2limit; counter_2++) {
                    generationSolution++;
                   j = counter_2;
                    Y1 = solution.get(j);
                    Y2 = solution.get((j + 1) % solution.size());
                    if (gainFrom2Opt(X1, X2, Y1, Y2) > 0) {

                        //System.out.println("generation" + generationSolution);
                        make_2_Opt_move(solution, i, j);
                        //System.out.println("New fitness "+computeFitness()+ "old fitness"+ bestDistance);

                        localOptimal = false;
                        break outerLoop;
                    }
                    /*
                    generation++;
                    iterations++;
                    //if ((cities.get(i).distance(cities.get(i - 1)) + cities.get(j + 1).distance(cities.get(j))) >=
                    //      (cities.get(i).distance(cities.get(j + 1)) + cities.get(i - 1).distance(cities.get(j))))
                    /*if(solution.get(i).distanceToCity(solution.get(i - 1)) + solution.get(j + 1).distanceToCity(solution.get(j)) >=
                            solution.get(i).distanceToCity(solution.get(j + 1)) + solution.get(i - 1).distanceToCity(solution.get(j))
                    ) {
                    if (computeFitness(swap(newTour, i, j)) < bestDistance) {
                        //System.out.println("swap was better");

                        //System.out.println("closer");
                        newTour = swap(newTour, i, j);
                        iterations++;
                        newDist = computeFitness(newTour);
                        //System.out.println(newDist);
                        if (newDist < bestDistance) {
                            bestDistance = newDist;
                            localOptimal = false;
                            break;
                        }
                        // System.out.println("closer");
                        reverseSegment(newTour, i, j);
                        iterations++;
                        newDist = computeFitness(newTour);
                        //System.out.println(newDist);
                        if (newDist < bestDistance) {
                            bestDistance = newDist;
                            localOptimal = false;
                            break;
                        }
                    }
                    */
                }
            }
        }
        //System.out.println("Two opt iterations" + iterations + "generation" + generation);

    }
    public static int getGeneration(){
        return generationSolution;
    }



    public void setXSolution(int index1, int xSolution) {
        solution.get(index1).setX(xSolution);

    }

    public void setYSolution(int index1, int ySolution) {
        solution.get(index1).setY(ySolution);
    }

    public int getDimension() {
        return _tsp.getDimension();
    }

    public void clearData() {
        A1 = null;
        A2 = null;
        A3 = null;
        A4 = null;
        A5 = null;
        A6 = null;

    }

    public void computeNewList(int[] list) {
        ArrayList<City> temp = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            int cityId = list[i];
            int index = solution.indexOf(solution.get(cityId));
            temp.add(solution.get(index));
        }

        solution = temp;

    }


    public void setSolution(ArrayList<City> s) {
        this.solution = s;
    }


    enum Reconnection3OptCase {
        OPT3_CASE_0, OPT3_CASE_1, OPT3_CASE_2, OPT3_CASE_3,
        OPT3_CASE_4, OPT3_CASE_5, OPT3_CASE_6, OPT3_CASE_7
    }

    private double gainFrom3Opt(City X1, City X2, City Y1, City Y2, City Z1, City Z2, Reconnection3OptCase optCase) {
        double delLength = 0;
        double addLength = 0;

        switch (optCase) {
            case OPT3_CASE_0:
                return 0; // original tour remains without changes

            case OPT3_CASE_1: //  a'bc;  2-opt (i,k)
                delLength = X1.distanceToCity(X2) + Z1.distanceToCity(Z2);
                addLength = X1.distanceToCity(Z1) + X2.distanceToCity(Z2);
                break;

            case OPT3_CASE_2: //  abc';  2-opt (j,k)
                delLength = Y1.distanceToCity(Y2) + Z1.distanceToCity(Z2);
                addLength = Y1.distanceToCity(Z1) + Y2.distanceToCity(Z2);
                break;

            case OPT3_CASE_3: //  ab'c;  2-opt (i,j)
                delLength = X1.distanceToCity(X2) + Y1.distanceToCity(Y2);
                addLength = X1.distanceToCity(Y1) + X2.distanceToCity(Y2);
                break;

            case OPT3_CASE_4: // ab'c'
                addLength = X1.distanceToCity(Y1) + X2.distanceToCity(Z1) + Y2.distanceToCity(Z2);
                delLength = X1.distanceToCity(X2) + Y1.distanceToCity(Y2) + Z1.distanceToCity(Z2);
                break;

            case OPT3_CASE_5: // a'b'c
                addLength = X1.distanceToCity(Z1) + Y2.distanceToCity(X2) + Y1.distanceToCity(Z2);
                delLength = X1.distanceToCity(X2) + Y1.distanceToCity(Y2) + Z1.distanceToCity(Z2);
                break;

            case OPT3_CASE_6: // a'bc'
                addLength = X1.distanceToCity(Y2) + Z1.distanceToCity(Y1) + X2.distanceToCity(Z2);
                delLength = X1.distanceToCity(X2) + Y1.distanceToCity(Y2) + Z1.distanceToCity(Z2);
                break;

            case OPT3_CASE_7: // a'b'c'
                addLength = X1.distanceToCity(Y2) + Z1.distanceToCity(X2) + Y1.distanceToCity(Z2);
                delLength = X1.distanceToCity(X2) + Y1.distanceToCity(Y2) + Z1.distanceToCity(Z2);
                break;
        }


        return delLength - addLength;
    }

    private int bestMove(City a, City b, City c, City d, City e, City f) {
        double delLength = 0;
        double addLength = 0;
        double[] gains = new double[8];
        gains[0] = 0;
        gains[1] = a.distanceToCity(e) + b.distanceToCity(f) - a.distanceToCity(b) + e.distanceToCity(f);
        gains[2] = c.distanceToCity(e) + d.distanceToCity(f) - c.distanceToCity(d) + e.distanceToCity(f);
        gains[3] = a.distanceToCity(c) + b.distanceToCity(d) - a.distanceToCity(b) + c.distanceToCity(d);
        int deletedEdges = (int) (a.distanceToCity(b) + c.distanceToCity(d) + e.distanceToCity(f));
        gains[4] = a.distanceToCity(c) + b.distanceToCity(e) + d.distanceToCity(f) - deletedEdges;
        gains[5] = a.distanceToCity(e) + d.distanceToCity(b) + c.distanceToCity(f) - deletedEdges;
        gains[6] = a.distanceToCity(d) + e.distanceToCity(c) + b.distanceToCity(f) - deletedEdges;
        gains[7] = a.distanceToCity(d) + e.distanceToCity(b) + c.distanceToCity(f) - deletedEdges;
        double maxGain = 0;
        int bestCase = 0;
        for (int i = 1; i < 8; i++) {
            if (gains[i] < 0 && gains[i] < maxGain) {
                bestCase = i;
                maxGain = gains[i];
            }
        }

        return bestCase;
    }

    private void make3OptMove(ArrayList<City> tour, int i, int j, int k, Reconnection3OptCase optCase) {
        switch (optCase) {
            case OPT3_CASE_0:
                break; // No change

            case OPT3_CASE_1:
                reverseSegment(tour, (k + 1) % tour.size(), i);
                break;

            case OPT3_CASE_2:
                reverseSegment(tour, (j + 1) % tour.size(), k);
                break;

            case OPT3_CASE_3:
                reverseSegment(tour, (i + 1) % tour.size(), j);
                break;

            case OPT3_CASE_4:
                reverseSegment(tour, (j + 1) % tour.size(), k);
                reverseSegment(tour, (i + 1) % tour.size(), j);
                break;

            case OPT3_CASE_5:
                reverseSegment(tour, (k + 1) % tour.size(), i);
                reverseSegment(tour, (i + 1) % tour.size(), j);
                break;

            case OPT3_CASE_6:
                reverseSegment(tour, (k + 1) % tour.size(), i);
                reverseSegment(tour, (j + 1) % tour.size(), k);
                break;

            case OPT3_CASE_7:
                reverseSegment(tour, (k + 1) % tour.size(), i);
                reverseSegment(tour, (i + 1) % tour.size(), j);
                reverseSegment(tour, (j + 1) % tour.size(), k);
                break;

        }
    }
    @Override
    public Solution clone() {
        try {
            Solution cloned = (Solution) super.clone();
            cloned.solution = new ArrayList<>(this.solution);
            cloned.prevSolution = new ArrayList<>(this.prevSolution);
            cloned.initialSolution = new ArrayList<>(this.initialSolution);
            // Clone any other mutable fields if necessary
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Should never happen
        }
    }

    private int makeBestMove(ArrayList<City> path, int i, int j, int k, Reconnection3OptCase optCase) {
        int n = path.size();
        int fitnessBefore = computeFitness(path);
        switch (optCase) {
            case OPT3_CASE_1:
                reverseSegment(path, (k + 1) % n, i);
                return computeFitness(path);
            case OPT3_CASE_2:
                reverseSegment(path, (j + 1) % n, k);
                return computeFitness(path);
            case OPT3_CASE_3:
                reverseSegment(path, (i + 1) % n, j);
                return computeFitness(path);
            case OPT3_CASE_4:
                reverseSegment(path, (j + 1) % n, k);
                reverseSegment(path, (i + 1) % n, j);
                return computeFitness(path);
            case OPT3_CASE_5:
                reverseSegment(path, (k + 1) % n, i);
                reverseSegment(path, (i + 1) % n, j);
                return computeFitness(path);
            case OPT3_CASE_6:
                reverseSegment(path, (k + 1) % n, i);
                reverseSegment(path, (j + 1) % n, k);
                return computeFitness(path);
            case OPT3_CASE_7:
                reverseSegment(path, (k + 1) % n, i);
                reverseSegment(path, (i + 1) % n, j);
                reverseSegment(path, (j + 1) % n, k);
                return computeFitness(path);

        }
        return n;
    }

    /*
        private void reverseSegment(ArrayList<City> tour, int start, int end) {
            while (start != end) {
                City temp = tour.get(start);
                tour.set(start, tour.get(end));
                tour.set(end, temp);

                start = (start + 1) % tour.size();
                if (start == end) break;
                end = (end - 1 + tour.size()) % tour.size();
            }
        }*/
    public void reverseSegment(ArrayList<City> tour, int startIndex, int endIndex) {


        int N = tour.size();
        int inversionSize = ((N + endIndex - startIndex + 1) % N) / 2;

        int left = startIndex;
        int right = endIndex;

        for (int counter = 0; counter < inversionSize; counter++) {
            // Swap the cities at the left and right indices
            City temp = tour.get(right);
            tour.set(right, tour.get(left));
            tour.set(left, temp);

            // Move indices towards the center of the segment
            left = (left + 1) % N;
            right = (N + right - 1) % N;


        }


    }

    private ArrayList<City> swap(ArrayList<City> cities, int i, int j) {
        // Create a new list to store the new tour after the swap
        ArrayList<City> newTour = new ArrayList<>();
        int size = cities.size();

        // Add the segment before index i to the new tour
        for (int c = 0; c < i; c++) {
            newTour.add(cities.get(c));
        }

        // Reverse the segment between indices i and j and add to the new tour
        for (int c = j; c >= i; c--) {
            newTour.add(cities.get(c));
        }

        // Add the segment after index j to the new tour
        for (int c = j + 1; c < size; c++) {
            newTour.add(cities.get(c));
        }

        return newTour;
    }
/*
    public void ls3Opt() {
        prevSolution = new ArrayList<>(solution);
        boolean locallyOptimal = false;
        int N = solution.size();
        int c = 0;
        int i, j, k;
        City X1, X2, Y1, Y2, Z1, Z2;
        Reconnection3OptCase[] optCases = new Reconnection3OptCase[]{Reconnection3OptCase.OPT3_CASE_3, Reconnection3OptCase.OPT3_CASE_6, Reconnection3OptCase.OPT3_CASE_7};
        double gainExpected;
        double totalGain = 0;
        int iterations = 0;
        int bestCase = 0;
        if(solution.isEmpty()){
            System.out.println("solution empty before");
        }


        while (!locallyOptimal) {
            locallyOptimal = true;

            outerLoop:
            for (int counter_1 = 0; counter_1 < N - 1; counter_1++) {
                i = counter_1;
                X1 = solution.get(i);
                X2 = solution.get((i + 1) % N);
                for (int counter_2 =  1; counter_2 < N - 3; counter_2++) {
                    j = (i + counter_2) % N;
                    Y1 = solution.get(j);
                    Y2 = solution.get((j + 1) % N);
                    //ændret her fra n-1 til N
                    for (int counter_3 = counter_2 + 1; counter_3 < N - 1; counter_3++) {
                        iterations++;
                        k = (i + counter_3) % N;
                        Z1 = solution.get(k);
                        Z2 = solution.get((k + 1) % N);
                        bestCase = bestMove(X1, X2, Y1, Y2, Z1, Z2);
                        generation++;
                        if (bestCase != 0) { // Make the move if a shorter tour is possible
                            makeMove(solution, i, j, k, Reconnection3OptCase.values()[bestCase]);
                         //   System.out.println(Reconnection3OptCase.values()[bestCase]);
                            locallyOptimal = false;
                            break outerLoop; // Break the loop
                        }

                        /*
                        for (Reconnection3OptCase optCase : Reconnection3OptCase.values()) {
                            c++;
                                gainExpected = gainFrom3Opt(X1, X2, Y1, Y2, Z1, Z2, optCase);
                                if (gainExpected > 0) {
                                    //System.out.println("case: " + optCase + " gain: " + gainExpected);
                                    //System.out.println("gain: " + gainExpected);
                                    totalGain += gainExpected;
                                    make3OptMove(solution, i, j, k, optCase);
                                    System.out.println("fitness"+ computeFitness());
                                    // System.out.println("gain: " + gainExpected);
                                    locallyOptimal = false;
                                    break outerLoop;
                            }
                        }

                    }
                }
            }
        }


        System.out.println("Three opt iterations"+ iterations + "generation" + generation);
        if(solution.isEmpty()){
            System.out.println("solution empty after");
        }
        //System.out.println("total gain: " + totalGain);
    }

 */
public void ls3Opt() {
    prevSolution = new ArrayList<>(solution);
    int iterations = 0;
    boolean locallyOptimal = false;
    int N = solution.size();

    while (!locallyOptimal) {
        locallyOptimal = true;
        outerLoop:
        for (int counter_1 = 0; counter_1 < N - 1; counter_1++) {
            int i = counter_1;
            City X1 = solution.get(i);
            City X2 = solution.get((i + 1) % N);

            for (int counter_2 =  1; counter_2 < N - 3; counter_2++) {
                int j = (i + counter_2) % N;
                City Y1 = solution.get(j);
                City Y2 = solution.get((j + 1) % N);

                for (int counter_3 = counter_2 + 1; counter_3 < N - 1; counter_3++) {
                    int k = (i + counter_3) % N;
                    City Z1 = solution.get(k);
                    City Z2 = solution.get((k + 1) % N);
                    iterations++;
                    //for (Reconnection3OptCase optCase : new Reconnection3OptCase[] {Reconnection3OptCase.OPT3_CASE_3, Reconnection3OptCase.OPT3_CASE_6, Reconnection3OptCase.OPT3_CASE_7}) {
                      for(Reconnection3OptCase optCase : Reconnection3OptCase.values()){
                        generationSolution++;
                        if (optCase != Reconnection3OptCase.OPT3_CASE_0) {
                            double gainExpected = gainFrom3Opt(X1, X2, Y1, Y2, Z1, Z2, optCase);
                            if (gainExpected > 0) {
                                //System.out.println("fitness before more in gainExpected" + computeFitness());
                                make3OptMove(solution, i, j, k, optCase);
                                //System.out.println("fitness after more in gainExpected" + computeFitness());
                                locallyOptimal = false;
                                break outerLoop;
                            }
                        }
                    }
                }
            }
        }

       //System.out.println("Three opt iterations: " + iterations);
    }
   // System.out.println("Three opt generation "+ generation);
}

    private ArrayList<City> applyTwoOpt(int i1, int j1, int i2, int j2) {
        ArrayList<City> newSolution = applyTwoOpt(i1, j1);
        return applyTwoOpt(newSolution, i2, j2);
    }

    private ArrayList<City> applyTwoOpt(ArrayList<City> sol, int i, int j) {
        ArrayList<City> newSolution = new ArrayList<>(sol);
        while (i < j) {
            City temp = newSolution.get(i);
            newSolution.set(i, newSolution.get(j));
            newSolution.set(j, temp);
            i++;
            j--;
        }
        return newSolution;
    }

    private ArrayList<City> applyTwoOpt(int i1, int j1, int i2, int j2, int i3, int j3) {
        ArrayList<City> newSolution = applyTwoOpt(i1, j1);
        newSolution = applyTwoOpt(newSolution, i2, j2);
        return applyTwoOpt(newSolution, i3, j3);
    }

    public void threeOptMutate2() {
        prevSolution = new ArrayList<>(solution);
        solution = chooseBestThreeOptMutation();
    }

    private ArrayList<City> chooseBestThreeOptMutation() {
        generationSolution++;
        ArrayList<City> bestTour = new ArrayList<>(solution);
        double bestDistance = computeFitness();
        int iterations = 0;

        for (int i = 0; i < solution.size() - 2; i++) {
            for (int j = i + 1; j < solution.size() - 1; j++) {
                for (int k = j + 1; k < solution.size(); k++) {
                    iterations++;
                    for (Reconnection3OptCase optCase : Reconnection3OptCase.values()) {
                        ArrayList<City> newTour = new ArrayList<>(solution);
                        make3OptMove(newTour, i, j, k, optCase);
                        double newDistance = computeFitness(newTour);

                        if (newDistance < bestDistance) {
                            bestTour = newTour;
                            bestDistance = newDistance;
                        }
                    }
                }
            }
        }
        System.out.println("best distance: " + bestDistance + " iterations: " + iterations + "solution size " + solution.size() + "generation" + generationSolution);

        return bestTour;
    }


}

