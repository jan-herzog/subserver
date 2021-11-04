package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import de.nebelniek.BukkitSpringApplication;
import de.nebelniek.database.user.CloudUserRepository;
import de.nebelniek.services.verify.VerifyService;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
@CommandAlias("verify")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class VerifyCommand extends BaseCommand {

    private CloudUserRepository cloudUserRepository;

    private VerifyService verifyService;

    @Default
    @CatchUnknown
    public void onDefault(Player sender) {
        cloudUserRepository.findByUuidEquals(sender.getUniqueId()).thenAccept(cloudUser -> {
            if(cloudUser.getTwitchId() == null)
                verifyService.showVerify(sender);
            else sender.sendMessage(Prefix.TWITCH + "Du bist bereits §averifiziert§7!");
        });
    }
}
