package testPack;

import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

//gamerule doImmediateRespawn true

public class Main extends JavaPlugin implements Listener{
	
	String owner = null;
	boolean fight = false;
	boolean reset = true;
	boolean start = false;
	int startTime = 0;
	
	Scoreboard board;
	Team red;
	Team blue;
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		
		getCommand("redteam").setExecutor(new Cmd1());
		getCommand("blueteam").setExecutor(new Cmd2());
		getCommand("giveToken").setExecutor(new Cmd3());
		getCommand("killme").setExecutor(new Cmd4());
		
		new BoardManager().startBoard();
		
		board = new BoardManager().getBoard();
		
		red = new BoardManager().getRed();
		blue = new BoardManager().getBlue();
		
		red.setColor(ChatColor.DARK_RED);
		blue.setColor(ChatColor.DARK_AQUA);
		
		red.setPrefix(ChatColor.DARK_RED + "");
		blue.setPrefix(ChatColor.DARK_AQUA + "");
		red.setSuffix(ChatColor.DARK_RED + "");
		blue.setSuffix(ChatColor.DARK_AQUA + "");
		
		owner = null;
		new Bar().bar1.setProgress(0);
		new Bar().bar2.setProgress(0);
		
		new BukkitRunnable() {
			int time = 0;
			int clearTime = 0;
			
			@Override
			public void run() {
				time++;
				startTime++;
				
				if(startTime > 100) {
					if(time % 15 == 0 && reset == true && fight == false) {
						if(owner != null) {
							if(owner.equals("red")) {
								new Bar().bar1.setVisible(true);
								new Bar().bar2.setVisible(false);
								if(new Bar().bar1.getProgress() + 0.01 >= 1) {
									new Bar().bar1.setProgress(1);
								} else {
									new Bar().bar1.setProgress(new Bar().bar1.getProgress() + 0.01);
								}
							} else if(owner.equals("blue")) {
								new Bar().bar1.setVisible(false);
								new Bar().bar2.setVisible(true);
								if(new Bar().bar2.getProgress() + 0.01 >= 1) {
									new Bar().bar2.setProgress(1);
								} else {
									new Bar().bar2.setProgress(new Bar().bar2.getProgress() + 0.01);
								}
							}
						}
					}
					
					if(new Bar().bar1.getProgress() == 1 && fight == false && reset == true) {
						reset = false;
						clearTime = time;
						// 레드 승리
						for(Player all : Bukkit.getOnlinePlayers()) {
							if(red.getPlayers().contains(all)) {
								try {
									PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, 
											ChatSerializer.a("{\"text\":\"§C승리하였습니다!\"}"));
									Object handle = all.getClass().getMethod("getHandle").invoke(all);
							        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
							        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, title);
								} catch(Exception e) {
									
								}
								all.getWorld().playSound(all.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);
							} else if(blue.getPlayers().contains(all)) {
								try {
									PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, 
											ChatSerializer.a("{\"text\":\"§9패배하였습니다.\"}"));
									Object handle = all.getClass().getMethod("getHandle").invoke(all);
							        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
							        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, title);
								} catch(Exception e) {
									
								}
								all.getWorld().playSound(all.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1.0f);
							}
							
							new BukkitRunnable() {
								int time = 0;

								@Override
								public void run() {
									
									if(time == 10) {
										all.getWorld().playSound(all.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 4.0f, 1.0f);
									}
									
									if(time == 20) {
										all.getWorld().playSound(all.getLocation(), Sound.ENTITY_FIREWORK_LARGE_BLAST, 2.0f, 1.0f);
									}
									
									if(time == 25) {
										all.getWorld().playSound(all.getLocation(), Sound.ENTITY_FIREWORK_TWINKLE, 2.0f, 1.0f);
									}
									
									if(time >= 40) {
										this.cancel();
									}
									
									time++;
								}
							}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
							
						}
						redFirework(new Location(Bukkit.getWorld("world"), -102,65,19));
						redFirework(new Location(Bukkit.getWorld("world"), -135,65,20));
						redFirework(new Location(Bukkit.getWorld("world"), -133,65,52));
						redFirework(new Location(Bukkit.getWorld("world"), -111,65,49));
					} else if(new Bar().bar2.getProgress() == 1 && fight == false && reset == true) {
						reset = false;
						clearTime = time;
						// 블루 승리
						for(Player all : Bukkit.getOnlinePlayers()) {
							if(blue.getPlayers().contains(all)) {
								try {
									PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, 
											ChatSerializer.a("{\"text\":\"§C승리하였습니다!\"}"));
									Object handle = all.getClass().getMethod("getHandle").invoke(all);
							        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
							        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, title);
								} catch(Exception e) {
									
								}
								all.getWorld().playSound(all.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);
							} else if(red.getPlayers().contains(all)) {
								try {
									PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, 
											ChatSerializer.a("{\"text\":\"§9패배하였습니다.\"}"));
									Object handle = all.getClass().getMethod("getHandle").invoke(all);
							        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
							        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, title);
								} catch(Exception e) {
									
								}
								all.getWorld().playSound(all.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1.0f);
							}
							
							new BukkitRunnable() {
								int time = 0;

								@Override
								public void run() {
									
									if(time == 10) {
										all.getWorld().playSound(all.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 4.0f, 1.0f);
									}
									
									if(time == 20) {
										all.getWorld().playSound(all.getLocation(), Sound.ENTITY_FIREWORK_LARGE_BLAST, 2.0f, 1.0f);
									}
									
									if(time == 25) {
										all.getWorld().playSound(all.getLocation(), Sound.ENTITY_FIREWORK_TWINKLE, 2.0f, 1.0f);
									}
									
									if(time >= 40) {
										this.cancel();
									}
									
									time++;
								}
							}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
							
						}
						blueFirework(new Location(Bukkit.getWorld("world"), -102,65,19));
						blueFirework(new Location(Bukkit.getWorld("world"), -135,65,20));
						blueFirework(new Location(Bukkit.getWorld("world"), -133,65,52));
						blueFirework(new Location(Bukkit.getWorld("world"), -111,65,49));
					} 
				}
				
				if(clearTime != 0 && time - clearTime == 100) {
					for(Player all : Bukkit.getOnlinePlayers()) {
						try {
							red.removePlayer(all);
						} catch(Exception e) {
							
						}
						try {
							blue.removePlayer(all);
						} catch(Exception e) {
							
						}
						
						all.getInventory().clear();
						all.setHealth(20);
						all.setFoodLevel(20);
						for(PotionEffect effect : all.getActivePotionEffects()){
					        all.removePotionEffect(effect.getType());
					    }
						all.setFireTicks(0);
						
						all.teleport(new Location(all.getWorld(),-49.5,64,33.5,90,0));
					}
					
					new Bar().bar1.setVisible(false);
					new Bar().bar1.setProgress(0);
					new Bar().bar1.setTitle(ChatColor.RED + "Red Team");
					new Bar().bar2.setVisible(false);
					new Bar().bar2.setProgress(0);
					new Bar().bar2.setTitle(ChatColor.AQUA + "Blue Team");
					owner = null;
					fight = false;
					reset = true;
				}
				
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
		
		new BukkitRunnable() {
			int time = 0;

			@Override
			public void run() {
				
				if(time % 20 == 0) {
					new CoolTime().countTime();
					new CoolTime().board();
				}
				
				for(Player p : Bukkit.getOnlinePlayers()) {
					Scoreboard board = new MakeScoreboard().getManager().getNewScoreboard();
					
				}
				
				time++;
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
	}
	
	@Override
	public void onDisable() {
		getLogger().info("===============");
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer(); 
		
		new Bar().bar1.addPlayer(player);
		new Bar().bar2.addPlayer(player);
		
		if(new Bar().bar1.getProgress() == 0) {
			new Bar().bar1.setVisible(false);
		}
		if(new Bar().bar2.getProgress() == 0) {
			new Bar().bar2.setVisible(false);
		}
		
		//플레이어 접속 시
		if(red.getPlayers().contains(player)) {
			red.removePlayer(player);
		} else if(blue.getPlayers().contains(player)) {
			blue.removePlayer(player);
		}
		player.getInventory().clear();
		player.teleport(new Location(player.getWorld(),-49.5,64,33.5,90,0));
	}
	
	@EventHandler
	public void reSpawn(PlayerRespawnEvent event) {
		try {
			Player player = (Player) event.getPlayer();
			
			event.setRespawnLocation(new Location(player.getWorld(), -50, 64, 72));
			
			new BukkitRunnable() {
				int time = 0;
				
				@Override
				public void run() {
					time++;
					
					if(time >= 200) {
						if(red.getPlayers().contains(player)) {
							player.teleport(new Location(player.getWorld(), -123, 114, 96));
						} else if(blue.getPlayers().contains(player)) {
							player.teleport(new Location(player.getWorld(), -123, 114, -16, 180, 0));
						} else {
							player.teleport(new Location(player.getWorld(),-49.5,64,33.5,90,0));
							player.getInventory().clear();
						}
						this.cancel();
					}
					
				}
			}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
			
			player.setNoDamageTicks(220);
		} catch(Exception e11) {

		}
		try {
			Player player = (Player) event.getPlayer();
			
			new CoolTime().resetTime(player);
		} catch(Exception e11) {

		}
	}
	
	@EventHandler
	public void die(PlayerDeathEvent event) {
		
		if(red.getPlayers().contains(event.getEntity())) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(ChatColor.DARK_RED + "" + event.getEntity().getDisplayName() + ChatColor.WHITE + "님이 사망하였습니다.");
			}
		} else if(blue.getPlayers().contains(event.getEntity())) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(ChatColor.DARK_AQUA + "" + event.getEntity().getDisplayName() + ChatColor.WHITE + "님이 사망하였습니다.");
			}
		}
		
		if(red.getPlayers().contains(event.getEntity())) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(blue.getPlayers().contains(all)) {
					if(all.getLevel() < 10) {
						all.setLevel(all.getLevel()+2);
					}
				}
			}
		} else if(blue.getPlayers().contains(event.getEntity())) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(red.getPlayers().contains(all)) {
					if(all.getLevel() < 10) {
						all.setLevel(all.getLevel()+2);
					}
				}
			}
		}
		
		try {
			Player player = (Player)event.getEntity();
			PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, 
					ChatSerializer.a("{\"text\":\"§CYOU DIED\"}"));
			PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, 
					ChatSerializer.a("{\"text\":\"§710초 후에 부활합니다.\"}"));
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
	        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
	        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, title);
	        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, subtitle);
		} catch(Exception e) {
			
		}
	}
	
	@EventHandler
	public void mouseEvent(PlayerInteractEvent event) {
		try {
			EquipmentSlot e = event.getHand();
			if (e.equals(EquipmentSlot.HAND)) {
				Player player = event.getPlayer();
				if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if(event.getClickedBlock().getType() == Material.STONE_BUTTON || event.getClickedBlock().getType() == Material.WOOD_BUTTON) {
						
						if(start) {
							return;
						}
						
						start = true;
						
						Location loc = event.getClickedBlock().getLocation();
						// 대기실 -62 255 18  -46 77 48
						if (loc.getX() <= -46 && loc.getY() <= 255 && loc.getZ() <= 48 
							&& loc.getX() >= -62 && loc.getY() >= 0 && loc.getZ() >= 18) {
							
							boolean bool = true;
							
							for(Player all : Bukkit.getOnlinePlayers()) {
								Location loc2 = all.getLocation();
								// 경기장  -89 90 90  -155 0 -10
								if (loc2.getX() <= -89 && loc2.getY() <= 90 && loc2.getZ() <= 90 
										&& loc2.getX() >= -155 && loc2.getY() >= 0 && loc2.getZ() >= -10) {
									bool = false;
								}
								if(!bool) {
									start = false;
									player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "경기가 아직 끝나지 않았습니다.");
									return;
								}
							}
							
							for(Player all : Bukkit.getOnlinePlayers()) {
								all.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "10초 후에 게임이 시작됩니다.");
							}
							
							new Bar().bar1.setVisible(false);
							new Bar().bar1.setProgress(0);
							new Bar().bar2.setVisible(false);
							new Bar().bar2.setProgress(0);
							owner = null;
							fight = false;
							reset = true;
							
							new BukkitRunnable() {
								int time = 0;
								
								@Override
								public void run() {
									time++;
									
									if(time == 140) {
										try {
											for(Player all : Bukkit.getOnlinePlayers()) {
												PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, 
														ChatSerializer.a("{\"text\":\"§73\"}"));
												Object handle = all.getClass().getMethod("getHandle").invoke(all);
										        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
										        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, title);
											}
										} catch(Exception e) {
											
										}
									}
									
									if(time == 160) {
										try {
											for(Player all : Bukkit.getOnlinePlayers()) {
												PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, 
														ChatSerializer.a("{\"text\":\"§72\"}"));
												Object handle = all.getClass().getMethod("getHandle").invoke(all);
										        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
										        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, title);
											}
										} catch(Exception e) {
											
										}
									}
									
									if(time == 180) {
										try {
											for(Player all : Bukkit.getOnlinePlayers()) {
												PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, 
														ChatSerializer.a("{\"text\":\"§71\"}"));
												Object handle = all.getClass().getMethod("getHandle").invoke(all);
										        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
										        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, title);
											}
										} catch(Exception e) {
											
										}
									}
									
									if(time >= 200) {
										try {
											for(Player all : Bukkit.getOnlinePlayers()) {
												PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, 
														ChatSerializer.a("{\"text\":\"§7GAME START!\"}"));
												Object handle = all.getClass().getMethod("getHandle").invoke(all);
										        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
										        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, title);
											}
										} catch(Exception e) {
											
										}
										try {
											for(Player all : Bukkit.getOnlinePlayers()) {
												Location loc2 = all.getLocation();
												// 레드존 -59 255 17  -49 0 27
												// 블루존 -49 255 39  -59 0 49
												if (loc2.getX() <= -49 && loc2.getY() <= 255 && loc2.getZ() <= 27 
														&& loc2.getX() >= -59 && loc2.getY() >= 0 && loc2.getZ() >= 17) {
													// 레드팀
													red.addPlayer(all);
													
													EntityEquipment helmet = all.getEquipment();
													ItemStack helmetItem = new ItemStack(Material.LEATHER_HELMET);
													LeatherArmorMeta helmetmeta = (LeatherArmorMeta) helmetItem.getItemMeta();
													helmetmeta.setColor(Color.fromRGB(255, 0, 0));
													helmetmeta.setUnbreakable(true);
													helmetItem.setItemMeta(helmetmeta);
													helmet.setHelmet(helmetItem);
													EntityEquipment chestplate = all.getEquipment();
													ItemStack chestplateItem = new ItemStack(Material.LEATHER_CHESTPLATE);
													LeatherArmorMeta chestmeta = (LeatherArmorMeta) chestplateItem.getItemMeta();
													chestmeta.setColor(Color.fromRGB(255, 0, 0));
													chestmeta.setUnbreakable(true);
													chestplateItem.setItemMeta(chestmeta);
													chestplate.setChestplate(chestplateItem);
													EntityEquipment leggings = all.getEquipment();
													ItemStack leggingsItem = new ItemStack(Material.LEATHER_LEGGINGS);
													LeatherArmorMeta leggingsmeta = (LeatherArmorMeta) leggingsItem.getItemMeta();
													leggingsmeta.setColor(Color.fromRGB(255, 0, 0));
													leggingsmeta.setUnbreakable(true);
													leggingsItem.setItemMeta(leggingsmeta);
													leggings.setLeggings(leggingsItem);
													EntityEquipment boots = all.getEquipment();
													ItemStack bootsItem = new ItemStack(Material.LEATHER_BOOTS);
													LeatherArmorMeta bootsmeta = (LeatherArmorMeta) bootsItem.getItemMeta();
													bootsmeta.setColor(Color.fromRGB(255, 0, 0));
													bootsmeta.setUnbreakable(true);
													bootsItem.setItemMeta(bootsmeta);
													boots.setBoots(bootsItem);
													
													all.setHealth(20);
													all.setFoodLevel(20);
													all.setLevel(0);
													all.setExp(0);
													for(PotionEffect effect : all.getActivePotionEffects()){
												        all.removePotionEffect(effect.getType());
												    }
													all.setFireTicks(0);
													
													all.teleport(new Location(all.getWorld(), -123, 114, 96));
												} else if (loc2.getX() <= -49 && loc2.getY() <= 255 && loc2.getZ() <= 49 
														&& loc2.getX() >= -59 && loc2.getY() >= 0 && loc2.getZ() >= 39) {
													// 블루팀
													blue.addPlayer(all);
													
													EntityEquipment helmet = all.getEquipment();
													ItemStack helmetItem = new ItemStack(Material.LEATHER_HELMET);
													LeatherArmorMeta helmetmeta = (LeatherArmorMeta) helmetItem.getItemMeta();
													helmetmeta.setColor(Color.fromRGB(0, 0, 255));
													helmetmeta.setUnbreakable(true);
													helmetItem.setItemMeta(helmetmeta);
													helmet.setHelmet(helmetItem);
													EntityEquipment chestplate = all.getEquipment();
													ItemStack chestplateItem = new ItemStack(Material.LEATHER_CHESTPLATE);
													LeatherArmorMeta chestmeta = (LeatherArmorMeta) chestplateItem.getItemMeta();
													chestmeta.setColor(Color.fromRGB(0, 0, 255));
													chestmeta.setUnbreakable(true);
													chestplateItem.setItemMeta(chestmeta);
													chestplate.setChestplate(chestplateItem);
													EntityEquipment leggings = all.getEquipment();
													ItemStack leggingsItem = new ItemStack(Material.LEATHER_LEGGINGS);
													LeatherArmorMeta leggingsmeta = (LeatherArmorMeta) leggingsItem.getItemMeta();
													leggingsmeta.setColor(Color.fromRGB(0, 0, 255));
													leggingsmeta.setUnbreakable(true);
													leggingsItem.setItemMeta(leggingsmeta);
													leggings.setLeggings(leggingsItem);
													EntityEquipment boots = all.getEquipment();
													ItemStack bootsItem = new ItemStack(Material.LEATHER_BOOTS);
													LeatherArmorMeta bootsmeta = (LeatherArmorMeta) bootsItem.getItemMeta();
													bootsmeta.setColor(Color.fromRGB(0, 0, 255));
													bootsmeta.setUnbreakable(true);
													bootsItem.setItemMeta(bootsmeta);
													boots.setBoots(bootsItem);
													
													all.setHealth(20);
													all.setFoodLevel(20);
													all.setLevel(0);
													all.setExp(0);
													for(PotionEffect effect : all.getActivePotionEffects()){
												        all.removePotionEffect(effect.getType());
												    }
													all.setFireTicks(0);
													
													all.teleport(new Location(all.getWorld(), -123, 114, -16, 180, 0));
												}
											}
										} catch(Exception e) {
											
										}
										start = false;
										this.cancel();
									}
									
								}
							}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
							
						}
					}
				}
			}
		} catch(Exception e) {
			
		}
		
		try {
			EquipmentSlot e = event.getHand();
			if (e.equals(EquipmentSlot.HAND)) {
				Player player = event.getPlayer();
				
				if (event.getAction() == Action.RIGHT_CLICK_AIR) {
					if(player.getInventory().getItemInMainHand().getType() != Material.BOW) {
						if (player.getInventory().contains(Material.NETHER_STAR)) {
							MouseClickForSkill mc = new MouseClickForSkill();
							int time = mc.getTime(player);
							if (time == 0) {
								mc.click(player, "R");
							} else {
								mc.click(player, "R", time);
							}
						}
					}
				} else if(event.getAction() == Action.LEFT_CLICK_AIR) {
					if(player.getInventory().getItemInMainHand().getType() != Material.BOW) {
						if (player.getInventory().contains(Material.NETHER_STAR)) {
							MouseClickForSkill mc = new MouseClickForSkill();
							int time = mc.getTime(player);
							if (time == 0) {
								mc.click(player, "L");
							} else {
								mc.click(player, "L", time);
							}						
						}
					}
				}
			}
		} catch(Exception e) {
			
		}
		
		try {
			if (event.getAction() == Action.PHYSICAL) {
				if(event.getClickedBlock().getType() == Material.STONE_PLATE) {
					boolean bool = new PlateEvent().plateEvent(event.getPlayer());
					if(!bool) {
						event.setCancelled(true);
					}
				}
			}
		} catch (Exception e) {

		}
		
    }
	
	@EventHandler
	public void damageEvent(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();
		
		if(damager instanceof Player) {
			if(entity instanceof Player) {
				Player p1 = (Player) damager;
				Player p2 = (Player) entity;
				
				if(red.getPlayers().contains(p1)) {
					if(red.getPlayers().contains(p2)) {
						event.setCancelled(true);
						return;
					}
				}
				
				if(blue.getPlayers().contains(p1)) {
					if(blue.getPlayers().contains(p2)) {
						event.setCancelled(true);
						return;
					}
				}
			}
		}
		
		if(damager instanceof Arrow) {
			if(entity instanceof Player) {
				Arrow arrow = (Arrow) damager;
				if(arrow.getShooter() instanceof Player) {
					Player p1 = (Player) arrow.getShooter();
					Player p2 = (Player) entity;
					
					if(red.getPlayers().contains(p1)) {
						if(red.getPlayers().contains(p2)) {
							event.setCancelled(true);
							return;
						}
					}
					
					if(blue.getPlayers().contains(p1)) {
						if(blue.getPlayers().contains(p2)) {
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}
		
		try {
			if (event.getDamager() instanceof SpectralArrow) {
				event.setCancelled(true);
				return;
			}
		} catch (Exception e) {

		}
		
		if(damager instanceof Wolf) {
			if(entity instanceof Player) {
				event.setDamage(2);
			}
		}
		
		if(damager instanceof Spider) {
			if(entity instanceof Player) {
				event.setDamage(2);
			}
		}
		
		if(damager instanceof Ocelot) {
			if(entity instanceof Player) {
				event.setCancelled(true);
			}
		}
		
		if(damager instanceof Snowball) {
			if(entity instanceof Player) {
				event.setDamage(3);
			}
		}
		
		if(damager instanceof IronGolem) {
			if(entity instanceof Player) {
				event.setDamage(5);
			}
		}
		
		if(damager instanceof Arrow) {
			if(entity instanceof Player) {
				event.setDamage(4);
			}
			
			if(damager.getCustomName() != null) {
				if(damager.getCustomName().equals("독")) {
					new BukkitRunnable() {
						int time = 0;
						
						@Override
						public void run() {
							
							if(time % 20 == 0) {
								if(entity instanceof Player) {
									Player p = (Player) entity;
									p.damage(2);
								}
							}

							if(time >= 100) {
								this.cancel();
							}
							
							time++;
						}
					}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
				}
				
				if(damager.getCustomName().equals("저지")) {
					entity.teleport(((Entity)(((Arrow) damager).getShooter())).getLocation());
				}

				if(damager.getCustomName().equals("아이스")) {
					((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
				}
			}
			
		}
	}
	
	@EventHandler
	public void damage2Event(EntityDamageEvent event) {
		if(event.getCause() == DamageCause.LIGHTNING) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void chatEvent(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String msg = event.getMessage();
		event.setCancelled(true);
		
		if(red.getPlayers().contains(player)) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(red.getPlayers().contains(all)) {
					all.sendMessage(ChatColor.RED + "[" + player.getDisplayName() + "] " + ChatColor.WHITE + msg);
				} else if(all.isOp()) {
					all.sendMessage(ChatColor.RED + "[" + player.getDisplayName() + "] " + ChatColor.WHITE + msg);
				}
			}
		} else if(blue.getPlayers().contains(player)) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(blue.getPlayers().contains(all)) {
					all.sendMessage(ChatColor.BLUE + "[" + player.getDisplayName() + "] " + ChatColor.WHITE + msg);
				} else if(all.isOp()) {
					all.sendMessage(ChatColor.BLUE + "[" + player.getDisplayName() + "] " + ChatColor.WHITE + msg);
				}
			}
		} else {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(ChatColor.GRAY + "[" + player.getDisplayName() + "] " + ChatColor.WHITE + msg);
			}
		}
		
		try {
			if(player.getInventory().getItem(8).getItemMeta().getDisplayName().equals("와라에의 증표")) {
				if (msg.equals("매혹의 향기로 벌레를 유혹하는 두 송이의 아름다운 꽃이여! 지금 하나가 되어 그 꽃잎의 깊은 지옥에서, 새로운 위협을 낳아라! 나타나라, 굶주린 송곳니를 가진 늪!")) {
					int cool = 20;
					if (new CoolTime().coolCheck(player, cool, "RRR")) {
						new ParticleEffect(player).newEffect37();
						player.sendMessage(ChatColor.GREEN + "[스킬]스타브에리어가 발동됩니다.");
					} else {
						player.sendMessage(ChatColor.WHITE + "쿨타임: " + new CoolTime().returnCool(player, cool, "RRR"));
						player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.2f, 2.0f);
					}
				} else if (msg.equals("그 아름답고도 웅장한 날개를 펼치고, 빛의 속도로 적을 쳐라! 나타나라, 얼음 송곳!")) {
					int cool = 20;
					if (new CoolTime().coolCheck(player, cool, "RRL")) {
						new ParticleEffect(player).newEffect38();
						player.sendMessage(ChatColor.GREEN + "[스킬]아이스본이 발동됩니다.");
					} else {
						player.sendMessage(ChatColor.WHITE + "쿨타임: " + new CoolTime().returnCool(player, cool, "RRL"));
						player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.2f, 2.0f);
					}
				} else if (msg.equals("칠흑의 어둠에서 우둔한 힘에 저항하는 반역의 송곳니! 지금, 강림하라! 빛나는 칠흑의 날개!")) {
					int cool = 10;
					if (new CoolTime().coolCheck(player, cool, "RLL")) {
						new ParticleEffect(player).newEffect39();
						player.sendMessage(ChatColor.GREEN + "[스킬]리벨리온윙이 발동됩니다.");
					} else {
						player.sendMessage(ChatColor.WHITE + "쿨타임: " + new CoolTime().returnCool(player, cool, "RLL"));
						player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.2f, 2.0f);
					}
				} else if (msg.equals("사천룡을 거느리며, 제5차원에 군림하는 궁극룡이여! 지금이야말로 이 몸과 하나가 되는거다!")) {
					if (player.getLevel() >= 10) {
						player.setLevel(0);
						player.setExp(0);
						new ParticleEffect(player).newEffect40();
						player.sendMessage(ChatColor.GREEN + "[스킬]패왕룡 즈아크가 발동됩니다.");
					} else {
						player.sendMessage(ChatColor.WHITE + "궁극기는 10레벨에 사용할 수 있습니다.");
						player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.2f, 2.0f);
					}
				}
			}
		} catch(Exception e) {
			
		}
		
