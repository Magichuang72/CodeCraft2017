package com.cacheserverdeploy.deploy;

import java.util.*;

/**
 * Created by yang on 2017/4/3.
 */
public class Util {
//    public static boolean checkPath(Network network, String[] paths) {
//        int directlyConnectedServer = 0;
//        int totalCost = 0;
//        HashSet<Integer> server = new HashSet<>();
//        int lineCount = 0;
//        String s = paths[lineCount++];
//        if(Debug.ENABLE) {
//            System.out.println(s);
//        }
//        while(s.trim().length() == 0) { //跳过空行
//            s = paths[lineCount++];
//        }
//        int pathNum = Integer.parseInt(s);
//        s = paths[lineCount++];
//        while(s.trim().length() == 0) { //跳过空行
//            s = paths[lineCount++];
//        }
//        lineCount--;
//        for(int i = 0; i < pathNum; i++) {
//            s = paths[lineCount++];
//            String[] ids = s.split("\\s+");
//            int startNetNodeID = Integer.parseInt(ids[0]);
//            server.add(startNetNodeID);
//            int usedBandwidth = Integer.parseInt(ids[ids.length-1]);
//            int consumerNodeID = Integer.parseInt(ids[ids.length-2]);
//            if(ids.length == 3) {
//                directlyConnectedServer++;
//                System.out.println("消费节点" + consumerNodeID + "直连了服务器");
//            }
//            for(int j = 1; j < ids.length-2; j++) {
//                int endNetNodeID = Integer.parseInt(ids[j]);
//                int linkBandwidth = network.linkBandwidths.get(startNetNodeID).get(endNetNodeID);
//                if(usedBandwidth > linkBandwidth) {
//                    System.out.println("带宽超限"+lineCount);
//                    return false;
//                }
//                totalCost += usedBandwidth * network.linkUnitCosts.get(startNetNodeID).get(endNetNodeID);
//                network.linkBandwidths.get(startNetNodeID).put(endNetNodeID,linkBandwidth-usedBandwidth);
//                startNetNodeID = endNetNodeID;
//            }
//            System.out.print("消费节点" + consumerNodeID + "原需求"+network.consumerNodes.get(consumerNodeID).getNeed());
//            network.consumerNodes.get(consumerNodeID).subtractNeed(usedBandwidth);
//            System.out.println(",现需求"+network.consumerNodes.get(consumerNodeID).getNeed());
//        }
//        for(int i = 0; i < network.consumerNodeNum; i++) {
//            if(network.consumerNodes.get(i).getNeed() > 0) {
//                System.out.println("消费节点"+i+"需求未满足");
//                return false;
//            }
//        }
//        System.out.println("直连服务器数量：" + directlyConnectedServer);
//        System.out.println("总花费：" + (server.size()*network.serverCost + totalCost));
//        return true;
//    }

    public static void sort1(LinkedList<Particle> head, Comparator<Particle> c) {
        Object[] a = head.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIterator<Particle> i = head.listIterator();
        for (Object e : a) {
            i.next();
            i.set((Particle) e);
        }
    }
    public static void sort2(LinkedList<ConsumerNode> head, Comparator<ConsumerNode> c) {
        Object[] a = head.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIterator<ConsumerNode> i = head.listIterator();
        for (Object e : a) {
            i.next();
            i.set((ConsumerNode) e);
        }
    }
//
//    /**
//     * SPFA算法
//     * @param sourceNodeID 起始节点
//     * @param linkUnitCosts 边的权重
//     * @return 距离
//     */
//    public static HashMap<Integer,Integer> SPFA(int sourceNodeID,
//                               HashMap<Integer, HashMap<Integer, Integer>> linkUnitCosts,
//                               HashMap<Integer, NetNode> netNodes) {
//        LinkedList<Integer> linkedList = new LinkedList<>();
//        int nodeNum = linkUnitCosts.size();
//        int[] dis = new int[nodeNum];
//        for (int i = 0; i < dis.length; i++) dis[i] = Integer.MAX_VALUE;
//        linkedList.add(start);
//        dis[start] = 0;
//        HashSet<Integer> visit = new HashSet<>();
//        while (!linkedList.isEmpty()) {
//            int temp = linkedList.get(0);
//            linkedList.remove(0);
//            visit.remove(temp);
//            if (temp == nodeNum + 1) continue;
//            HashMap<Integer, Integer> around = linkUnitCosts.get(temp);
//            Iterator<Map.Entry<Integer, Integer>> iterator = around.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<Integer, Integer> entry = iterator.next();
//                int NeedTimesUnitCost = entry.getValue()*
//                        (netNodes.get(temp).attachedConsumerNode.getNeed()+
//                                netNodes.get(entry.getKey()).attachedConsumerNode.getNeed());
//                if (dis[temp] + NeedTimesUnitCost < dis[entry.getKey()]) {
//                    dis[entry.getKey()] = dis[temp] + NeedTimesUnitCost;
//                    if (visit.add(entry.getKey())) linkedList.addLast(entry.getKey());
//                }
//            }
//        }
//        return dis;
//    }
}
