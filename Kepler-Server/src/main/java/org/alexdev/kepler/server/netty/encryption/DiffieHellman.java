package org.alexdev.kepler.server.netty.encryption;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;
import java.security.SecureRandom;

public class DiffieHellman {
    private static final int BITLENGTH = 64;

    private BigInteger clientP;
    private BigInteger clientG;
    private BigInteger publicKey;
    private BigInteger privateKey;
    private BigInteger sharedKey;

    public DiffieHellman() {
        this.privateKey = generatePrivateKey();

        this.clientG = new BigInteger("23786635532332886537261431906453031264918297");
        this.clientP = new BigInteger("632158881801130885249042417232212770524741295422564233061391190031954228421232913648184592218883487397503624904102572293826728806813079");

        this.publicKey = this.computePublicKey(this.privateKey);
    }

    private static BigInteger generatePrivateKey() {
        SecureRandom random = new SecureRandom();
        BigInteger privateKey;
        do {
            privateKey = new BigInteger(BITLENGTH, random);
        } while (privateKey.compareTo(BigInteger.ZERO) == 0);
        return privateKey;
    }

    private BigInteger computePublicKey(BigInteger privateKey) {
        return this.clientG.modPow(privateKey, this.clientP);
    }

    private BigInteger computeSharedSecret(BigInteger privateKey, BigInteger publicKey) {
        return publicKey.modPow(privateKey, this.clientP);
    }

    public void generateSharedKey(String publicServerKey) {
        this.sharedKey = computeSharedSecret(this.privateKey, new BigInteger(publicServerKey));
    }

    public static String generateRandomNumString(int len) {
        StringBuilder result = new StringBuilder();

        char[] numbers = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };

        for (int i = 0; i < len; i++) {
            result.append(numbers[ThreadLocalRandom.current().nextInt(numbers.length)]);
        }
        return result.toString();
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public BigInteger getSharedKey() {
        return sharedKey;
    }
}
