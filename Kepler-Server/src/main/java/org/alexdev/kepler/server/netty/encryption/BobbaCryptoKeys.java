package org.alexdev.kepler.server.netty.encryption;

/**
 * Reverse Engineered by <a href="https://github.com/UnfamiliarLegacy">Mikee</a>.
 */
public interface BobbaCryptoKeys {

    BobbaChaChaKey getC2sData();

    BobbaChaChaKey getC2sHeader();

    BobbaChaChaKey getS2cData();

    BobbaChaChaKey getS2cHeader();

}
