package org.wkh.bateman.pso;

import java.util.Arrays;
import java.util.Date;

/* Straightforward implementation of http://en.wikipedia.org/wiki/Particle_swarm_optimization */
public class SimpleParticleSwarmOptimizer {
    private static final int SWARM_SIZE = 30;
    private static final double omega = 0.999; // acceleration coefficient (ω in Wikipedia's velocity equation)
    private static final double c1=2.0;
    private static final double c2=2.0;
    
    private MersenneTwisterFast rng;
    
    // this might seem a poor abstraction, but actually it winds up being less 
    // tedious than having an array of objects
    // this is because Java is a fucking piece of shit
    private double[][] x;
    private double[][] v;
    private double[][] pbest;
    private double[] pbestVal;
    
    private double[] gbest;
    private double gbestVal;
    private int n;
    
    private FitnessFunction fitness;
    private double[] xmin;
    private double[] xmax;
    private int generations;
    
    
    public SimpleParticleSwarmOptimizer(FitnessFunction fitness, double[] xmin, double[] xmax, int generations) {
        this.fitness = fitness;
        this.xmin = xmin;
        this.xmax = xmax;
        this.generations = generations;
        
        this.n = xmin.length;
        
        x = new double[SWARM_SIZE][n];
      
        rng = new MersenneTwisterFast();
        
        for(int i = 0; i < SWARM_SIZE; i++) {
            for(int d = 0; d < n; d++) {
                x[i][d] = randomRange(rng, xmin[d], xmax[d]);
            }
        }
        
        v = new double[SWARM_SIZE][n];
        
        for(int i = 0; i < n; i++) {
            for(int d = 0; d < n; d++) {
                double width = Math.abs(xmax[d]-xmin[d]);
                v[i][d] = 0.0;
            }
            
        }
        
        pbest = new double[SWARM_SIZE][n];
        pbestVal = new double[SWARM_SIZE];
        
        for(int i = 0; i < n; i++) {
            pbest[i] = Arrays.copyOf(x[i], n);
            pbestVal[i] = fitness.evaluate(pbest[i]);
        }
        
        gbest = x[0];
        
        gbestVal = fitness.evaluate(gbest);
        
        for(int i = 1; i < SWARM_SIZE; i++) {
            double[] candidateBest = x[i];
            double candidateBestVal = fitness.evaluate(candidateBest);
            
            if(candidateBestVal < gbestVal) {
                gbest = candidateBest;
                gbestVal = candidateBestVal;
            }
        }
    }
    
    public double[] learn() {
        for(int generation = 1; generation <= generations; generation++) {
            
            for(int i = 0; i < SWARM_SIZE; i++) {
                for(int d = 0; d < n; d++) {
                    double r1 = rng.nextDouble();
                    double r2 = rng.nextDouble();
                    
                    v[i][d] = omega*v[i][d]*(generations-generation)/generations + c1*r1*(pbest[i][d]-x[i][d]) + c2*r2*(gbest[d]-x[i][d]);
                }
            
                for(int d = 0; d < n; d++) {
                    x[i][d] += v[i][d];
                }
                
                double candidatePbestVal = fitness.evaluate(x[i]);
                
                if(candidatePbestVal < pbestVal[i]) {
                    pbestVal[i] = candidatePbestVal;
                    pbest[i] = x[i];
                }
                
                if(candidatePbestVal < gbestVal) {
                    gbestVal = candidatePbestVal;
                    gbest = x[i];
                }
            }
        }
        
        return gbest;
    }
    
    private double randomRange(MersenneTwisterFast rng, double min, double max) {
        return (max-min)*rng.nextDouble()+min;
    }
    
}
