package br.com.finalelite.discord.bot.manager;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.entity.Captcha;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;
import lombok.Getter;
import lombok.val;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CaptchaManager {

    private final ImageCaptchaService service = new DefaultManageableImageCaptchaService();
    private final Map<String, Integer> ids = new HashMap<>();
    @Getter
    private Map<String, Integer> captchaChannels = new HashMap<>();

    public CaptchaManager() {

        // thread to auto close idle captchas
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (captchaChannels.isEmpty())
                    continue;

                val newList = new HashMap<>(captchaChannels);
                captchaChannels.forEach((channelId, createIn) -> {
                    val c = Bot.getTextChannelById(channelId);
                    if (c == null)
                        return;

                    val now = new Date();
                    if (now.getTime() / 1000 >= createIn + 5 * 60) {
                        c.delete().complete();
                        val channel = Bot.getTextChannelById(Bot.getInstance().getConfig().getVerifyChannelId());
                        channel.getGuild().getController()
                                .kick(channel.getGuild().getMemberById(Bot.getInstance().getDatabase()
                                        .getCaptchaUserIdByChannelId(channelId)), "Tempo limite.").complete();
                        Bot.getInstance().getDatabase().setCaptchaStatus(channelId, Captcha.Status.TIMED_OUT);
                        newList.remove(channelId);
                    }
                });
                captchaChannels = newList;
            }
        }).start();
    }

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
