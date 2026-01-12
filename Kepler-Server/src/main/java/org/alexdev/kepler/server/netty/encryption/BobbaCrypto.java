package org.alexdev.kepler.server.netty.encryption;

import org.alexdev.kepler.util.encoding.Base64Encoding;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * Reverse Engineered by <a href="https://github.com/UnfamiliarLegacy">Mikee</a>.
 */
public class BobbaCrypto implements BobbaCryptoKeys {

    private static final int BITLENGTH = 64;

    private static final String G = "23786635532332886537261431906453031264918297";
    private static final String P = "632158881801130885249042417232212770524741295422564233061391190031954228421232913648184592218883487397503624904102572293826728806813079";

    private static final String HKDF_SALT = "BobbaXtraHKDFSalt";
    private static final String HKDF_INFO_PREFIX = "BobbaXtra|";

    private final BigInteger clientG;
    private final BigInteger clientP;
    private final BigInteger privateKey;
    private final BigInteger publicKey;

    private BigInteger sharedKey;
    private BobbaChaChaKey c2sData;
    private BobbaChaChaKey c2sHeader;
    private BobbaChaChaKey s2cData;
    private BobbaChaChaKey s2cHeader;

    public BobbaCrypto() {
        this(generatePrivateKey());
    }

    public BobbaCrypto(BigInteger privateKey) {
        this.clientG = new BigInteger(G);
        this.clientP = new BigInteger(P);
        this.privateKey = privateKey;
        this.publicKey = computePublicKey(this.privateKey);
    }

    @Override
    public BobbaChaChaKey getC2sData() {
        return c2sData;
    }

    @Override
    public BobbaChaChaKey getC2sHeader() {
        return c2sHeader;
    }

    @Override
    public BobbaChaChaKey getS2cData() {
        return s2cData;
    }

    @Override
    public BobbaChaChaKey getS2cHeader() {
        return s2cHeader;
    }

    private BigInteger computePublicKey(BigInteger privateKey) {
        return this.clientG.modPow(privateKey, this.clientP);
    }

    public String generatePublicKey() {
        return publicKey.toString();
    }

    private BigInteger computeSharedSecret(BigInteger privateKey, BigInteger publicKey) {
        return publicKey.modPow(privateKey, this.clientP);
    }

    public void setRemotePublicKey(String publicServerKey) {
        this.sharedKey = computeSharedSecret(this.privateKey, new BigInteger(publicServerKey));

        final byte[] data = BobbaCryptoUtils.getKeyBytes(this.sharedKey);

        this.c2sData = createKey(data, "bobba-c2s-data");
        this.c2sHeader = createKey(data, "bobba-c2s-header");
        this.s2cData = createKey(data, "bobba-s2c-data");
        this.s2cHeader = createKey(data, "bobba-s2c-header");
    }

    public boolean hasKeys() {
        return this.sharedKey != null &&
                this.c2sData != null &&
                this.c2sHeader != null &&
                this.s2cData != null &&
                this.s2cHeader != null;
    }

    public static byte[] encrypt(byte[] data, BobbaChaChaKey chaChaKey) {
        return encrypt(data, 0, data.length, chaChaKey);
    }

    public static byte[] encrypt(byte[] data, int offset, int length, BobbaChaChaKey chaChaKey) {
        final byte[] encrypted = BobbaCrypto.applyChaCha(data, 0, length, chaChaKey);
        return Base64Encoding.encode(encrypted, 0, encrypted.length);
    }

    public static byte[] decrypt(byte[] data, BobbaChaChaKey chaChaKey) {
        return decrypt(data, 0, data.length, chaChaKey);
    }

    public static byte[] decrypt(byte[] data, int offset, int length, BobbaChaChaKey chaChaKey) {
        final byte[] decoded = Base64Encoding.decode(data, offset, length);
        return BobbaCrypto.applyChaCha(decoded, 0, decoded.length, chaChaKey);
    }

    public static byte[] applyChaCha(byte[] data, int offset, int length, BobbaChaChaKey chaChaKey) {
        final byte[] result = new byte[length];

        StreamCipher chacha = new ChaCha7539Engine();

        chacha.init(false, new ParametersWithIV(new KeyParameter(chaChaKey.getKey()), chaChaKey.getNextNonce()));
        chacha.processBytes(data, offset, length, result, 0);

        return result;
    }

    private static BigInteger generatePrivateKey() {
        SecureRandom random = new SecureRandom();
        BigInteger privateKey;
        do {
            privateKey = new BigInteger(BITLENGTH, random);
        } while (privateKey.compareTo(BigInteger.ZERO) == 0);
        return privateKey;
    }

    private static BobbaChaChaKey createKey(final byte[] ikm, final String type) {
        final HKDFBytesGenerator hkdf = new HKDFBytesGenerator(new SHA256Digest());

        hkdf.init(new HKDFParameters(ikm, HKDF_SALT.getBytes(StandardCharsets.UTF_8), (HKDF_INFO_PREFIX + type).getBytes(StandardCharsets.UTF_8)));

        final byte[] key = new byte[32];
        final byte[] nonce = new byte[12];

        if (hkdf.generateBytes(key, 0, key.length) != key.length) {
            throw new IllegalStateException("HKDF failed to generate enough bytes for key");
        }

        if (hkdf.generateBytes(nonce, 0, nonce.length) != nonce.length) {
            throw new IllegalStateException("HKDF failed to generate enough bytes for nonce");
        }

        return new BobbaChaChaKey(key, nonce);
    }
}
