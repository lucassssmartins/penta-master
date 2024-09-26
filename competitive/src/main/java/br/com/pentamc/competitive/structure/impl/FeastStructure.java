package br.com.pentamc.competitive.structure.impl;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.structure.Chestable;
import br.com.pentamc.competitive.structure.Structure;
import lombok.AllArgsConstructor;
import lombok.Getter;
import br.com.pentamc.common.CommonConst;

public class FeastStructure implements Structure, Chestable {

    private int radius;
    private int maxSpawnDistance;
    private static Set<Block> feastBlocks = new HashSet<>();

    public FeastStructure() {
        this(25, 150);
    }

    public FeastStructure(int radius, int maxSpawnDistance) {
        this.radius = radius;
        this.maxSpawnDistance = maxSpawnDistance;
    }

    @Override
    public Location findPlace() {
        World w = Bukkit.getWorld("world");
        Random r = new Random();
        int x = -maxSpawnDistance + r.nextInt(2 * maxSpawnDistance);
        int z = -maxSpawnDistance + r.nextInt(2 * maxSpawnDistance);
        int y = w.getHighestBlockYAt(x, z);
        return new Location(w, x, y, z);
    }

    @Override
    public void spawnChest(Location location) {
        List<Chest> chests = new ArrayList<>();
        location.clone().add(0, 1, 0).getBlock().setType(Material.ENCHANTMENT_TABLE);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (!(x == 0 && z == 0) && ((x % 2 == 0 && z % 2 == 0) || (x % 2 != 0 && z % 2 != 0))) {
                    Location loc = location.clone().add(x, 1, z);
                    loc.getBlock().setType(Material.CHEST);

                    if (loc.getBlock().getType() == Material.CHEST) {
                        Block b = loc.getBlock();
                        if (b.getState() instanceof Chest)
                            chests.add((Chest) loc.getBlock().getState());
                    }
                }
            }
        }

        if (chests.size() <= 0)
            return;

        List<Material> luckyItems = new ArrayList<>();

        luckyItems.add(Material.DIAMOND_HELMET);
        luckyItems.add(Material.DIAMOND_CHESTPLATE);
        luckyItems.add(Material.DIAMOND_LEGGINGS);
        luckyItems.add(Material.DIAMOND_BOOTS);
        luckyItems.add(Material.DIAMOND_AXE);

        Collections.shuffle(chests);

        for (Chest chest : chests) {
            if (luckyItems.isEmpty())
                break;
            ItemStack lucky;
            if (luckyItems.size() == 1)
                lucky = new ItemStack(luckyItems.remove(CommonConst.RANDOM.nextInt(luckyItems.size())));
            else
                lucky = new ItemStack(luckyItems.remove(0));
            int randomSlot;
            int t = 0;
            while (chest.getBlockInventory().getItem(randomSlot = CommonConst.RANDOM.nextInt(chest.getBlockInventory().getSize()))
                    != null) {
                if (++t >= 30)
                    break;
            }
            chest.getBlockInventory().setItem(randomSlot, lucky);
        }

        List<ItemStack> items = generateItems();
        int diamondSpawns = 0;
        Map<ItemStack, AtomicInteger> itemsCount = new HashMap<>();

        while (!items.isEmpty() && !chests.isEmpty()) {
            Chest chest = chests.remove(0);
            Inventory inv = chest.getBlockInventory();
            Set<Integer> random = new HashSet<>();

            int max = CommonConst.RANDOM.ints(7, 12).findFirst().getAsInt();
            for (int i = 0; i <= max; i++) {
                int next = CommonConst.RANDOM.nextInt(inv.getSize());

                int tentatives = 0;
                while (inv.getItem(next) != null || random.contains(next)) {
                    if (++tentatives > 30)
                        break;
                    next = CommonConst.RANDOM.nextInt(inv.getSize());
                }

                if (tentatives <= 30) {
                    random.add(next);
                }
            }

            if (random.isEmpty())
                continue;

            boolean first = true;
            for (Integer slot : random) {
                if (items.isEmpty())
                    break;

                ItemStack next = items.remove(0);
                if (isDiamondOP(next) && ++diamondSpawns > 2)
                    continue;

                if (itemsCount.computeIfAbsent(next, v -> new AtomicInteger()).incrementAndGet()
                        > getMaxAllowed(next.getType()))
                    continue;

                if (first)
                    inv.setItem(slot, next);
                else if (next.getType().getMaxStackSize() > 1 && inv.contains(next))
                    inv.addItem(next);
                else {
                    inv.setItem(slot, next);
                }

                first = false;
            }

            chest.update();
        }
    }

    public boolean isDiamondOP(ItemStack stack) {
        switch (stack.getType()) {
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return true;
            default:
                return false;
        }
    }

    public int getMaxAllowed(Material type) {
        switch (type) {
            case WATER_BUCKET:
            case LAVA_BUCKET:
            case ENDER_PEARL:
            case POTION:
            case GOLDEN_APPLE:
                return 6;
            case TNT:
                return 12;
            case EXP_BOTTLE:
            case WEB:
                return 16;
            case BREAD:
            case ARROW:
                return 10;
            default:
                return 4;
        }
    }

    @Override
    public void spawn(Location central) {
        System.out.println("New Feast: " + central.getBlockX() + " " + central.getBlockY() + " " + central.getBlockZ());
        central.getChunk().load(true);

        Material material = Material.GRASS;
        Biome biome = central.getBlock().getBiome();

        if (biome == Biome.FOREST)
            material = Material.STONE;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Location feastBlock = central.clone().add(x, 0, z);
                if (central.distance(feastBlock) < radius) {
                    feastBlock.getBlock().setType(material);
                    feastBlocks.add(feastBlock.getBlock());
                    feastBlock.getBlock().setMetadata("unbreakableBlock",
                            new FixedMetadataValue(GameMain.getInstance(), true));

                    for (int i = 1; i < 10; i++) {
                        Location airBlock = feastBlock.clone().add(0, i, 0);
                        airBlock.getBlock().setType(Material.AIR);
                        feastBlocks.add(airBlock.getBlock());
                    }
                }
            }
        }
    }

    public List<ItemStack> generateItems() {
        List<ItemStack> feastItems = new ArrayList<>();
        feastItems.addAll(addItem(Material.DIAMOND, 2, 6));
        feastItems.addAll(addItem(Material.DIAMOND_SWORD, 1, 2));
        feastItems.addAll(addItem(Material.DIAMOND_HELMET, 1, 2));
        feastItems.addAll(addItem(Material.DIAMOND_CHESTPLATE, 1, 2));
        feastItems.addAll(addItem(Material.DIAMOND_LEGGINGS, 1, 2));
        feastItems.addAll(addItem(Material.DIAMOND_BOOTS, 1, 2));

        feastItems.addAll(addItem(Material.DIAMOND_PICKAXE, 1, 2));

        feastItems.addAll(addItem(Material.IRON_SWORD, 1, 2));
        feastItems.addAll(addItem(Material.IRON_HELMET, 1, 2));
        feastItems.addAll(addItem(Material.IRON_CHESTPLATE, 1, 2));
        feastItems.addAll(addItem(Material.IRON_LEGGINGS, 1, 2));
        feastItems.addAll(addItem(Material.IRON_BOOTS, 1, 2));

        feastItems.addAll(addItem(Material.BREAD, 4, 24));
        feastItems.addAll(addItem(Material.WATER_BUCKET, 2, 4));
        feastItems.addAll(addItem(Material.LAVA_BUCKET, 2, 4));
        feastItems.addAll(addItem(Material.MUSHROOM_SOUP, 10, 25));
        feastItems.addAll(addItem(Material.WEB, 10, 20));
        feastItems.addAll(addItem(Material.ENDER_PEARL, 5, 15));
        feastItems.addAll(addItem(Material.TNT, 8, 24));
        feastItems.addAll(addItem(Material.ARROW, 12, 36));
        feastItems.addAll(addItem(Material.BOW, 2, 7));
        feastItems.addAll(addItem(Material.EXP_BOTTLE, 7, 12));
        feastItems.addAll(addItem(Material.GOLDEN_APPLE, 6, 12));
        feastItems.addAll(addItem(Material.POTION, (short) 16385, 1, 2));
        feastItems.addAll(addItem(Material.POTION, (short) 16386, 1, 2));
        feastItems.addAll(addItem(Material.POTION, (short) 16387, 1, 2));
        feastItems.addAll(addItem(Material.POTION, (short) 16388, 1, 2));
        feastItems.addAll(addItem(Material.POTION, (short) 16389, 1, 2));
        feastItems.addAll(addItem(Material.POTION, (short) 16394, 1, 2));
        feastItems.addAll(addItem(Material.POTION, (short) 16396, 1, 2));
        Collections.shuffle(feastItems);
        return feastItems;
    }

    private List<ItemStack> addItem(Material mat, int min, int max) {
        return addItem(mat, (short) 0, min, max);
    }

    private List<ItemStack> addItem(Material mat, short durability, int min, int max) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i <= min + CommonConst.RANDOM.nextInt(max - min); i++) {
            items.add(new ItemStack(mat, 1, durability));
        }

        return items;
    }

    public static boolean isFeastBlock(Block block) {
        return feastBlocks.contains(block);
    }

    public List<Item> getItems() {

        List<Item> itemList = new ArrayList<>();

        itemList.add(new Item(Material.DIAMOND_SWORD, (short) 0, 1, 1, 12));
        itemList.add(new Item(Material.DIAMOND_BOOTS, (short) 0, 1, 1, 12));
        itemList.add(new Item(Material.DIAMOND_LEGGINGS, (short) 0, 1, 1, 12));
        itemList.add(new Item(Material.DIAMOND_CHESTPLATE, (short) 0, 1, 1, 12));
        itemList.add(new Item(Material.DIAMOND_HELMET, (short) 0, 1, 1, 12));

        itemList.add(new Item(Material.DIAMOND_HELMET, (short) 0, 1, 1, 12));

        return itemList;

    }

    @Getter
    @AllArgsConstructor
    public class Item {

        private Material material;
        private short data;

        private int min;
        private int max;

        private int chance;

    }

}