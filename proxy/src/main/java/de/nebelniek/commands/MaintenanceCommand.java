package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import de.nebelniek.services.maintenance.MaintenanceKey;
import de.nebelniek.services.maintenance.MaintenanceService;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("maintenance")
@CommandPermission("proxy.maintenance")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MaintenanceCommand extends BaseCommand {

    private final MaintenanceService maintenanceService;

    @Default
    public void onDefault(ProxiedPlayer sender) {
        sender.sendMessage(Prefix.PROXY + "Hilfe für §e/maintenance§7:");
        sender.sendMessage(Prefix.PROXY + "/maintenance §6[Server]");
        sender.sendMessage(Prefix.PROXY + "Blockiere einen Server für die Öffentlichkeit.");
    }

    @Default
    @CommandCompletion("SUBSERVER")
    public void onToggle(ProxiedPlayer sender, String type) {
        MaintenanceKey key = MaintenanceKey.valueOf(type);
        maintenanceService.toggle(key);
        sender.sendMessage(Prefix.PROXY + "§eMaintenance§7 für §6" + type + "§7 umgeschaltet! (" + (maintenanceService.get(key) ? "§atrue" : "§cfalse") + "§7)");
    }

}
