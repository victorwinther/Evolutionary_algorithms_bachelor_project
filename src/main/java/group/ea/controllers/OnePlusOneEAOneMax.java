package group.ea.controllers;

public class OnePlusOneEAOneMax {

    private static int stringLength = 100; // Length of the binary string

    // Fitness function for the OneMax problem: counts the number of 1's in the binary string
    private static int fitness(String individual) {
        int count = 0;
        for (int i = 0; i < individual.length(); i++) {
            if (individual.charAt(i) == '1') {
                count++;
            }
        }
        return count;
    }

    // Function to initialize a random binary string
    private static String initializeIndividual(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(Math.random() > 0.5 ? '1' : '0');
        }
        return sb.toString();
    }

    // Mutation function: flips a random bit in the binary string
    private static String mutate(String parent) {
        int mutateIndex = (int) (Math.random() * parent.length());
        char[] chars = parent.toCharArray();
        chars[mutateIndex] = chars[mutateIndex] == '0' ? '1' : '0';
        return new String(chars);
    }

    public static void main(String[] args) {
        String parent = initializeIndividual(stringLength); // Initialize the parent
        int bestFitness = fitness(parent);
        int maxGenerations = 1000; // Stopping condition

        System.out.println("Initial Solution: " + parent + " with fitness: " + bestFitness);

        for (int generation = 1; generation <= maxGenerations; generation++) {
            String offspring = mutate(parent); // Create offspring by mutation
            int offspringFitness = fitness(offspring);

            // Selection: if the offspring is better, it becomes the new parent
            if (offspringFitness > bestFitness) {
                parent = offspring;
                bestFitness = offspringFitness;
                System.out.println("Generation " + generation + ": New solution found: " + parent + " with fitness: " + bestFitness);
            }

            // Stopping condition: perfect fitness achieved
            if (bestFitness == stringLength) {
                System.out.println("Perfect solution found in generation " + generation);
                break;
            }
        }

        System.out.println("Final Solution: " + parent + " with fitness: " + bestFitness);
    }
}

