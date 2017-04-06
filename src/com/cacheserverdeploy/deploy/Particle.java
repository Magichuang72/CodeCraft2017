package com.cacheserverdeploy.deploy;

import java.util.LinkedList;

/**
 * Created by magichuang on 17-4-2.
 */
public class Particle {
    int[] particle;
    int cost;
    int realCost;
    int id;
    double p;
    boolean solve;
    LinkedList<LinkedList<Integer>> allPath;

    public Particle(int id, int[] particle) {
        this.particle = particle;
        this.cost = 0;
        this.id = id;
        p = 0;
        this.solve = false;
        this.realCost = 0;

    }

    public void setRealCost(int realCost) {
        this.realCost = realCost;
    }

    public int getRealCost() {
        return realCost;
    }

    public int[] getParticle() {
        return particle;
    }

    public double getP() {
        return p;
    }

    public LinkedList<LinkedList<Integer>> getAllPath() {
        return allPath;
    }

    public void setAllPath(LinkedList<LinkedList<Integer>> allPath) {
        this.allPath = allPath;
    }

    public boolean isSolve() {
        return solve;
    }

    public void setSolve(boolean solve) {
        this.solve = solve;
    }

    public void setParticle(int[] particle) {
        this.particle = particle;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setP(double p) {
        this.p = p;
    }
}
