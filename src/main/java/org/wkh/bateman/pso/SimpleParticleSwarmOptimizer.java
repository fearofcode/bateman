package org.wkh.bateman.pso;

import java.util.Arrays;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Straightforward implementation of http://en.wikipedia.org/wiki/Particle_swarm_optimization */
public class SimpleParticleSwarmOptimizer {

    private static Logger logger = LoggerFactory.getLogger(SimpleParticleSwarmOptimizer.class.getName());
    private static final int SWARM_SIZE = 30;
    private static final double omega = 0.999; // acceleration coefficient (ω in Wikipedia's velocity equation)
    private static final double c1 = 1.5;
    private static final double c2 = 1.5;
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

        for (int i = 0; i < SWARM_SIZE; i++) {
            for (int d = 0; d < n; d++) {
                x[i][d] = randomRange(rng, xmin[d], xmax[d]);
            }
        }

        v = new double[SWARM_SIZE][n];

        for (int i = 0; i < n; i++) {
            for (int d = 0; d < n; d++) {
                double width = Math.abs(xmax[d] - xmin[d]);
                v[i][d] = 0.0;
            }

        }

        pbest = new double[SWARM_SIZE][n];
        pbestVal = new double[SWARM_SIZE];

        for (int i = 0; i < n; i++) {
            pbest[i] = Arrays.copyOf(x[i], n);
            pbestVal[i] = fitness.evaluate(pbest[i]);
        }

        gbest = x[0];

        gbestVal = fitness.evaluate(gbest);

        for (int i = 1; i < SWARM_SIZE; i++) {
            double[] candidateBest = x[i];
            double candidateBestVal = fitness.evaluate(candidateBest);

            if (candidateBestVal < gbestVal) {
                gbest = Arrays.copyOf(candidateBest, n);
                gbestVal = candidateBestVal;
            }
        }

        logger.info("Particle swarm initialized");
    }

    public double[] learn() {
        for (int generation = 1; generation <= generations; generation++) {
            logger.info("Generation " + generation + ": best value " + gbestVal + " at coords " + Arrays.toString(gbest));

            for (int i = 0; i < SWARM_SIZE; i++) {
                for (int d = 0; d < n; d++) {
                    double r1 = rng.nextDouble();
                    double r2 = rng.nextDouble();

                    v[i][d] = omega * v[i][d] + c1 * r1 * (pbest[i][d] - x[i][d]) + c2 * r2 * (gbest[d] - x[i][d]);
                }

                for (int d = 0; d < n; d++) {
                    x[i][d] += v[i][d];
                    x[i][d] = Math.min(x[i][d], xmax[d]);
                    x[i][d] = Math.max(x[i][d], xmin[d]);
                }

                double candidatePbestVal = fitness.evaluate(x[i]);

                if (candidatePbestVal < pbestVal[i]) {
                    pbestVal[i] = candidatePbestVal;
                    pbest[i] = Arrays.copyOf(x[i], n);
                }

                if (candidatePbestVal < gbestVal) {
                    gbestVal = candidatePbestVal;
                    gbest = Arrays.copyOf(x[i], n);
                }
            }
        }

        return gbest;
    }

    private double randomRange(MersenneTwisterFast rng, double min, double max) {
        return (max - min) * rng.nextDouble() + min;
    }
}
