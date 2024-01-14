package com.mise.postcenter.service.impl;

public class Keyword implements Comparable<Keyword> {
    private double tfidfvalue;
    private String name;

    public double getTfidfvalue() {
        return tfidfvalue;
    }

    public void setTfidfvalue(double tfidfvalue) {
        this.tfidfvalue = tfidfvalue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Keyword(String name, double tfidfvalue) {
        this.name = name;
        // tfidf值只保留3位小数
        this.tfidfvalue = (double) Math.round(tfidfvalue * 10000) / 10000;
    }

    @Override
    public int compareTo(Keyword o) {
        if (this.tfidfvalue > o.tfidfvalue) {
            return -1;
        } else if (this.tfidfvalue == o.tfidfvalue) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        long temp;
        temp = Double.doubleToLongBits(tfidfvalue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Keyword other = (Keyword) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
//		if (Double.doubleToLongBits(tfidfvalue) != Double.doubleToLongBits(other.tfidfvalue))
//			return false;
        return true;
    }


}
