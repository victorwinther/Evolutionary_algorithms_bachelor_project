package group.ea.structure.TSP;

import group.ea.structure.problem.Problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class Solution extends Problem {

    TSPParser _tsp;
    private ArrayList<City> solution = new ArrayList<>();
    private ArrayList<City> prevSolution = new ArrayList<>();

    @Override
    public double computeFitness(String string) {
        return 0;
    }

    private void init() {
        for (Map.Entry<String, double[]> entry : _tsp.getTspCitiesDict().entrySet()) {
            int _id = Integer.parseInt(entry.getKey());
            City temp = new City(_id, entry.getValue());
            solution.add(temp);
        }
    }

    public Solution(TSPParser tsp) {
        _tsp = tsp;
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

    public void twoOptMutate() {
        prevSolution = new ArrayList<>(solution);
        int firstIndex = randomIndex();
        int secondIndex = randomIndex();

        while (firstIndex == secondIndex) {
            secondIndex = randomIndex();
        }

        City firstCity = solution.get(firstIndex);
        City secondCity = solution.get(secondIndex);
        //System.out.println("swapping " + firstIndex + "which is " + firstCity.getId() + " with " + secondIndex + "which " + secondCity.getId());
        solution.set(firstIndex, secondCity);
        solution.set(secondIndex, firstCity);
    }

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

    public void revert() {
        solution = prevSolution;
    }

    public void printSolution() {

        for (City c : solution) {
            System.out.println(c.getId());
        }
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
                break;

            case OPT3_CASE_5: // a'b'c
                addLength = X1.distanceToCity(Z1) + Y2.distanceToCity(X2) + Y1.distanceToCity(Z2);
                break;

            case OPT3_CASE_6: // a'bc'
                addLength = X1.distanceToCity(Y2) + Z1.distanceToCity(Y1) + X2.distanceToCity(Z2);
                break;

            case OPT3_CASE_7: // a'b'c'
                addLength = X1.distanceToCity(Y2) + Z1.distanceToCity(X2) + Y1.distanceToCity(Z2);
                break;
        }

        if (optCase.ordinal() >= Reconnection3OptCase.OPT3_CASE_4.ordinal()) {
            delLength = X1.distanceToCity(X2) + Y1.distanceToCity(Y2) + Z1.distanceToCity(Z2);
        }

        return delLength - addLength;
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

    private void reverseSegment(ArrayList<City> tour, int start, int end) {
        while (start != end) {
            City temp = tour.get(start);
            tour.set(start, tour.get(end));
            tour.set(end, temp);

            start = (start + 1) % tour.size();
            if (start == end) break;
            end = (end - 1 + tour.size()) % tour.size();
        }
    }
    public void ls3Opt() {
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

                        for (Reconnection3OptCase optCase : new Reconnection3OptCase[] {Reconnection3OptCase.OPT3_CASE_3, Reconnection3OptCase.OPT3_CASE_6, Reconnection3OptCase.OPT3_CASE_7}) {
                            if (optCase != Reconnection3OptCase.OPT3_CASE_0) {
                                double gainExpected = gainFrom3Opt(X1, X2, Y1, Y2, Z1, Z2, optCase);
                                if (gainExpected > 0) {
                                    make3OptMove(solution, i, j, k, optCase);
                                    locallyOptimal = true;
                                    break outerLoop;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
