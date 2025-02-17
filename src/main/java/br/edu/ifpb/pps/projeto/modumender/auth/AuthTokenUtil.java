package br.edu.ifpb.pps.projeto.modumender.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Gera e valida tokens no formato
 * "username:timestamp:signatureBase64",
 * simulando um JWT simplificado.
 */
public class AuthTokenUtil {

    private static final String SECRET_KEY = "MINHA_CHAVE_SECRETA";

    public static String generateToken(String username) {
        long ts = System.currentTimeMillis();
        String data = username + ":" + ts;
        String signature = signature(data);
        return username + ":" + ts + ":" + signature;
    }

    public static String validateToken(String token) {
        try {
            String[] parts = token.split(":");
            if (parts.length != 3) return null;

            String user = parts[0];
            String timeStr = parts[1];
            String sig = parts[2];

            String data = user + ":" + timeStr;
            String expected = signature(data);
            if (!expected.equals(sig)) return null;

            // expira em 30 min
            long sent = Long.parseLong(timeStr);
            long now = System.currentTimeMillis();
            if (now - sent > 30 * 60_000) {
                return null;
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String signature(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((data + SECRET_KEY).getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
