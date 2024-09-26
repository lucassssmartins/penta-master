package br.com.pentamc.gladiator.menu.custom;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.utils.json.JsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.bukkit.api.item.ItemBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Custom {

    public static ItemStack[] createContents() {
        ItemStack[] contents = new ItemStack[36];

        contents[0] = new ItemBuilder().name("§aEspada de Diamante!").type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL).build();
        contents[1] = new ItemStack(Material.COBBLE_WALL, 64);
        contents[2] = new ItemStack(Material.LAVA_BUCKET);
        contents[3] = new ItemStack(Material.WATER_BUCKET);
        contents[8] = new ItemStack(Material.WOOD, 64);

        contents[27] = new ItemStack(Material.LAVA_BUCKET);
        contents[28] = new ItemStack(Material.LAVA_BUCKET);

        contents[17] = new ItemStack(Material.STONE_AXE);
        contents[26] = new ItemStack(Material.STONE_PICKAXE);

        contents[13] = new ItemStack(Material.BOWL, 64);
        contents[14] = new ItemStack(Material.INK_SACK, 64, (short) 3);
        contents[15] = new ItemStack(Material.INK_SACK, 64, (short) 3);
        contents[16] = new ItemStack(Material.INK_SACK, 64, (short) 3);

        contents[22] = new ItemStack(Material.BOWL, 64);
        contents[23] = new ItemStack(Material.INK_SACK, 64, (short) 3);
        contents[24] = new ItemStack(Material.INK_SACK, 64, (short) 3);
        contents[25] = new ItemStack(Material.INK_SACK, 64, (short) 3);

        contents[9] = new ItemStack(Material.IRON_HELMET);
        contents[10] = new ItemStack(Material.IRON_CHESTPLATE);
        contents[11] = new ItemStack(Material.IRON_LEGGINGS);
        contents[12] = new ItemStack(Material.IRON_BOOTS);

        contents[18] = new ItemStack(Material.IRON_HELMET);
        contents[19] = new ItemStack(Material.IRON_CHESTPLATE);
        contents[20] = new ItemStack(Material.IRON_LEGGINGS);
        contents[21] = new ItemStack(Material.IRON_BOOTS);

        return contents;
    }

    public static JsonObject translate(ItemStack[] contents) {
        JsonObject jsonObject = new JsonObject();

        for (int i = 0; i < contents.length; i++) {
            jsonObject.add(Integer.toString(i), toJson(contents[i]));
        }

        return jsonObject;
    }

    public static JsonObject toJson(ItemStack itemStack) {
        if (itemStack == null) {
            return new JsonBuilder().addProperty("type", "AIR").addProperty("amount", 1)
                    .addProperty("durability", 0)
                    .add("enchantments", new JsonObject())
                    .build();
        }

        JsonObject enchantments = new JsonObject();

        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
            enchantments.addProperty(Integer.toString(entry.getKey().getId()), entry.getValue());
        }

        return new JsonBuilder()
                .addProperty("type", itemStack.getType().name())
                .addProperty("amount", itemStack.getAmount())
                .addProperty("durability", itemStack.getDurability())
                .add("enchantments", enchantments)
                .build();
    }

    public static ItemStack fromJson(JsonObject jsonObject) {
        Material material = jsonObject.has("type") ? Material.valueOf(jsonObject.get("type").getAsString()) : Material.AIR;
        int amount = jsonObject.has("amount") ? jsonObject.get("amount").getAsInt() : 0;
        short durability = jsonObject.has("durability") ? jsonObject.get("durability").getAsShort() : 0;
        JsonObject enchantments = jsonObject.getAsJsonObject("enchantments");
        Map<Enchantment, Integer> enchantmentMap = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : enchantments.entrySet()) {
            enchantmentMap.put(Enchantment.getById(Integer.parseInt(entry.getKey())), entry.getValue().getAsInt());
        }

        return new ItemBuilder().type(material).amount(amount).durability(durability).enchantment(enchantmentMap).build();
    }

    public static ItemStack[] translate(JsonObject jsonObject) {
        ItemStack[] itemStacks = new ItemStack[jsonObject.size()];

        for (int i = 0; i < jsonObject.size(); i++) {
            itemStacks[i] = fromJson(jsonObject.getAsJsonObject(Integer.toString(i)));
        }

        return itemStacks;
    }

    public static ItemStack[] getContents(UUID playerId) {
        Member member = CommonGeneral.getInstance().getMemberManager().getMember(playerId);

        if (member == null) {
            return createContents();
        }

        if (member.getGladiatorInventory() == null) {
            ItemStack[] contents = createContents();
            member.setGladiatorInventory(translate(contents));
            return contents;
        }

        return translate(member.getGladiatorInventory());
    }
}
