package de.nebelniek.inventory.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuName {

    GUILD_MAIN_MENU("§8» §2§lGilde §r§8«"),
    NOGUILD_MAIN_MENU("§8» §2§lGilden §r§8«"),
    BANK_TRANSFER_MENU("§8» §6§lBank §r§8«"),
    ;

    private String name;

}
