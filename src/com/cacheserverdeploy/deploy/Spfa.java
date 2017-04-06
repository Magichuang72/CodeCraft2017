package com.cacheserverdeploy.deploy;

import com.cacheserverdeploy.deploy.Link;

import java.util.*;

/**
 * Created by magichuang on 17-4-2.
 */
public class Spfa {
    public int[] getDis(int start, HashMap<Integer, HashMap<Integer, Link>> links) {
        LinkedList<Integer> linkedList = new LinkedList<>();
        int nodeNum = links.size();
        int[] dis = new int[nodeNum];
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
                    if (visit.add(cur.to)) linkedList.addLast(cur.to);

                }
            }
        }
        return dis;
    }
}
