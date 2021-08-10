package com.li.gamesocket.ssl;

import org.springframework.util.StringUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * @author li-yuanwen
 */
public class SslContextFactory {

    public static SSLContext ONE_WAY_SSL_CONTEXT;
    public static SSLContext TWO_WAY_SSL_CONTEXT;

    public static SSLEngine getSslEngine(SslConfig sslConfig) {
        switch (sslConfig.getSslMode()) {
            case CA:
                return buildSslContext(sslConfig).createSSLEngine();
            case CAS:
                SSLEngine sslEngine = buildSslContext(sslConfig).createSSLEngine();
                sslEngine.setNeedClientAuth(true);
                return sslEngine;
            default:
                throw new Error("UnsupportedOperation SSL MODE " + sslConfig.getSslMode().name());

        }
    }

    public static SSLContext buildSslContext(SslConfig sslConfig) {
        if (sslConfig.getSslMode() == SslMode.CA && ONE_WAY_SSL_CONTEXT != null) {
            return ONE_WAY_SSL_CONTEXT;
        }

        if (sslConfig.getSslMode() == SslMode.CAS && TWO_WAY_SSL_CONTEXT != null) {
            return TWO_WAY_SSL_CONTEXT;
        }

        InputStream in = null;
        InputStream tIN = null;

        SSLContext sslContext = null;
        try {
            KeyManagerFactory kmf = null;
            if (!StringUtils.isEmpty(sslConfig.getPkPath())) {
                KeyStore ks = KeyStore.getInstance(sslConfig.getStoreType());
                in = new FileInputStream(sslConfig.getPkPath());
                ks.load(in, sslConfig.getPassword().toCharArray());
                kmf = KeyManagerFactory.getInstance(sslConfig.getAlgorithm());
                kmf.init(ks, sslConfig.getPassword().toCharArray());
            }

            TrustManagerFactory tf = null;
            if (!StringUtils.isEmpty(sslConfig.getCaPath())) {
                KeyStore tks = KeyStore.getInstance(sslConfig.getStoreType());
                tIN = new FileInputStream(sslConfig.getCaPath());
                tks.load(tIN, sslConfig.getPassword().toCharArray());
                tf = TrustManagerFactory.getInstance(sslConfig.getAlgorithm());
                tf.init(tks);
            }

            sslContext = SSLContext.getInstance(sslConfig.getProtocol());
            switch (sslConfig.getSslMode()) {
                case CA:
                    sslContext.init(kmf.getKeyManagers(), null, null);
                    ONE_WAY_SSL_CONTEXT = sslContext;
                    break;
                case CAS:
                    sslContext.init(kmf.getKeyManagers(), tf.getTrustManagers(), null);
                    TWO_WAY_SSL_CONTEXT = sslContext;
                    break;
                default:
                    throw new Error("UnsupportedOperation SSL MODE " + sslConfig.getSslMode().name());
            }


        }catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }finally {
            if (in != null) {
                try {
                    in.close();;
                }catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    in = null;
                }
            }

            if (tIN != null) {
                try {
                    tIN.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    tIN = null;
                }
            }
        }

        return sslContext;

    }

}
