package br.com.finalelite.bots.supporter.utils;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

public class Captcha {

    private ImageCaptchaService service = new DefaultManageableImageCaptchaService();

    public byte[] createNewCaptcha(String id) {
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            BufferedImage challenge =
                    service.getImageChallengeForID(id, Locale.ENGLISH);

            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (CaptchaServiceException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jpegOutputStream.toByteArray();
    }

    public boolean check(String id, String text) {
        return service.validateResponseForID(id, text);
    }
}
