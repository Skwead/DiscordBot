package com.github.pauloo27.discord.bot.entity.command;

import com.github.pauloo27.discord.bot.Bot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommandPermission {
    EVERYONE("Todo mundo", null),
    SUPPORT("Suporte", Bot.getInstance().getConfig().getSupportRoleId()),
    MODERATOR("Moderador", Bot.getInstance().getConfig().getModeratorRoleId()),
    ADMIN("Admin", Bot.getInstance().getConfig().getAdminRoleId()),
    MANAGER("Supervisor", Bot.getInstance().getConfig().getManagerRoleId()),
    MASTER("Master", Bot.getInstance().getConfig().getMasterRoleId()),
    BOT_OWNER("Dono do Bot", null);

    private final String displayName;
    private final String roleId;
}
