package edu.cwru.eecs444.fall2018.implementations;

import edu.cwru.eecs444.fall2018.Utilities;

import java.math.BigInteger;
import java.util.Random;

import static edu.cwru.eecs444.fall2018.Utilities.bytesToHex;

public class Rsa {
    public static final int DEFAULT_KEY_SIZE = 2048;
    private static final int DEFAULT_PUBLIC_EXP = 65537;
    private static final int MAX_MESSAGE_LENGTH = DEFAULT_KEY_SIZE / 8;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger ZERO = BigInteger.ZERO;

    /**
     * This method generates a new KeyPair object
     * @return This returns a KeyPair object used for RSA
     */
    public static KeyPair generateKeyPair(final int keySize) {
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
     * @param message This is the plain message as a byte array
     * @param publicKey This is the public key
     * @param publicKeyExponent This is the public key exponent
     * @return byte[] This returns the encrypted message
     */
    public static byte[] encrypt(final byte[] message, final byte[] publicKey, final byte[] publicKeyExponent) {
        byte[][] chunked = chunk(message, MAX_MESSAGE_LENGTH);
        byte[] encryptedChunk;
        int j = 0;
        byte[] output = new byte[MAX_MESSAGE_LENGTH * chunked.length];

        for(int i = 0; i < chunked.length; i++){
            encryptedChunk = encrypt(new BigInteger(chunked[i]), new BigInteger(publicKey), new BigInteger(publicKeyExponent)).toByteArray();
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
     * @param message This is the encrypted message as a byte array
     * @param privateKey This is the private key
     * @param publicKey This is the public key
     * @return byte[] This returns the decrypted message
     */
    public static byte[] decrypt(final byte[] message, final byte[] privateKey, final byte[] publicKey) {
        byte[][] chunked = chunk(message, MAX_MESSAGE_LENGTH);
        byte[] decryptedChunk;
        int j = 0;
        byte[] output = new byte[MAX_MESSAGE_LENGTH * chunked.length];

        for(int i = 0; i < chunked.length; i++){
            decryptedChunk = decrypt(new BigInteger(chunked[i]), new BigInteger(privateKey), new BigInteger(publicKey)).toByteArray();
            System.arraycopy(decryptedChunk, 0, output, j, decryptedChunk.length);
            j = j + decryptedChunk.length;
        }
        return output;

        //return decrypt(new BigInteger(c), new BigInteger(d), new BigInteger(n)).toByteArray();
    }

    private static BigInteger decrypt(final BigInteger c, final BigInteger d, final BigInteger n) {
        return c.modPow(d, n);
    }

    public static class KeyPair {
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
        byte[][] chunkedInput = new byte[((input.length - 1) / chunkLength) + 1][];

        int i =0;
        while (i < chunkedInput.length - 1) {
            chunkedInput[i] = new byte[chunkLength];
            System.arraycopy(input, chunkLength*i, chunkedInput[i], 0, chunkLength);
            i++;
        }

        chunkedInput[i] = new byte[input.length-chunkLength*i];
        System.arraycopy(input, chunkLength*i, chunkedInput[i], 0, input.length-chunkLength*i);
        return chunkedInput;
    }


    public static void main(String[] args) {
        final KeyPair keyPair = generateKeyPair(DEFAULT_KEY_SIZE);
        System.out.println(keyPair);
        final byte[] publicKey = keyPair.getPublicKey();
        final byte[] publicKeyExp = keyPair.getPublicKeyExp();
        final byte[] privateKey = keyPair.getPrivateKey();

        final String message = "Hello world";
        final String message3000 = "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";
        final String messageOther = "2gZkajleugKwUsbtKwfFpBNHyup0poaggOMV9jXvX8F08o7rTyTh8KX1Nqf3bkh9I5kMSIv6CM90juVIrzsjhOA3ZVpB0OYYoCwngjy2VmXNTJyVOlMXutAGfQ3HGpjVs0bypznb9ynbIQemcVIKAOlbxLOVoWAmVPqTOEz5ZqnokzbHl0y6PWOBNTHeZLSNeN6ggHGvSOtsu8qmUB1n4l7WObvvobB10Mbv4I2xDjgYWED5KK6oQQfYwHlG1ich";
        final byte[] messageBytes = messageOther.getBytes();
        System.out.printf("Original bytes: %s\n", bytesToHex(messageBytes));
        final byte[] encryptedBytes = encrypt(messageBytes, publicKey, publicKeyExp);
        System.out.printf("Encrypted bytes: %s\n", bytesToHex(encryptedBytes));
        final byte[] decryptedBytes = decrypt(encryptedBytes, privateKey, publicKey);
        System.out.printf("Decrypted bytes: %s\n", bytesToHex(decryptedBytes));
    }
}