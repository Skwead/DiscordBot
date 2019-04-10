package br.com.finalelite.discord.bot.commands.support.utils;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.entity.ticket.Ticket;
import br.com.finalelite.discord.bot.utils.SimpleLogger;
import lombok.*;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TicketLogger {

    private Map<String, Function<Ticket, String>> ticketPlaceHolders = new HashMap<>();
    private Map<String, Function<Message, String>> messagePlaceHolders = new HashMap<>();
    private Map<String, Function<MessageAttachment, String>> attachmentPlaceHolders = new HashMap<>();


    {
        ticketPlaceHolders.put("id", ticket -> String.valueOf(ticket.getId()));
        ticketPlaceHolders.put("subject", Ticket::getSubject);
        ticketPlaceHolders.put("user_avatar", ticket -> ticket.getUser().getAvatarUrl());
        ticketPlaceHolders.put("user_name", ticket -> ticket.getUser().getName() + "#" + ticket.getUser().getDiscriminator());
        ticketPlaceHolders.put("date", ticket -> SimpleLogger.format(ticket.getDate()));
        ticketPlaceHolders.put("close_date", ticket -> SimpleLogger.format(ticket.getCloseDate()));
        ticketPlaceHolders.put("user_id", Ticket::getUserId);
        ticketPlaceHolders.put("ticket_display_name", ticket -> String
                .format("#%d%s", ticket.getId(), ticket.getName() == null ? "" : " - " + ticket.getName().replace("-", " ")));

        messagePlaceHolders.put("user_avatar", message -> message.getAuthor().getAvatarUrl());
        messagePlaceHolders.put("user_id", message -> message.getAuthor().getId());
        messagePlaceHolders.put("user_name", message -> message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator());
        messagePlaceHolders.put("message_raw", Message::getContentDisplay);
        messagePlaceHolders.put("message_id", Message::getId);
        messagePlaceHolders.put("message_date", message -> message.getCreationTime()
                .format(DateTimeFormatter.ofPattern(Bot.getInstance().getConfig().getDateFormat())));

        attachmentPlaceHolders.put("attachment_id", attachment -> String.valueOf(attachment.getId()));
        attachmentPlaceHolders.put("attachment_type", attachment -> attachment.getType().getName());
        attachmentPlaceHolders.put("attachment_url", attachment -> attachment.getAttachment().getUrl());
        attachmentPlaceHolders.put("attachment_caption", attachment -> attachment.getMessage().getContentRaw().isEmpty() ? "Clique para abrir" : attachment.getMessage().getContentRaw());
    }

    private String randomBase64() {
        val bytes = new byte[5];
        new Random().nextBytes(bytes);

        return Base64.getEncoder().encodeToString(bytes).substring(0, 7);
    }

    private String getValidURL(File baseFolder) {
        if (!baseFolder.exists())
            baseFolder.mkdirs();

        val names = baseFolder.list();
        val list = names == null ? new ArrayList<String>() : Arrays.asList(names);

        var name = "";
        do {
            name = randomBase64();
        } while (!name.matches("\\w*") || list.contains(name));


        return name;
    }

    public String generateLog(Ticket ticket) {
        val baseContent = loadBasePageContent();

        val messages = new StringBuilder();
        val attachments = new StringBuilder();
        loadMessages(ticket, messages, attachments);

        var formattedContent = baseContent.replace("${messages}", messages)
                .replace("${attachments}", attachments);

        formattedContent = formatPlaceHolders(ticket, formattedContent, false, ticketPlaceHolders);

        var baseFolderName = Bot.getInstance().getConfig().getTranscriptionFolder();
        if (!baseFolderName.endsWith("/"))
            baseFolderName = baseFolderName + "/";

        val base64 = getValidURL(new File(baseFolderName));
        val folderName = baseFolderName + base64;

        val folder = new File(folderName);
        if (!folder.exists())
            folder.mkdirs();

        val file = new File(folder, "index.html");
        try {
            file.createNewFile();
            Files.write(file.toPath(), Arrays.asList(formattedContent.split("\n")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return base64;
    }

    private void loadMessages(Ticket ticket, StringBuilder messages, StringBuilder attachments) {
        val channel = Bot.getTextChannelById(ticket.getChannelId());

        val baseMessage = loadBaseMessageContent();
        val baseImageAttachment = loadBaseAttachmentContent();
        val messageHistory = channel.getIterableHistory().complete();
        Collections.reverse(messageHistory);

        val attachmentsCount = new AtomicInteger();

        messageHistory.forEach(message -> addMessage(baseMessage, baseImageAttachment, messages, attachments, message, attachmentsCount));
    }

    public void addMessage(String baseMessage, String baseImageAttachment, StringBuilder messages, StringBuilder attachments, Message message, AtomicInteger totalAttachments) {
        if (message.getType() == MessageType.CHANNEL_PINNED_ADD)
            return;

        if (!message.getAttachments().isEmpty()) {
            addAttachment(baseImageAttachment, attachments, message, totalAttachments.get());

            val currentAttachment = totalAttachments.getAndIncrement();
            val linkedMessage = baseMessage.replace("${message_raw}", "<a href=\"#attachment-id-" + currentAttachment + "\">ANEXO " + currentAttachment + "</a>");

            var formattedContent = formatPlaceHolders(message, linkedMessage, true, messagePlaceHolders);

            messages.append("\n").append(formattedContent.replace(":white_check_mark:", "\u2705"));
            return;
        }

        var formattedContent = formatPlaceHolders(message, baseMessage, true, messagePlaceHolders);

        messages.append("\n").append(formattedContent);
    }

    public void addAttachment(String baseAttachment, StringBuilder attachments, Message message, int id) {
        var formattedContent = formatPlaceHolders(new MessageAttachment(id, message, message.getAttachments().get(0), message.getAttachments().get(0).isImage() ? MessageAttachment.AttachmentType.IMAGE : MessageAttachment.AttachmentType.OTHER), baseAttachment, true, attachmentPlaceHolders);

        attachments.append("\n").append(formattedContent);
    }

    private String loadBasePageContent() {
        return loadResourceContent("/webpage/index.html");
    }

    private String loadBaseMessageContent() {
        return loadResourceContent("/webpage/message.html");
    }

    private String loadBaseAttachmentContent() {
        return loadResourceContent("/webpage/attachment.html");
    }

    private String loadResourceContent(String resourceName) {
        val inputStream = TicketLogger.class.getResourceAsStream(resourceName);
        val reader = new BufferedReader(new InputStreamReader(inputStream));

        return reader.lines().collect(Collectors.joining("\n"));
    }

    private <T> String formatPlaceHolders(T object, String text, boolean appendLinesWithHTMLTag, Map<String, Function<T, String>> placeHolders) {
        val pattern = Pattern.compile("\\$\\{\\w*}");
        val matcher = pattern.matcher(text);
        var newText = text;

        while (matcher.find()) {
            val group = matcher.group();
            val key = group.substring(2, group.length() - 1);

            if (placeHolders.containsKey(key.toLowerCase())) {
                val newString = placeHolders.get(key.toLowerCase()).apply(object);
                newText = newText.replace(group, appendLinesWithHTMLTag ? escapeHTML(newString).replace("\n", "<br>") : escapeHTML(newString));
            }
        }
        return newText;
    }

    private String escapeHTML(String unsafe) {
        return unsafe
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#039;")
                .replace("$", "&#36;");
    }

    @Data
    private static class MessageAttachment {

        private final int id;
        private final Message message;
        private final Message.Attachment attachment;
        private final AttachmentType type;

        @RequiredArgsConstructor
        @Getter
        private enum AttachmentType {
            IMAGE("image"),
            OTHER("other");

            private final String name;
        }
    }

}
