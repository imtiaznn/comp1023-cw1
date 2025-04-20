package comp1023.loadeddice;

import com.badlogic.gdx.math.MathUtils;

public class Dice {
    private final int[] probabilities;
    private final int sides;
    
    public Dice() {
        this.sides = 6;
        this.probabilities = new int[sides];
        resetProbabilities();
    }
    
    private void resetProbabilities() {
        for (int i = 0; i < sides; i++) {
            probabilities[i] = 1; // Equal probability
        }
    }
    
    public int roll() {
        // Calculate total weight
        int totalWeight = 0;
        for (int weight : probabilities) {
            totalWeight += weight;
        }
        
        // Generate random number
        int random = MathUtils.random(0, totalWeight - 1);
        
        // Find which side was rolled
        int currentWeight = 0;
        for (int i = 0; i < sides; i++) {
            currentWeight += probabilities[i];
            if (random < currentWeight) {
                return i + 1; // Return 1-6
            }
        }
        
        return 1; // Fallback
    }
    
    public void modifyProbability(int side, int modifier) {
        if (side >= 1 && side <= sides) {
            probabilities[side - 1] += modifier;
        }
    }
    
    public void resetProbabilitiesToDefault() {
        resetProbabilities();
    }
} 