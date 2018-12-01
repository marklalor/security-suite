package edu.cwru.eecs444.fall2018.implementations;

import edu.cwru.eecs444.fall2018.Utilities;

import java.math.BigInteger;
import java.util.Random;

import static edu.cwru.eecs444.fall2018.Utilities.bytesToHex;

public class Rsa {
    private static final int DEFAULT_KEY_SIZE = 2048;
    private static final int DEFAULT_PUBLIC_EXP = 65537;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger ZERO = BigInteger.ZERO;

    /**
     * This method generates a new KeyPair object
     * @return This returns a KeyPair object used for RSA
     */
    private static KeyPair generateKeyPair(final int keySize) {
        while(true) {
            final BigInteger p = getRandomPrime(keySize / 2);
            final BigInteger e = BigInteger.valueOf(DEFAULT_PUBLIC_EXP);
            final BigInteger q = getRandomPrime(keySize / 2);
            final BigInteger lambda = lcm(p.subtract(ONE), q.subtract(ONE));

            if(validKeyPair(keySize, lambda, p, e, q)) {
                return new KeyPair(p.multiply(q), e, e.modInverse(lambda));
            }
        }
    }

    private static BigInteger getRandomPrime(final int bitLength) {
        return BigInteger.probablePrime(bitLength, new Random());
    }

    private static boolean validKeyPair(final int keySize,
                                        final BigInteger lambda,
                                        final BigInteger p,
                                        final BigInteger e,
                                        final BigInteger q) {

        return e.gcd(lambda).equals(ONE) &&
                p.subtract(q).abs().subtract(ONE.shiftLeft(keySize / 2 - 100)).signum() == 1;
    }

    private static BigInteger lcm(BigInteger a, BigInteger b) {
        return a.multiply(b.divide(a.gcd(b)));
    }

    /**
     * This method encrypts a message with a public key and its exponent
     * @param m This is the plain message as a byte array
     * @param n This is the public key
     * @param e This is the public key exponent
     * @return byte[] This returns the encrypted message
     */
    private static byte[] encrypt(final byte[] m, final byte[] n, final byte[] e) {
        byte[][] chunked = chunk(m, DEFAULT_KEY_SIZE);
        byte[] encryptedChunk;
        byte[] output = new byte[chunked.length * DEFAULT_KEY_SIZE];
        int j = 0;

        for(int i = 0; i < chunked.length; i++){
        	encryptedChunk = encrypt(new BigInteger(chunked[i]), new BigInteger(n), new BigInteger(e)).toByteArray();
        	System.arraycopy(encryptedChunk, 0, output, j, encryptedChunk.length);
        	j = j + encryptedChunk.length;
        }

        return output;
    }

    public static BigInteger encrypt(final BigInteger m, final BigInteger n, final BigInteger e) {
        return m.modPow(e, n);
    }

    /**
     * This method decrypts a message with the private key and public key
     * @param c This is the encrypted message as a byte array
     * @param d This is the private key
     * @param n This is the public key
     * @return byte[] This returns the decrypted message
     */
    private static byte[] decrypt(final byte[] c, final byte[] d, final byte[] n) {
        byte[][] chunked = chunk(c, DEFAULT_KEY_SIZE);
        byte[] decryptedChunk;
        byte[] output = new byte[chunked.length * DEFAULT_KEY_SIZE];
        int j = 0;

        for(int i = 0; i < chunked.length; i++){
        	decryptedChunk = decrypt(new BigInteger(chunked[i]), new BigInteger(d), new BigInteger(n)).toByteArray();
        	System.arraycopy(decryptedChunk, 0, output, j, decryptedChunk.length);
        	j = j + decryptedChunk.length;
        }

        return output;
    }

    private static BigInteger decrypt(final BigInteger c, final BigInteger d, final BigInteger n) {
        return c.modPow(d, n);
    }

    private static class KeyPair {
        private final byte[] publicKey, publicKeyExp, privateKey;

        KeyPair(final BigInteger publicKey,
                final BigInteger publicKeyExp,
                final BigInteger privateKey) {
            this.publicKey = publicKey.toByteArray();
            this.publicKeyExp = publicKeyExp.toByteArray();
            this.privateKey = privateKey.toByteArray();
        }

        public byte[] getPublicKey() {
            return publicKey;
        }

        public byte[] getPublicKeyExp() {
            return publicKeyExp;
        }

        public byte[] getPrivateKey() {
            return privateKey;
        }

        @Override
        public String toString() {
            return String.format("Public: %s, Exp: %s, Private: %s", new BigInteger(publicKey).toString(), new BigInteger(publicKeyExp).toString(), new BigInteger(privateKey).toString());
        }
    }

    private static byte[][] chunk(final byte[] input, int chunkLength) {
        byte[][] chunkedInput = new byte[(input.length + 1) / chunkLength][chunkLength];

        int i =0;
        while (i < chunkedInput.length) {
            chunkedInput[i] = new byte[chunkLength];
            System.arraycopy(input, chunkLength*i, chunkedInput[i], 0, Math.min(chunkLength, input.length-chunkLength*i));
            i++;
        }

        return chunkedInput;
    }


    public static void main(String[] args) {
        final KeyPair keyPair = generateKeyPair(DEFAULT_KEY_SIZE);
        System.out.println(keyPair);
        final byte[] publicKey = keyPair.getPublicKey();
        final byte[] publicKeyExp = keyPair.getPublicKeyExp();
        final byte[] privateKey = keyPair.getPrivateKey();

        final String message = "Hello world";
        final byte[] messageBytes = message.getBytes();
        System.out.printf("Original bytes: %s\n", bytesToHex(messageBytes));
        final byte[] encryptedBytes = encrypt(messageBytes, publicKey, publicKeyExp);
        System.out.printf("Encrypted bytes: %s\n", bytesToHex(encryptedBytes));
        final byte[] decryptedBytes = decrypt(encryptedBytes, privateKey, publicKey);
        System.out.printf("Decrypted bytes: %s\n", bytesToHex(decryptedBytes));
    }
}