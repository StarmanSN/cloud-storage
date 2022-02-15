package com.geekbrains.cloud.jan.model;

import lombok.Data;

import java.util.Objects;

public class A {
    private final int a;
    private final String s;
    private A parent;

    public A(int a, String s) {
        this.a = a;
        this.s = s;
    }

    public int getA() {
        return a;
    }

    public String getS() {
        return s;
    }

    public A getParent() {
        return parent;
    }

    public void setParent(A parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        System.out.println("=");
        if (this == o) return true;
        if (!(o instanceof A)) return false;
        A a1 = (A) o;
        return getA() == a1.getA() && Objects.equals(getS(), a1.getS()) && Objects.equals(getParent(), a1.getParent());
    }

    @Override
    public int hashCode() {
        System.out.println("h");
        return Objects.hash(getA(), getS(), getParent());
    }

    @Override
    public String toString() {
        System.out.println("*");
        return "A{" +
                "a=" + a +
                ", s='" + s + '\'' +
                ", parent=" + parent +
                '}';
    }
}
