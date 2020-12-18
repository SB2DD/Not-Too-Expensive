package me.mr_redstone5230.nottooexpensive;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class NotTooExpensive extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

    public void onEnable() {
        saveDefaultConfig();

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginCommand("not-too-expensive").setExecutor(this);
        this.getServer().getPluginCommand("not-too-expensive").setTabCompleter(this);
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player)event.getWhoClicked();
            if (event.getInventory() instanceof AnvilInventory) {
                ItemStack item = event.getCurrentItem();
                if (item == null) {
                    return;
                }

                if (getConfig().getStringList("items").contains(item.getType().name())) {
                    Repairable repairable = (Repairable)item.getItemMeta();
                    int current = repairable.getRepairCost();
                    if (current != -1 && current >= getConfig().getInt("cost")) {
                        repairable.setRepairCost(getConfig().getInt("cost"));
                        item.setItemMeta((ItemMeta)repairable);
                        event.setCurrentItem(item);
                        if (getConfig().getBoolean("message.enabled") && current != getConfig().getInt("cost")) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',getConfig().getString("message.text").replace("%cost%", String.valueOf(getConfig().getInt("cost")))));
                        }
                    }
                }
            }
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args == null || args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "Not too expensive.\nPlugin made by Mr_redstone5230");
            sender.sendMessage(ChatColor.GOLD + "Usage: /not-too-expensive [reload]");
            return true;
        }
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("not-too-expensive.reload")) {
                    sender.sendMessage(ChatColor.RED + "Not enough permission to use this command !");
                    return true;
                }
                reloadConfig();
                saveDefaultConfig();
                sender.sendMessage(ChatColor.GREEN + "Config reloaded !");
                return true;
            } else {
                sender.sendMessage(ChatColor.GOLD + "Usage: /not-too-expensive [reload]");
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Wrong usage: /not-too-expensive [reload]");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<>();
        if (sender.hasPermission("not-too-expensive.reload"))
            options.add("reload");
        return options;
    }
}
