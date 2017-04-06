package com.cacheserverdeploy.deploy;

/**
 * Created by yang on 2017/3/28.
 */
public class ConsumerNode {
    public final int netNodeID;
    int need;

    public ConsumerNode(int netNodeID, int need) {
        this.netNodeID = netNodeID;
        this.need = need;
    }
	
	public ConsumerNode(ConsumerNode consumerNode) {
		this.netNodeID = consumerNode.netNodeID;
		this.need = consumerNode.getNeed();
	}
	
	public int getNeed() {
		return need;
	}

    public void setNeed(int need) {
        this.need = need;
    }

    public String toString() {
        return String.format("ConsumerNode connected to netNodeID %d, with need: %d.",
                netNodeID, need);
    }
}
