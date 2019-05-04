package com.github.pauloo27.discord.bot.entity.command;

import com.github.pauloo27.discord.bot.Bot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.core.entities.TextChannel;

@RequiredArgsConstructor
@Getter
public enum CommandChannelChecker {
    DISABLE(
            true,
            false,
            false,
            false,
            false
    ),
    ALL(
            false,
            true,
            true,
            true,
            true
    ),
    SUPPORT_CHANNEL_ONLY(
            false,
            true,
            false,
            false,
            false
    ),
    OPENED_TICKET_MANAGEMENT(
            false,
            false,
            true,
            false,
            false
    ),
    CLOSED_TICKET_MANAGEMENT(
            false,
            false,
            false,
            true,
            false
    ),
    TICKET_MANAGEMENT(
            false,
            false,
            true,
            true,
            false
    ),
    TICKET_MANAGEMENT_AND_STAFF(
            false,
            false,
            true,
            true,
            true
    ),
    STAFF(
            false,
            false,
            false,
            false,
            true
    );

    private final boolean disableChannelCheck;
    private final boolean usableInSupportChannel;
    private final boolean usableInOpenedCategory;
    private final boolean usableInClosedCategory;
    private final boolean usableInStaffChannel;

    public boolean canRun(TextChannel textChannel) {
        if (disableChannelCheck)
            return true;

        val config = Bot.getInstance().getConfig();
        if (usableInSupportChannel && textChannel.getId().equals(config.getSupportChannelId()))
            return true;

        if (usableInStaffChannel && textChannel.getId().equals(config.getStaffChannelId()))
            return true;

        val parent = textChannel.getParent();

        if (parent != null && usableInOpenedCategory && parent.getId().equals(config.getOpenedCategoryId()))
            return true;

        return parent != null && usableInClosedCategory && parent.getId().equals(config.getClosedCategoryId());

    }
}
