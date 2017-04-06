package com.cacheserverdeploy.deploy;

import java.util.*;

/**
 * Created by magichuang on 17-3-31.
 */
public class MCMFOPT {
    HashMap<Integer, HashMap<Integer, Link>> links;
    int nodeNum;
    int linkNum;
    int consumNum;
    int serverCost;
    private LinkedList<LinkedList<Integer>> allPath;
    private int allCost;
    private int allBand;
    private ConsumerNode[] consumerNodes;
    HashSet<Integer> serverSet;
    HashSet<Integer> used;
    private int requireBand;


    public MCMFOPT(int[] server, HashMap<Integer, HashMap<Integer, Link>> links, ConsumerNode[] consumerNodes, int serverCost) {
        serverSet = new HashSet<>();
        requireBand = 0;
        this.links = new HashMap<>();
        for (int i = 0; i < server.length; i++) {
            serverSet.add(server[i]);
        }
        for (int i = 0; i < links.size(); i++) {
            HashMap<Integer, Link> temp = links.get(i);
            HashMap<Integer, Link> copy = new HashMap<>();
            Iterator<Map.Entry<Integer, Link>> iterator = temp.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Link> entry = iterator.next();
                Link tempL = entry.getValue();
                Link copyLink = new Link(tempL);
                copy.put(entry.getKey(), copyLink);
            }
            this.links.put(i, copy);
        }
        this.used = new HashSet<>();
        this.nodeNum = links.size();
        this.consumerNodes = consumerNodes;
        this.serverCost = serverCost;
        allCost = 0;
        allBand = 0;
        allPath = new LinkedList<>();
        for (int i = 0; i < server.length; i++) {
            this.links.get(server[i]).put(nodeNum + 1, new Link(server[i], nodeNum + 1, Integer.MAX_VALUE, 0));
        }
        HashMap<Integer, Link> superStart = new HashMap<>();
        for (int i = 0; i < consumerNodes.length; i++) {
            superStart.put(consumerNodes[i].netNodeID, new Link(nodeNum, consumerNodes[i].netNodeID, consumerNodes[i].getNeed(), 0));
            this.links.get(consumerNodes[i].netNodeID).put(nodeNum, new Link(consumerNodes[i].netNodeID, nodeNum, consumerNodes[i].getNeed(), 0));
            requireBand += consumerNodes[i].getNeed();
        }
        this.links.put(nodeNum, superStart);
    }

    public void start() {
        int[] pre = new int[nodeNum + 2];
        Arrays.fill(pre, -1);
        int[] dis = getPath(nodeNum, links, pre);
        while (dis[nodeNum + 1] != Integer.MAX_VALUE) {
            int minBand = Integer.MAX_VALUE;
            LinkedList<Integer> path = new LinkedList<>();
            int cur = nodeNum + 1;
            while (cur != nodeNum) {
                cur = pre[cur];
                path.addLast(cur);
            }
            for (int i = 0; i < path.size() - 1; i++) {
                minBand = Math.min(links.get(path.get(i)).get(path.get(i + 1)).capacity(), minBand);
            }

            for (int i = 0; i < path.size() - 1; i++) {
                int curBand = links.get(path.get(i)).get(path.get(i + 1)).capacity();
                curBand -= minBand;
                links.get(path.get(i)).get(path.get(i + 1)).setCapacity(curBand);
                links.get(path.get(i + 1)).get(path.get(i)).setCapacity(curBand);
                if (curBand <= 0) {
                    links.get(path.get(i)).remove(path.get(i + 1));
                    links.get(path.get(i + 1)).remove(path.get(i));
                }
            }
            path.removeLast();
            int serverId = path.getFirst();
            used.add(serverId);
            path.addLast(minBand);
            allPath.add(path);
            allCost += (dis[nodeNum + 1] * minBand);
            allBand += minBand;
            dis = getPath(nodeNum, links, pre);
        }
    }

    public int[] getPath(int start, HashMap<Integer, HashMap<Integer, Link>> links, int[] pre) {
        LinkedList<Integer> linkedList = new LinkedList<>();
        int[] dis = new int[nodeNum+2];
        for (int i = 0; i < dis.length; i++) dis[i] = Integer.MAX_VALUE;
        linkedList.add(start);
        dis[start] = 0;
        HashSet<Integer> visit = new HashSet<>();
        while (!linkedList.isEmpty()) {
            int temp = linkedList.get(0);
            linkedList.remove(0);
            visit.remove(temp);
            if (temp == nodeNum + 1) continue;
            HashMap<Integer, Link> around = links.get(temp);
            Iterator<Map.Entry<Integer, Link>> iterator = around.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Link> entry = iterator.next();
                Link cur = entry.getValue();
                if (dis[temp] + cur.cost() < dis[cur.to]) {
                    dis[cur.to] = dis[temp] + cur.cost();
                    pre[cur.to] = temp;
                    if (visit.add(cur.to)) linkedList.addLast(cur.to);
                }
            }
        }
        return dis;

    }

    public LinkedList<LinkedList<Integer>> getAllPath() {
        return allPath;
    }

    public int getAllCost() {
        return allCost + serverSet.size() * serverCost;
    }

    public int getAllBand() {
        return allBand;
    }

    public boolean isSolve() {
        return allBand == requireBand;
    }

    public HashSet<Integer> getServerSet() {
        return serverSet;
    }

    public HashSet<Integer> getUsed() {
        return used;
    }

    public int getRealCost() {
        return allCost + used.size() * serverCost;
    }
}
