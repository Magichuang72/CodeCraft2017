package com.cacheserverdeploy.deploy;

import java.util.HashSet;

/**
 * Created by magichuang on 17-4-5.
 */
public class ServerInfo {
    private int outPutBand;
    private HashSet<Integer> consumerSet;
    private int allCost;
    private int serverId;
    private int serverCost;

    public ServerInfo(int serverId, int outPutBand, HashSet<Integer> consumerSet, int allCost, int serverCost) {
        this.outPutBand = outPutBand;
        this.consumerSet = consumerSet;
        this.allCost = allCost;
        this.serverId = serverId;
        this.serverCost = serverCost;
    }

    public int fitness() {
        return (serverCost / consumerSet.size() + allCost) / outPutBand;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getOutPutBand() {
        return outPutBand;
    }

    public void setOutPutBand(int outPutBand) {
        this.outPutBand = outPutBand;
    }

    public HashSet<Integer> getConsumerSet() {
        return consumerSet;
    }

    public void setConsumerSet(HashSet<Integer> consumerSet) {
        this.consumerSet = consumerSet;
    }

    public int getAllCost() {
        return allCost;
    }

    public void setAllCost(int allCost) {
        this.allCost = allCost;
    }
}
