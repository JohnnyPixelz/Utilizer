package io.github.johnnypixelz.utilizer.depend.dependencies;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public class VaultWrapper {
    private static Economy economy;
    private static Permission permission;
    private static Chat chat;

    public Economy getEconomy() {
        if (economy == null) {
            RegisteredServiceProvider<Economy> rsp = getServer()
                    .getServicesManager()
                    .getRegistration(Economy.class);
            economy = rsp.getProvider();
        }

        return economy;
    }

    public Permission getPermission() {
        if (permission == null) {
            RegisteredServiceProvider<Permission> rsp = getServer()
                    .getServicesManager()
                    .getRegistration(Permission.class);
            permission = rsp.getProvider();
        }

        return permission;
    }

    public Chat getChat() {
        if (chat == null) {
            RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
            chat = rsp.getProvider();
        }

        return chat;
    }
}
