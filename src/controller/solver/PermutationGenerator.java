package controller.solver;

public class PermutationGenerator implements Iterator {
    private boolean hasNext = true;
    private int[] permutation;
    private boolean firstCall = true;

    public PermutationGenerator(int size) {
        this.permutation = new int[size];
        for (int i = 0; i < size; i++) {
            permutation[i] = 1;
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Object next() {
        if (firstCall) {
            firstCall = false;
            return permutation;
        }
        for (int i = 0; i < permutation.length; i++) {
            permutation[i]++;

            if (permutation[i] <= 9) {
                if (i == permutation.length - 1 && permutation[i] == 9) {
                    hasNext = false;
                }
                break;
            } else {
                permutation[i] = 1;

                if (i == permutation.length - 1) {
                    hasNext = false;
                }
            }
        }

        return permutation;
    }
}