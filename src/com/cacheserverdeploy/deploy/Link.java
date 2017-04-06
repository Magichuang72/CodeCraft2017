package com.cacheserverdeploy.deploy;

/**
 * Created by yang on 2017/3/28.
 */

public class Link{

    public final int from;
    public final int to;
    private final int cost;
    private int capacity; // 也许要修改边，不要声明为final

    /**
     * Initializes an edge between vertices {@code from} and {@code to} of
     * the given {@code capacity, @code cost}.
     *
     * @param  from 链路的起点
     * @param  to 链路的终点
     * @param  capacity 链路的带宽上限
     * @param  cost 单位带宽的租用成本
     */
    public Link(int from, int to, int capacity, int cost) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.cost = cost;
    }
	
	public Link(Link link) {
		this.from = link.from;
		this.to = link.to;
		this.capacity = link.capacity();
		this.cost = link.cost();
	}

    /**
     * Returns the cost of this link.
     *
     * @return the cost of this link
     */
    public int cost() {
        return cost;
    }

    /**
     * Returns the capacity of this link.
     *
     * @return the capacity of this link
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Set the capacity of this link.
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }



    /**
     * Returns a string representation of this edge.
     *
     * @return a string representation of this edge
     */


    /**
     * Unit tests the {@code Edge} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Link e = new Link(12, 34, 12, 1);
        System.out.println(e);
    }
}
