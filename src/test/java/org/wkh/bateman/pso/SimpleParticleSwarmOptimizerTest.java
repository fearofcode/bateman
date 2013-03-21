package org.wkh.bateman.pso;

import junit.framework.TestCase;

public class SimpleParticleSwarmOptimizerTest extends TestCase {
    
    public SimpleParticleSwarmOptimizerTest(String testName) {
        super(testName);
    }
    
    public void testLearn() {
        final FitnessFunction fitness = new FitnessFunction() {
            public double evaluate(double[] x) {
                return x[0]*x[0]; // 1d sphere function
            }
        };
        
        SimpleParticleSwarmOptimizer pso = new SimpleParticleSwarmOptimizer(fitness, new double[] {-5.0}, new double[] {5.0}, 500);
        
        assertEquals(fitness.evaluate(pso.learn()), 0.0, 0.001);
    }
}
