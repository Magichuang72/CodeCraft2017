package com.cacheserverdeploy.deploy;

import java.util.*;

/**
 * Created by magichuang on 17-3-29.
 */
public class Helper {
    public int[] generateRandomArray(int size, int max) {
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = (int) Math.floor(Math.random() * max);
        }
        return result;
    }

    public int generateServerByHop(int start, int hop, HashMap<Integer, LinkedList<Integer>> aroundNode) {
        int cur = start;
        for (int i = 0; i < hop; i++) {
            LinkedList<Integer> list = aroundNode.get(cur);
            int choose = (int) Math.floor(Math.random() * list.size());
            cur = list.get(choose);
        }
        return cur;
    }

    public int[] generateGaparticle(int size, int max1) {
        int[] result = new int[size];
        int serverSize = (int) Math.floor(Math.random() * (max1 + 1));
        int[] temp = generateRandomArray(serverSize, size);
        for (int i = 0; i < serverSize; i++) {
            result[temp[i]] = 1;
        }

        return result;
    }

    public int[] generateGaserverByparticle(int[] particle) {
        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < particle.length; i++) {
            if (particle[i] == 1) list.add(i);
        }
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public int[] generateParticleByServer(int[] server, int size) {
        int[] pa = new int[size];
        for (int i = 0; i < server.length; i++) {
            pa[server[i]] = 1;
        }
        return pa;
    }

    public int[] generateParicleBySeverSet(HashSet<Integer> serverSet, int size) {
        int[] pa = new int[size];
        for (int temp : serverSet) {
            pa[temp] = 1;
        }
        return pa;
    }

    public HashMap<Integer, HashMap<Integer, Link>> getPartLinksById(HashMap<Integer, HashMap<Integer, Link>> allLinks, HashSet<Integer> partId) {
        int size = allLinks.size();
        HashMap<Integer, HashMap<Integer, Link>> result = new HashMap<>();
        for (int i = 0; i < size; i++) {
            HashMap<Integer, Link> temp = allLinks.get(i);
            result.put(i, new HashMap<Integer, Link>());
            if (partId.contains(i)) {
                for (Map.Entry<Integer, Link> iter : temp.entrySet()) {
                    int to = iter.getKey();
                    Link copy = iter.getValue();
                    if (partId.contains(to)) {
                        result.get(i).put(to, copy);
                    }
                }
            }
        }
        return result;
    }

    public ConsumerNode[] getPartConsumerById(ConsumerNode[] allPay, HashSet<Integer> consumerId) {
        int index = 0;
        ConsumerNode[] result = new ConsumerNode[consumerId.size()];
        for (ConsumerNode cn : allPay) {
            if (consumerId.contains(cn.netNodeID)) {
                result[index++] = cn;
            }
        }
        return result;
    }

    public HashMap<Integer, HashSet<Integer>> spiltGraph(HashMap<Integer, HashMap<Integer, Link>> links, HashSet<Integer> ConsumerId, HashSet<Integer> allPartId) {
        boolean cut = false;
        int size = allPartId.size();

        HashSet<Integer> part2ConsumerIdTemp = new HashSet<>();
        HashSet<Integer> part1ConsumerIdTemp = new HashSet<>();
        HashSet<Integer> part1IdTemp = new HashSet<>();
        HashSet<Integer> part2IdTemp = new HashSet<>();
        HashMap<Integer, HashSet<Integer>> result = new HashMap<>();
        while (!cut) {
            int randomStart = (int) Math.floor(Math.random() * size);
            while (!allPartId.contains(randomStart)) randomStart = (int) Math.floor(Math.random() * size);
            part2ConsumerIdTemp = new HashSet<>();
            part1ConsumerIdTemp = new HashSet<>();
            part1IdTemp = new HashSet<>();
            part2IdTemp = new HashSet<>();
            Spfa spfa = new Spfa();

            int[] dis = spfa.getDis(randomStart, links);
            for (int i = 0; i < dis.length; i++) {
                if (allPartId.contains(i)) {
                    if (dis[i] < 10) {
                        part1IdTemp.add(i);
                        if (ConsumerId.contains(i)) {
                            part1ConsumerIdTemp.add(i);
                        }
                    } else {
                        part2IdTemp.add(i);
                        if (ConsumerId.contains(i)) {
                            part2ConsumerIdTemp.add(i);
                        }
                    }
                }
            }
            if (Math.abs(part1IdTemp.size() - part2IdTemp.size()) < size * 0.2 && part1IdTemp.size() + part2IdTemp.size() == size) {
                if (Math.abs(part1ConsumerIdTemp.size() - part2ConsumerIdTemp.size()) < ConsumerId.size() * 0.2)
                    cut = true;
            }
            if (cut == true) break;
        }
        result.put(0, part1IdTemp);
        result.put(1, part2IdTemp);
        result.put(2, part1ConsumerIdTemp);
        result.put(3, part2ConsumerIdTemp);
        return result;
    }


    public HashMap<Integer, HashSet<Integer>> spiltGraphN(HashMap<Integer, HashMap<Integer, Link>> links, HashSet<Integer> consumerId, int n) {
        boolean cut = false;
        HashMap<Integer, HashSet<Integer>> result = new HashMap<>();
        int avg = consumerId.size() / n;
        while (!cut) {
            int j = 0;
            result = new HashMap<>();
            HashSet<Integer>[] allSet = new HashSet[n];
            HashSet<Integer>[] consumerSet = new HashSet[n];
            for (int l = 0; l < n; l++) {
                allSet[l] = new HashSet<>();
                consumerSet[l] = new HashSet<>();
            }
            HashSet<Integer> hasAdd = new HashSet<>();

            while (j < n) {
                int temp = (int) Math.floor(Math.random() * links.size());
                allSet[j].add(temp);
                hasAdd.add(temp);
                j++;
            }
            for (int i = 0; i < links.size(); i++) {
                // if (hasAdd.contains(i)) continue;
                Spfa spfa = new Spfa();
                int[] dis = spfa.getDis(i, links);
                double[] disToGroup = new double[n];
                double min = Double.MAX_VALUE;
                int minIndex = -1;
                for (int u = 0; u < n; u++) {
                    HashSet<Integer> temp = allSet[u];
                    for (int t : temp) {
                        disToGroup[u] += dis[t];
                    }
                    disToGroup[u] = disToGroup[u] / temp.size();
                    if (disToGroup[u] < min) {
                        min = disToGroup[u];
                        minIndex = u;
                    }
                }
                if (consumerId.contains(i)) {
                    consumerSet[minIndex].add(i);
                }
                allSet[minIndex].add(i);
            }
            int put = 0;
            int sum = 0;
            for (int k = 0; k < n; k++) {
                sum += Math.abs(consumerSet[k].size() - avg);
                result.put(put, allSet[k]);
                result.put(put + 1, consumerSet[k]);
                put += 2;
            }
            if (sum / n < consumerId.size() * 0.1) {
                cut = true;
            }
        }

        return result;
    }

    public HashMap<Integer, ServerInfo> getAllServerInfoByPathAndLink(LinkedList<LinkedList<Integer>> allPath, HashMap<Integer, HashMap<Integer, Link>> links,int serverCost) {
        HashMap<Integer, ServerInfo> result = new HashMap<>();
        for (LinkedList<Integer> temp : allPath) {
            int serverId = temp.getFirst();
            int band = temp.getLast();
            int consumerId = temp.get(temp.size() - 2);
            int cost = 0;
            for (int i = 0; i < temp.size() - 2; i++) {
                int from = temp.get(i);
                int to = temp.get(i + 1);
                cost += band * links.get(from).get(to).cost();
            }
            if (result.get(serverId) == null) {
                HashSet<Integer> consumerSet = new HashSet<>();
                consumerSet.add(consumerId);

                ServerInfo serverInfo = new ServerInfo(serverId,band, consumerSet, cost,serverCost);
                result.put(serverId, serverInfo);
            } else {
                ServerInfo serverInfo = result.get(serverId);
                int tempBand = serverInfo.getOutPutBand();
                int tempCost = serverInfo.getAllCost();
                HashSet<Integer> tempConset = serverInfo.getConsumerSet();
                tempConset.add(consumerId);
                serverInfo.setAllCost(tempCost+cost);
                serverInfo.setOutPutBand(tempBand+band);
            }
        }
        return result;
    }
}
