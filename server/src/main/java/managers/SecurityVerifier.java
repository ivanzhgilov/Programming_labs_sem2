package managers;

import utility.CryptoUtils;

public class SecurityVerifier {
    private static final long MAX_TIME_SKEW_MS = 30000;

    /**
     * Расшифровывает значение и проверяет временную метку
     * 
     * @param encryptedValue Зашифрованная строка в Base64
     * @return Расшифрованный текст без временной метки
     * @throws SecurityException если данные повреждены или время запроса истекло
     */
    public static String verifyAndDecrypt(String encryptedValue) {
        if (encryptedValue == null) {
            throw new SecurityException("Данные авторизации отсутствуют");
        }

        String decrypted = CryptoUtils.decrypt(encryptedValue);
        int lastPipeIndex = decrypted.lastIndexOf('|');
        if (lastPipeIndex == -1) {
            throw new SecurityException("Неверный формат зашифрованных данных");
        }

        String value = decrypted.substring(0, lastPipeIndex);
        String timestampStr = decrypted.substring(lastPipeIndex + 1);
        
        try {
            long timestamp = Long.parseLong(timestampStr);
            long currentTime = System.currentTimeMillis();
            if (Math.abs(currentTime - timestamp) > MAX_TIME_SKEW_MS) {
                throw new SecurityException("Временная метка запроса невалидна (просрочена)");
            }
        } catch (NumberFormatException e) {
            throw new SecurityException("Некорректный формат временной метки");
        }
        return value;
    }
}
