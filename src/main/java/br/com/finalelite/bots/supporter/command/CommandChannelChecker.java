package br.com.finalelite.bots.supporter.command;

import br.com.finalelite.bots.supporter.Supporter;
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
        if(disableChannelCheck)
            return true;

        val config = Supporter.getInstance().getConfig();
        if(usableInSupportChannel && textChannel.getId().equals(config.getSupportChannelId()))
            return true;

        if(usableInStaffChannel && textChannel.getId().equals(config.getStaffChannelId()))
            return true;

        val parent = textChannel.getParent();

        if(parent!= null && usableInOpenedCategory && parent.getId().equals(config.getOpenedCategoryId()))
            return true;

        if(parent!= null && usableInClosedCategory && parent.getId().equals(config.getClosedCategoryId()))
            return true;

        return false;
    }
}
