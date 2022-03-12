package com.example.diffie_hellman_key_exchange.model;

public class DiffieHellmanRepo {
    public static DiffieHellmanRepo instance;
    private DiffieHellman diffieHellman;


    public static DiffieHellmanRepo getInstance(int group)
    {
        if (instance == null) {
            instance = new DiffieHellmanRepo(group);
        }
        return instance;
    }

    public DiffieHellman getDiffieHellman() {
        return diffieHellman;
    }

    public DiffieHellmanRepo(int group)
    {
        this.diffieHellman = new DiffieHellman(group);
    }
}
