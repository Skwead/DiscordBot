package br.com.finalelite.bots.supporter.command;

import br.com.finalelite.bots.supporter.Supporter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommandPermission {
    EVERYONE("Todo mundo", null),
    SUPPORT("Suporte", Supporter.getInstance().getConfig().getSupportRoleId()),
    MODERATOR("Moderador", Supporter.getInstance().getConfig().getModeratorRoleId()),
    ADMIN("Admin", Supporter.getInstance().getConfig().getAdminRoleId()),
    MANAGER("Supervisor", Supporter.getInstance().getConfig().getManagerRoleId()),
    MASTER("Master", Supporter.getInstance().getConfig().getMasterRoleId()),
    BOT_OWNER("Dono do Bot", null);

    private final String displayName;
    private final String roleId;
}
