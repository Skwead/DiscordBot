package br.com.finalelite.bots.supporter.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommandType {
    DEFAULT(true, false, false, false, false),
    ALL(false, true, true, true, true),
    SUPPORT_MANAGEMENT(false, true, false, false, false),
    OPENED_TICKET_MANAGEMENT(false, false, true, false, false),
    CLOSED_TICKET_MANAGEMENT(false, false, false, true, false),
    TICKET_MANAGEMENT(false, false, true, true, false),
    TICKET_MANAGEMENT_AND_STAFF(false, false, true, true, true),
    STAFF(false, false, false, false, true);

    private final boolean disableChannelCheck;
    private final boolean usableInSupportChannel;
    private final boolean usableInOpenedCategory;
    private final boolean usableInClosedCategory;
    private final boolean usableInStaffChannel;
}