//		if(player.getDisplayName().equals("yumehama")) {
//			blueFirework(player.getLocation());
//		}
	}
	
	@EventHandler
	public void arrowRemove(ProjectileHitEvent event) {
		try {
			event.getEntity().getPassenger().remove(); 
		} catch(Exception e) {
			
		}
		event.getEntity().remove();
	}
	
	@EventHandler
	public void moveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(red.getPlayers().contains(player)) {
			Location loc = player.getLocation();
			// 점령존 -125 255 42  -121 0 37
			if(loc.getX() <= -121 && loc.getY() <= 120 && loc.getZ() <= 42 
					&& loc.getX() >= -125 && loc.getY() >= 116 && loc.getZ() >= 37) {
				occup("red");
			}
		} else if(blue.getPlayers().contains(player)) {
			Location loc = player.getLocation();
			// 점령존 -125 255 42  -121 0 37
			if(loc.getX() <= -121 && loc.getY() <= 120 && loc.getZ() <= 42 
					&& loc.getX() >= -125 && loc.getY() >= 116 && loc.getZ() >= 37) {
				occup("blue");
			}
		} 
		
		if(player.getLocation().add(0,-1,0).getBlock().getType() == Material.EMERALD_BLOCK) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
		}
		
//		for(Player all : Bukkit.getOnlinePlayers()) {
//			if(all.getDisplayName().equals("yumehama")) {
//				all.sendMessage("" + red.getPlayers().contains(all));
//				all.sendMessage("" + blue.getPlayers().contains(all));
//			}
//		}
		
	}
	
	@EventHandler
	public void dropEvent(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerOffEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(red.getPlayers().contains(player)) {
			red.removePlayer(player);
		} else if(blue.getPlayers().contains(player)) {
			blue.removePlayer(player);
		}
		player.getInventory().clear();
	}
	
	@EventHandler
	public void clickInv(InventoryClickEvent event) {
		try {
			if(event.getCurrentItem().getType() == Material.NETHER_STAR) {
				event.setCancelled(true);
			}
			if(event.getCursor().getType() == Material.NETHER_STAR) {
				event.setCancelled(true);
			}
			if(event.getClick() != ClickType.MIDDLE) {
				// 핫키 제한
				try {
					if(event.getHotbarButton() != -1) {
						event.setCancelled(true);
					}
				} catch(Exception e) {
					
				}
			}
		} catch (Exception e7) {
			System.err.println(e7);
		}
	}
	
	@EventHandler
	public void onBlockignite(BlockIgniteEvent event) {
		if (event.getIgnitingEntity().getType() == EntityType.LIGHTNING) {
			event.setCancelled(true);
		}
	}
	
	public void occup(String str) {
		
		fight = false;
		
		if(owner == null) {
			owner = str;
			startTime = 0;
			return;
		}
		
		if(str.equals("red")) {
			if(!str.equals(owner)) {
				for(Player all : Bukkit.getOnlinePlayers()) {
					Location loc = all.getLocation();
					// 점령존 -125 255 42  -121 0 37
					if(loc.getX() <= -121 && loc.getY() <= 255 && loc.getZ() <= 42 
							&& loc.getX() >= -125 && loc.getY() >= 0 && loc.getZ() >= 37) {
						if(blue.getPlayers().contains(all)) {
							fight = true;
						}
					}
				}
			} else {
				return;
			}
		}
		
		if(str.equals("blue")) {
			if(!str.equals(owner)) {
				for(Player all : Bukkit.getOnlinePlayers()) {
					Location loc = all.getLocation();
					// 점령존 -125 255 42  -121 0 37
					if(loc.getX() <= -121 && loc.getY() <= 255 && loc.getZ() <= 42 
							&& loc.getX() >= -125 && loc.getY() >= 0 && loc.getZ() >= 37) {
						if(red.getPlayers().contains(all)) {
							fight = true;
						}
					}
				}
			} else {
				return;
			}
		}
		
		if(!fight) {
			owner = str;
			startTime = 0;
		}
		
	}
	
	public void redFirework(Location loc) {
		Location normal = loc;
		World world = loc.getWorld(); 
		
		new BukkitRunnable() {
			int time = 0;
			int size = 0;

			@Override
			public void run() {
				Location e1;
				
				if(time % 2 == 0 && time < 20) {
					e1 = normal.clone().add(0, time/2, 0);
					world.spawnParticle(Particle.BLOCK_DUST, e1, 0,1,0,0,1);
				}
				
				if(time % 3 == 0 && time >= 20) {
					size++;
					double r = size;
					for (double pi = 0; pi <= Math.PI; pi += Math.PI / 5) {
						double y = r * Math.cos(pi) + 1.5;
						for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 10) {
							double x = r * Math.cos(theta) * Math.sin(pi);
							double z = r * Math.sin(theta) * Math.sin(pi);

							e1 = loc.clone().add(x, y+10, z);
							world.spawnParticle(Particle.REDSTONE, e1, 0,1,0,0,1);
						}
					}
				}
				
				if(time >= 40) {
					this.cancel();
				}
				
				time++;
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);

	}
	
	public void blueFirework(Location loc) {
		Location normal = loc;
		World world = loc.getWorld(); 
		
		new BukkitRunnable() {
			int time = 0;
			int size = 0;

			@Override
			public void run() {
				Location e1;
				
				if(time % 2 == 0 && time < 20) {
					e1 = normal.clone().add(0, time/2, 0);
					world.spawnParticle(Particle.REDSTONE, e1, 0,0.01,0,1,1);
				}
				
				if(time % 3 == 0 && time >= 20) {
					size++;
					double r = size;
					for (double pi = 0; pi <= Math.PI; pi += Math.PI / 5) {
						double y = r * Math.cos(pi) + 1.5;
						for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 10) {
							double x = r * Math.cos(theta) * Math.sin(pi);
							double z = r * Math.sin(theta) * Math.sin(pi);

							e1 = loc.clone().add(x, y+10, z);
							world.spawnParticle(Particle.REDSTONE, e1, 0,0.01,0,1,1);
						}
					}
				}
				
				if(time >= 40) {
					this.cancel();
				}
				
				time++;
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);

	}
	
	private Class<?> getNMSClass(String name) {
		try {
	        return Class.forName("net.minecraft.server."
	                + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
}