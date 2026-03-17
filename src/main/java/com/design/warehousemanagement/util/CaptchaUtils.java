package com.design.warehousemanagement.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 生成加减法公式和答案
 */
public class CaptchaUtils {

    private static final Random random = new Random();

    public static Map<String, Object> generateMathCaptcha() {
        int a = random.nextInt(10);
        int b = random.nextInt(10);
        boolean isAdd = random.nextBoolean();
        String question = (isAdd ? a + " + " : a + " - ") + b + " = ?";
        int answer = isAdd ? a + b : a - b;

        Map<String, Object> result = new HashMap<>();
        result.put("question", question);
        result.put("answer", answer);
        return result;
    }

    /**
     * 根据文本生成验证码图片
     *
     * @param text 显示的文本内容（如 "5 + 3 = ?"）
     * @return BufferedImage 对象
     * @throws IOException IO异常
     */
    public static BufferedImage createCaptchaImage(String text) throws IOException {
        int width = 120;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 设置字体和颜色
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.BLACK);

        // 绘制文字
        g.drawString(text, 10, 25);

        g.dispose();
        return image;
    }

    public static byte[] convertImageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
