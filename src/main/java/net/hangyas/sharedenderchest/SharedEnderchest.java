package net.hangyas.sharedenderchest;

import static com.avaje.ebeaninternal.server.cluster.socket.SocketClusterMessage.packet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.material.EnderChest;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author adamkrisz
 */
public class SharedEnderchest extends JavaPlugin implements Listener{

    public static Inventory chest;
    
    @Override
    public void onEnable() {
        File f = new File(getDataFolder() + File.separator);
        
        if (f.isDirectory() && getChestFile().exists()){
            try {
                loadChest();
            } catch (IOException ex) {
                getLogger().info("ERROR:" + ex.getMessage());
            }
        }else{
            f.mkdirs();
            chest = Bukkit.createInventory(null, 27, "Enderchest");
            try {
                saveChest();
            } catch (IOException ex) {
                getLogger().info("ERROR:" + ex.getMessage());
            }
        }

        getServer().getPluginManager().registerEvents(this, this);
    }
 
    @Override
    public void onDisable() {
        try {
            saveChest();
        } catch (IOException ex) {
            getLogger().info("ERROR:" + ex.getMessage());
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
            event.getClickedBlock().getType().equals(Material.ENDER_CHEST)){
            
            event.setCancelled(true);
            
            final Player player = (Player) event.getPlayer();
            player.openInventory(chest);
//            player.playNote(event.getClickedBlock().getLocation(), (byte)1, (byte)1);
//            player.getHandle().playerConnection.sendPacket(packet);
//            player.setMetadata("openedEnderChest", new FixedMetadataValue(this, event.getClickedBlock()));
        }
    }
    
    /*
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if (!(event.getPlayer() instanceof Player) || !event.getPlayer().hasMetadata("openedEnderChest"))
            return;
        Player player = (Player)event.getPlayer();
        player.playNote(((Block)player.getMetadata("openedEnderChest")).getLocation(), (byte)1, (byte)0);
        player.removeMetadata("openedEnderChest", this);
    }*/
    
    private File getChestFile(){
        return new File(getDataFolder() + File.separator + "sharedchest.txt");
    }
    
    private void loadChest() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(getChestFile()));
        StringBuilder sb = new StringBuilder();
        
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String str = sb.toString();        
        
        chest = BukkitSerialization.fromBase64(str);

        reader.close();
    }
    
    private void saveChest() throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(getChestFile()));
        writer.write(BukkitSerialization.toBase64(chest));
        writer.close();
    }
}
