package com.cacheserverdeploy.deploy;

import java.util.*;

/**
 * Created by magichuang on 17-4-3.
 */
public class GA {
    long timeremain;
    int particlNum;
    double changeSizeP;
    double removeP;
    HashMap<Integer, HashMap<Integer, Link>> thisPartLinks;
    ConsumerNode[] thisPartPay;
    HashSet<Integer> thispartId;
    Helper helper;
    private long TIME_OUT;
    private int serveCost;
    private int size;
    private LinkedList minPath;
    private int minCost;
    private int[] minParticle;
    private int[] init;
    HashSet<Integer> setServer;
    boolean flag;
    HashSet<Integer> notUsed;
    int count;
    int lastMin;

    public GA(int particlNum, double changeSizeP, double removeP, HashMap<Integer,
            HashMap<Integer, Link>> thisPartLinks, ConsumerNode[] thisPartPay,
              HashSet<Integer> thispartId, int serveCost,
              int size, long TIME_OUT, int[] init, HashSet<Integer> setServer) {
        this.particlNum = particlNum;
        this.changeSizeP = changeSizeP;
        this.removeP = removeP;
        this.thisPartLinks = thisPartLinks;
        this.thisPartPay = thisPartPay;
        this.thispartId = thispartId;
        this.setServer = setServer;
        this.flag = true;
        helper = new Helper();
        this.serveCost = serveCost;
        this.size = size;
        this.TIME_OUT = TIME_OUT;
        this.minParticle = new int[size];
        this.init = init;
        this.timeremain = 0;
        this.notUsed = new HashSet<>();
        count = 0;
    }

