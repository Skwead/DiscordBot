package br.com.finalelite.discord.bot.utils;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;
import lombok.val;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CaptchaManager {

    private final ImageCaptchaService service = new DefaultManageableImageCaptchaService();
    private final Map<String, Integer> ids = new HashMap<>();

    public byte[] createNewCaptcha(String id) {
        ids.put(id, 1);
        return getImage(id);
    }

    public byte[] createAnotherCaptcha(String id) {
        ids.put(id, ids.get(id) + 1);
        return getImage(id);
    }

    private byte[] getImage(String id) {
        val jpegOutputStream = new ByteArrayOutputStream();
        try {
            val challenge =
                    service.getImageChallengeForID(id, Locale.ENGLISH);

            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (CaptchaServiceException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jpegOutputStream.toByteArray();
    }

    public int getTries(String id) {
        return ids.getOrDefault(id, 0);
    }

    public boolean check(String id, String text) {
        return service.validateResponseForID(id, text);
    }
}
