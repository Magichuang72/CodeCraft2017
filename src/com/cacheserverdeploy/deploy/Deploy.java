package com.cacheserverdeploy.deploy;

import java.util.*;

public class Deploy {
    private static final long RUN_TIME = 87000;
    private static long TIME_OUT = 86000;
    private static final double E = 2.718281828;
    private static HashMap<Integer, Integer> payId;

    /**
     * 你需要完成的入口
     * <功能详细描述>
     *
     * @param graphContent 用例信息文件
     * @return [参数说明] 输出结果信息
     * @see [类、类#方法、类#成员]
     */
    public static String[] deployServer(String[] graphContent) {
        //----------------读取数据开始--------------------------
        Helper helper = new Helper();
        Network network = new Network(graphContent);
        payId = new HashMap<>();
        //按照不同规模设定分治策略的运行时间
        long smallTime = 15000;//中级
        if (network.netNodeNum > 500) {
            smallTime = 28000;//高级
        } else if (network.netNodeNum < 200) {
            smallTime = 3000;//初级
        }
        ConsumerNode[] pay = network.getConsumerNodes();
        LinkedList<ConsumerNode> consumerList = new LinkedList<>();
        HashMap<Integer, HashMap<Integer, Link>> links = network.getLinks();
        HashSet<Integer> ConsumerId = new HashSet<>();
        HashSet<Integer> allId = new HashSet<>();
        for (int i = 0; i < links.size(); i++) {
            allId.add(i);
        }

        for (int i = 0; i < pay.length; i++) {
            ConsumerId.add(pay[i].netNodeID);
            consumerList.add(pay[i]);
            payId.put(pay[i].netNodeID, i);
        }
        HashSet<Integer> setServer = new HashSet<>();
        Util.sort2(consumerList, new Comparator<ConsumerNode>() {
            @Override
            public int compare(ConsumerNode o1, ConsumerNode o2) {
                return o2.getNeed() - o1.getNeed();
            }
        });
        for (int i = 0; i < consumerList.size() * 0.2; i++) {
            int id = consumerList.get(i).netNodeID;
            int count = 0;
            HashMap<Integer, Link> map = links.get(id);
            for (Map.Entry<Integer, Link> t : map.entrySet()) {
                int to = t.getKey();
                if (ConsumerId.contains(to)) {
                    count++;
                }
            }
            if (count < 10)
                setServer.add(id);
        }

        int serveCost = network.serverCost;
        int size = network.getNetNodeNum();
        //-----预处理------
        // HashMap<Integer, HashSet<Integer>> splitResult = helper.spiltGraph2(links, network.getConsumerNodes(),ConsumerId);
        int splitNum = 3;
        long timeStart = System.currentTimeMillis();
        HashMap<Integer, HashSet<Integer>> splitResult = helper.spiltGraphN(links, ConsumerId, splitNum);
        long spiltTime = System.currentTimeMillis() - timeStart;
        TIME_OUT -= spiltTime;
        HashMap<Integer, HashMap<Integer, Link>>[] partLinks = new HashMap[splitNum];
        HashSet<Integer>[] partId = new HashSet[splitNum];
        HashSet<Integer>[] consumerId = new HashSet[splitNum];
        ArrayList<ConsumerNode[]> partPay = new ArrayList<>();
        int splitIndex = 0;
        for (int h = 0; h < splitNum; h++) {
            partId[h] = splitResult.get(splitIndex);
            consumerId[h] = splitResult.get(splitIndex + 1);
            partLinks[h] = helper.getPartLinksById(links, splitResult.get(splitIndex));
            partPay.add(helper.getPartConsumerById(pay, splitResult.get(splitIndex + 1)));
            splitIndex += 2;
        }
        System.out.println(splitResult.size() / 2 + "fenge");


        //------参数设置-----


        GA[] allGA = new GA[splitNum];

        for (int i = 0; i < splitNum; i++) {
            allGA[i] = new GA(5, 0.01, 0.8, partLinks[i], partPay.get(i), consumerId[i], serveCost, size, smallTime * consumerId[i].size() / ConsumerId.size(), new int[]{}, setServer);
            allGA[i].start();
        }
        int[] next = new int[size];
        for (int i = 0; i < size; i++) {
            for (GA ga : allGA) next[i] += ga.getMinParticle()[i];

        }
        GA totalGa = new GA(10, 0.01, 0.6, links, pay, ConsumerId, serveCost, size, TIME_OUT - smallTime, next, setServer);
        totalGa.start();
        //-----------------------GA-----------------------
        LinkedList<LinkedList<Integer>> minPath = totalGa.getMinPath();
        int minCost = 0;
        int ds = 0;
//        for (LinkedList<Integer> temp : minPath) {
//            if (temp.size() == 2) ds++;
//        }
//        System.out.println("ds" + ds);
        minCost = totalGa.getMinCost();
        System.out.println(minCost);
        String[] resultOut = new String[minPath.size() + 2];
        resultOut[0] = String.valueOf(minPath.size());
        resultOut[1] = "";
        for (int i = 0; i < minPath.size(); i++) {
            LinkedList<Integer> path = minPath.get(i);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < path.size() - 1; j++) {
                sb.append(path.get(j) + " ");
            }
            int payI = path.get(path.size() - 2);
            int payID = payId.get(payI);
            sb.append(payID + " ");
            int bandwidth = path.get(path.size() - 1);
            sb.append(bandwidth);
            resultOut[i + 2] = sb.toString();
        }
        return resultOut;

    }
}