    public void start() {
        long startTime = System.currentTimeMillis();
        //-----------------生成直连-----------
        HashMap<Integer, Particle> particles = new HashMap<>();
        int directCost = 0;
        int[] directServer = new int[thisPartPay.length];
        int initCost = 0;
        MCMFOPT mcmf = new MCMFOPT(directServer, thisPartLinks, thisPartPay, serveCost);
        if (init.length == 0) {
            for (int i = 0; i < thisPartPay.length; i++) {
                directServer[i] = thisPartPay[i].netNodeID;
            }
            mcmf = new MCMFOPT(directServer, thisPartLinks, thisPartPay, serveCost);
            mcmf.start();
            directCost = mcmf.getAllCost();
        } else {
            mcmf = new MCMFOPT(helper.generateGaserverByparticle(init), thisPartLinks, thisPartPay, serveCost);
            mcmf.start();
            initCost = mcmf.getAllCost();
        }
        //-------------生成个体-------------------
        for (int i = 0; i < particlNum; i++) {
            if (init.length == 0) {
                int[] p = helper.generateParticleByServer(directServer, size);
                Particle put = new Particle(i, p);
                put.setCost(directCost);
                put.setAllPath(mcmf.getAllPath());
                put.setSolve(true);
                particles.put(i, put);
            } else {
                int[] p = init.clone();
                Particle put = new Particle(i, p);
                put.setCost(initCost);
                put.setAllPath(mcmf.getAllPath());
                put.setSolve(true);
                particles.put(i, put);
            }
        }
        minCost = Integer.MAX_VALUE;
        minPath = new LinkedList<>();

        int cur = 0;
        long runTime = 0;
        boolean timeout = false;
        //--------------------------------------------------------------------
        while (runTime < TIME_OUT) {
            LinkedList<Particle> queue = new LinkedList<>();
            ArrayList<Particle> dead = new ArrayList<>();
            for (int i = 0; i < particles.size(); i++) {
                Particle particle = particles.get(i);
                if (particle.getCost() == 0) {
                    mcmf = new MCMFOPT(helper.generateGaserverByparticle(particle.getParticle()), thisPartLinks, thisPartPay, serveCost);
                    mcmf.start();
                    particle.setCost(mcmf.getAllCost());
                    particle.setSolve(mcmf.isSolve());
                    particle.setAllPath(mcmf.getAllPath());
                }
                long endTime = System.currentTimeMillis();
                runTime = endTime - startTime;
                if (runTime > TIME_OUT) {
                    timeout = true;
                    break;
                }
                if (particle.isSolve()) {
                    if (particle.getCost() < minCost) {
                        minParticle = particle.getParticle().clone();
                        minCost = particle.getCost();
                        minPath = new LinkedList<LinkedList<Integer>>();
                        LinkedList<LinkedList<Integer>> toCopy = particle.getAllPath();
                        if (toCopy != null) {
                            for (LinkedList<Integer> iter : toCopy) {
                                LinkedList<Integer> copy = new LinkedList<>();
                                for (int p = 0; p < iter.size(); p++) {
                                    copy.add(iter.get(p));
                                }
                                minPath.add(copy);
                            }
                        }
                    }
                    queue.add(particle);
                } else {
                    dead.add(particle);
                }
            }

            if (queue.size() == 0) continue;
            if (timeout) {
                timeremain = TIME_OUT - (System.currentTimeMillis() - startTime);
                break;
            }
            //------计算所有个体适应度并记录------------
            int rn = 0;
            int removeNum = (int) Math.floor(particlNum * removeP);
            HashSet<Particle> addBuffer = new HashSet<>();
            LinkedList<Integer> bufferId = new LinkedList<>();
            while (rn < removeNum) {
                int length = queue.size();
                Util.sort1(queue, new Comparator<Particle>() {
                    @Override
                    public int compare(Particle o1, Particle o2) {
                        return o2.getCost() - o1.getCost();
                    }
                });
                int m = length / 2;
                double temp1 = 1 - Math.pow(0.5, m);
                double temp2 = Math.pow(0.5, m) - Math.pow(0.5, length);
                double a = 0.8;
                double[] port1 = new double[length - m];
                double[] port2 = new double[m];
                for (int i = 0; i < port1.length; i++) {
                    port1[i] = Math.pow(0.5, length - i) / temp2 * (1 - a);
                }
                for (int i = 0; i < port2.length; i++) {
                    port2[i] = Math.pow(0.5, m - i) / temp1 * a;
                }
                int index = 0;
                double[] parti = new double[port1.length + port2.length];
                double temp = 0.0;
                for (int i = 0; i < parti.length; i++) {
                    if (i < port1.length)
                        parti[i] = temp + port1[i];
                    else
                        parti[i] = temp + port2[i - port1.length];
                    temp = parti[i];
                }

                Iterator<Particle> iterator = queue.iterator();
                while (iterator.hasNext()) {
                    Particle p = iterator.next();
                    p.setP(parti[index++]);
                }
                Particle father = new Particle(-1, new int[]{0});
                Particle mother = new Particle(-1, new int[]{0});
                Iterator<Particle> iterator3 = queue.iterator();
                double choose = Math.random();
                while (iterator3.hasNext()) {
                    Particle p = iterator3.next();
                    if (choose < p.p) {
                        father = p;
                        break;
                    }
                }
                Iterator<Particle> iterator4 = queue.iterator();
                double choose2 = Math.random();
                while (iterator4.hasNext()) {
                    Particle p = iterator4.next();
                    if (choose2 < p.p) {
                        if (mother != father) {
                            mother = p;
                            break;
                        }
                    }
                }
                if (mother.getId() == -1) {
                    mother = father;
                }
                //----------------------------------
                //
                int[] child = new int[size];

                LinkedList<LinkedList<Integer>> fatherallPath = father.getAllPath();
                LinkedList<LinkedList<Integer>> motherallPath = mother.getAllPath();
                HashMap<Integer, ServerInfo> motherServerInfo = helper.getAllServerInfoByPathAndLink(motherallPath, thisPartLinks, serveCost);
                HashMap<Integer, ServerInfo> fatherServerInfo = helper.getAllServerInfoByPathAndLink(fatherallPath, thisPartLinks, serveCost);
                int sumM = 0;
                int sumF = 0;
                for (Map.Entry<Integer, ServerInfo> entry : motherServerInfo.entrySet()) {
                    sumM += entry.getValue().fitness();
                }
                for (Map.Entry<Integer, ServerInfo> entry : fatherServerInfo.entrySet()) {
                    sumF += entry.getValue().fitness();
                }
                int avgM = sumM / motherServerInfo.size();
                int avgF = sumF / fatherServerInfo.size();
                //----------------------------------------------

                for (int i : thispartId) {
                    if (setServer.contains(i)) {
                        child[i] = 1;
                        continue;
                    }
                    if (notUsed.contains(i)) {
                        if (Math.random() < 0.8)
                            child[i] = 0;
                        else {
                            child[i] = 1;
                            notUsed.remove(i);
                        }
                        continue;
                    }
                    if (mother.getParticle()[i] + father.getParticle()[i] == 1) {
                        if (mother.getParticle()[i] == 1) {
                            int fitness = motherServerInfo.get(i).fitness();
                            if (fitness > avgM * 0.7) {
                                if (fitness > avgM * 2) {
                                    notUsed.add(i);
//                                    System.out.println();
//                                    System.out.println("aaaa");
                                }
                                child[i] = 0;
                            } else child[i] = 1;
                        } else {
                            int fitness = fatherServerInfo.get(i).fitness();
                            if (fitness > avgF * 0.7) {
                                if (fitness > avgM * 2) {
                                    notUsed.add(i);
//                                    System.out.println();
//                                    System.out.println("bbbbb");
                                }
                                child[i] = 0;
                            } else child[i] = 1;
                        }
                    } else child[i] = father.getParticle()[i];
                    if (Math.random() < changeSizeP) {
                        if (child[i] == 1) {
                            child[i] = 0;
                        } else {
                            if (cur < 80) {
                                if (Math.random() < 0.01)
                                    child[i] = 1;
                            } else child[i] = 1;

                        }
                    }
                }

                mcmf = new MCMFOPT(helper.generateGaserverByparticle(child), thisPartLinks, thisPartPay, serveCost);
                mcmf.start();
                if (!mcmf.isSolve()) continue;
                int removeId = -1;
                if (!dead.isEmpty()) {
                    removeId = dead.get(0).getId();
                    dead.remove(0);
                    particles.remove(removeId);
                } else {
                    removeId = queue.poll().getId();
                    particles.remove(removeId);
                }
                Particle childPaticle = new Particle(removeId, child);
                childPaticle.setAllPath(mcmf.getAllPath());
                childPaticle.setCost(mcmf.getAllCost());
                childPaticle.setSolve(true);
//                queue.add(childPaticle);
//                particles.put(removeId, childPaticle);
                addBuffer.add(childPaticle);
                bufferId.add(removeId);
                rn++;
            }
            HashSet<Integer> setFlag = new HashSet<>();
            for (Particle p : queue) {
                System.out.printf(cur + "  " + "id: " + p.getId() + " cost: " + p.getCost() + " p: " + p.getP() + " ");
                setFlag.add(p.getCost());
            }
            Iterator<Particle> iterator = addBuffer.iterator();
            for (int i = 0; i < bufferId.size(); i++) {
                int id = bufferId.get(i);
                particles.put(id, iterator.next());
            }

            System.out.println();
            if (lastMin != minCost) {
                lastMin = minCost;
                count = 0;
                changeSizeP = 0.01;

            } else {
                count++;
                if (count > 10) {
                    changeSizeP = 0.05;

                }
            }
            cur++;
        }
        //-------------------------------结束--------------------
        System.out.println(minCost);
        System.out.println(minPath);
    }

    public LinkedList getMinPath() {
        return minPath;
    }

    public int[] getMinParticle() {
        return minParticle;
    }

    public int getMinCost() {
        return minCost;
    }

    public long getTimeremain() {
        return timeremain;
    }

    public HashSet<Integer> getNotUsed() {
        return notUsed;
    }
}
