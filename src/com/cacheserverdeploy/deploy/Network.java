package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by yang on 2017/3/28.
 */
public class Network {
    public int netNodeNum;
    public int linkNum;
    public int consumerNodeNum;
    public int serverCost;
    public ConsumerNode[] consumerNodes;
    public HashMap<Integer, HashMap<Integer, Link>> links;

    public Network(String[] graphContent) {
        // 第一部分（一行）是“网络节点数量 网络链路数量 消费节点数量”
        int lineCount = 0;
        String s = graphContent[lineCount++];
        String[] nums = s.split("\\s+");
        netNodeNum = Integer.parseInt(nums[0]);
        linkNum = Integer.parseInt(nums[1]);
        consumerNodeNum = Integer.parseInt(nums[2]);
        s = graphContent[lineCount++];
        while (s.trim().length() == 0) { //跳过空行
            s = graphContent[lineCount++];
        }
        // 空行后的第二部分是视频内容服务器部署成本
        serverCost = Integer.parseInt(s);

        s = graphContent[lineCount++];
        while (s.trim().length() == 0) { //跳过空行
            s = graphContent[lineCount++];
        }
        // 第三部分是链路信息
        // 链路起始节点ID 链路终止节点ID 总带宽大小 单位网络租用费
        links = new HashMap<>();
        for (int i = 0; i < netNodeNum; i++) {
            links.put(i, new HashMap<Integer, Link>());
        }
        int startNodeID, endNodeID, bandwidth, cost;
        Link link;
        for (int i = 0; i < linkNum; i++) {
            String[] linkInfo = s.trim().split("\\s+");
            startNodeID = Integer.parseInt(linkInfo[0]);
            endNodeID = Integer.parseInt(linkInfo[1]);
            bandwidth = Integer.parseInt(linkInfo[2]);
            cost = Integer.parseInt(linkInfo[3]);
            link = new Link(startNodeID, endNodeID, bandwidth, cost);
            links.get(startNodeID).put(endNodeID, link);
            link = new Link(endNodeID, startNodeID, bandwidth, cost);
            links.get(endNodeID).put(startNodeID, link);
            s = graphContent[lineCount++];
        }

        s = graphContent[lineCount++];
        while (s.trim().length() == 0) { //跳过空行
            s = graphContent[lineCount++];
        }
        lineCount--;
        // 第四部分是终端用户信息
        // 消费节点ID 相连网络节点ID 视频带宽消耗需求
        consumerNodes = new ConsumerNode[consumerNodeNum];
        int consumerNodeID, netNodeID, need;
        for (int i = 0; i < consumerNodeNum; i++) {
            s = graphContent[lineCount++];
            String[] terminalInfo = s.trim().split("\\s+");
            consumerNodeID = Integer.parseInt(terminalInfo[0]);
            netNodeID = Integer.parseInt(terminalInfo[1]);
            need = Integer.parseInt(terminalInfo[2]);
            consumerNodes[consumerNodeID] = new ConsumerNode(netNodeID, need);

        }
    }

//    public Network(Network network) {
//        this.netNodeNum = network.netNodeNum;
//        this.linkNum = network.linkNum;
//        this.consumerNodeNum = network.consumerNodeNum;
//        this.serverCost = network.serverCost;
//        this.consumerNodes = new ConsumerNode[consumerNodeNum];
//        for (int i = 0; i < consumerNodeNum; i++) {
//            consumerNodes[i] = new ConsumerNode(network.consumerNodes[i]);
//        }
//        this.links = new ArrayList<>();
//        for (int i = 0; i < netNodeNum; i++) {
//            links.add(new HashMap<>());
//        }
//    }

//    public Network copy() {
//        return new Network(this);
//    }

    public int getNetNodeNum() {
        return netNodeNum;
    }

    public int getLinkNum() {
        return linkNum;
    }

    public int getConsumerNodeNum() {
        return consumerNodeNum;
    }

    public int getServerCost() {
        return serverCost;
    }

    public ConsumerNode[] getConsumerNodes() {
        return consumerNodes;
    }

    public HashMap<Integer, HashMap<Integer, Link>> getLinks() {
        return links;
    }
}
