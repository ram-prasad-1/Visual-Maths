package com.visualfiber.apps.visualmaths.utils;

public class mth {


    // LCM
    public int lcm(int a, int b) {

        return ((a / gcd(a, b)) * b);
    }


    // GCD
    public int gcd(int a, int b) {
        if (a == 0 || b == 0) return a + b; // base case
        return gcd(b, a % b);
    }


}
