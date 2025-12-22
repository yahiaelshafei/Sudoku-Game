package controller.solver;

public class PermuationGenerator implements Iterator {
    private boolean hasNext = true;
    private int[] permutation = { 1, 1, 1, 1, 1 };
    private boolean x = false;

    public PermuationGenerator() {
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Object next() {
        if (!x) {
            x = true;
            return permutation;
        }
        for (int i = 0; i < permutation.length; i++) {
            permutation[i]++;
            if (permutation[i] == 10) {
                permutation[i] = 1;
                continue;
            }
            if (permutation[i] == 9 && i == permutation.length - 1) { // if the last digit is 9 , then we reached 99999
                hasNext = false;
            }
            break;
        }
        return permutation;
    }
}
