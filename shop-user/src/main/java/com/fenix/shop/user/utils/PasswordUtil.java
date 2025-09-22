package com.fenix.shop.user.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类
 * 用于密码加密、验证和盐值生成
 * 
 * @author AI Assistant
 * @since 2025-01-27
 */
@Slf4j
@Component
public class PasswordUtil {

    private final BCryptPasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;

    public PasswordUtil() {
        // 使用强度为12的BCrypt
        this.passwordEncoder = new BCryptPasswordEncoder(12);
        this.secureRandom = new SecureRandom();
    }

    /**
     * 生成随机盐值
     * @return Base64编码的盐值
     */
    public String generateSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 使用BCrypt加密密码
     * @param rawPassword 原始密码
     * @param salt 盐值（BCrypt会忽略此参数，但保留用于兼容性）
     * @return 加密后的密码
     */
    public String encode(String rawPassword, String salt) {
        try {
            // BCrypt内部会生成自己的盐值，这里的salt参数主要用于数据库存储的兼容性
            return passwordEncoder.encode(rawPassword);
        } catch (Exception e) {
            log.error("密码加密失败", e);
            throw new RuntimeException("密码加密失败");
        }
    }

    /**
     * 验证密码是否匹配
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @param salt 盐值（BCrypt会忽略此参数，但保留用于兼容性）
     * @return true-匹配，false-不匹配
     */
    public boolean matches(String rawPassword, String encodedPassword, String salt) {
        try {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            log.error("密码验证失败", e);
            return false;
        }
    }

    /**
     * 检查密码强度
     * @param password 密码
     * @return 密码强度等级 (1-弱, 2-中, 3-强)
     */
    public int checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return 0; // 无效密码
        }

        int score = 0;
        
        // 长度检查
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        
        // 字符类型检查
        if (password.matches(".*[a-z].*")) score++; // 包含小写字母
        if (password.matches(".*[A-Z].*")) score++; // 包含大写字母
        if (password.matches(".*\\d.*")) score++;    // 包含数字
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':,.<>?].*")) score++; // 包含特殊字符
        
        // 复杂度检查
        if (password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$")) score++; // 同时包含大小写字母和数字
        
        // 返回强度等级
        if (score <= 2) return 1; // 弱
        if (score <= 4) return 2; // 中
        return 3; // 强
    }

    /**
     * 生成随机密码
     * @param length 密码长度
     * @param includeSpecialChars 是否包含特殊字符
     * @return 随机密码
     */
    public String generateRandomPassword(int length, boolean includeSpecialChars) {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        
        StringBuilder charset = new StringBuilder();
        charset.append(lowercase).append(uppercase).append(digits);
        
        if (includeSpecialChars) {
            charset.append(specialChars);
        }
        
        StringBuilder password = new StringBuilder();
        
        // 确保密码包含各种字符类型
        password.append(lowercase.charAt(secureRandom.nextInt(lowercase.length())));
        password.append(uppercase.charAt(secureRandom.nextInt(uppercase.length())));
        password.append(digits.charAt(secureRandom.nextInt(digits.length())));
        
        if (includeSpecialChars) {
            password.append(specialChars.charAt(secureRandom.nextInt(specialChars.length())));
        }
        
        // 填充剩余长度
        for (int i = password.length(); i < length; i++) {
            password.append(charset.charAt(secureRandom.nextInt(charset.length())));
        }
        
        // 打乱字符顺序
        return shuffleString(password.toString());
    }

    /**
     * 打乱字符串字符顺序
     * @param input 输入字符串
     * @return 打乱后的字符串
     */
    private String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    /**
     * 验证密码是否符合安全要求
     * @param password 密码
     * @return true-符合要求，false-不符合要求
     */
    public boolean isPasswordSecure(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        // 检查是否包含至少一个大写字母、一个小写字母和一个数字
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,}$");
    }
}