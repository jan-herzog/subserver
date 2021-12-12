package de.nebelniek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import de.nebelniek.configuration.BukkitConfiguration;
import de.nebelniek.utils.Prefix;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Component
@CommandAlias("dynamicslots|ds")
@CommandPermission("bukkit.dynamicslots")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DynamicSlotsCommand extends BaseCommand {

    private final BukkitConfiguration bukkitConfiguration;

    private Field maxPlayersField;

    @SneakyThrows
    @Default
    public void onDefault(Player sender, int slots) {
        changeSlots(slots);
        sender.sendMessage(Prefix.SUBSERVER + "Slots zu §e" + slots + "§7 geändert.");
    }

    private void changeSlots(int slots) throws ReflectiveOperationException {
        Method serverGetHandle = bukkitConfiguration.getPlugin().getServer().getClass().getDeclaredMethod("getHandle");
        Object playerList = serverGetHandle.invoke(bukkitConfiguration.getPlugin().getServer());
        if (this.maxPlayersField == null)
            this.maxPlayersField = getMaxPlayersField(playerList);
        this.maxPlayersField.setInt(playerList, slots);
    }

    private Field getMaxPlayersField(Object playerList) throws ReflectiveOperationException {
        Class<?> playerListClass = playerList.getClass().getSuperclass();

        try {
            Field field = playerListClass.getDeclaredField("maxPlayers");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            for (Field field : playerListClass.getDeclaredFields()) {
                if (field.getType() != int.class) {
                    continue;
                }
                field.setAccessible(true);

                if (field.getInt(playerList) == Bukkit.getMaxPlayers()) {
                    return field;
                }
            }

            throw new NoSuchFieldException("Unable to find maxPlayers field in " + playerListClass.getName());
        }
    }

}
