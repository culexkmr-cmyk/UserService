package com.culex.userService.config;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Configuration
public class JwtKeyConfig {

    @Value("${PRIVATE_KEY_PATH:classpath:keys/private.pem}")
    private Resource privateKeyResource;

    @Bean
    public PrivateKey privateKey() throws Exception {
        try (PemReader pemReader = new PemReader(new InputStreamReader(privateKeyResource.getInputStream()))) {
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(content);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyInfo.getEncoded()));
        }
    }
}
