package com.gmail.yeshjho2.testplugin.recipes;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static com.gmail.yeshjho2.testplugin.Constants.*;

public class RecipeAdder
{
    private static final HashSet<Material> REMOVE_RECIPES_OF = new HashSet<>(Arrays.asList(
            Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE,
            Material.DIAMOND_HOE, Material.NETHERITE_HOE
    ));

    private JavaPlugin plugin;

    private ItemStack makeItem(Material material, String name, int customModelData, boolean doEnchant, boolean makeUnrepairable)
    {
        final ItemStack item = new ItemStack(material);
        //???? Order matters
        if (doEnchant)
        {
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        }

        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(name);
        itemMeta.setCustomModelData(customModelData);
        if (doEnchant)
        {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (makeUnrepairable)
        {
            ((Repairable) itemMeta).setRepairCost(Integer.MAX_VALUE);
        }

        item.setItemMeta(itemMeta);

        return item;
    }

    public RecipeAdder(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    public void AddRecipes()
    {
        final ArrayList<Recipe> recipes = new ArrayList<>();


        //Purified Water
        final ItemStack purifiedWaterItem = makeItem(Material.HONEY_BOTTLE, "Purified Water", PURIFIED_WATER_CUSTOM_MODEL_DATA, true, false);

        final ItemStack purifierItem = makeItem(Material.GLOW_INK_SAC, "Water Purifier", PURIFIER_CUSTOM_MODEL_DATA, false, false);

        final ShapedRecipe purifierRecipe = new ShapedRecipe(new NamespacedKey(plugin, "purifier"), purifierItem);
        {
            purifierRecipe.shape("ABC", "DED", "DFD");
            purifierRecipe.setIngredient('A', Material.GRAVEL);
            purifierRecipe.setIngredient('B', new RecipeChoice.MaterialChoice(Tag.SAND));
            purifierRecipe.setIngredient('C', Material.CHARCOAL);
            purifierRecipe.setIngredient('D', new RecipeChoice.MaterialChoice(Tag.IMPERMEABLE));
            purifierRecipe.setIngredient('E', Material.QUARTZ);
            purifierRecipe.setIngredient('F', Material.PAPER);
        }
        recipes.add(purifierRecipe);

        final ShapelessRecipe purifiedWaterRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "purified_water"), purifiedWaterItem);
        {
            purifiedWaterRecipe.addIngredient(Material.POTION);
            purifiedWaterRecipe.addIngredient(new RecipeChoice.ExactChoice(purifierItem));
        }
        recipes.add(purifiedWaterRecipe);


        // Hazmat Suit
        final ItemStack antiAcidGoldItem = makeItem(Material.GOLD_INGOT, "Anti Acid Gold", ANTI_ACID_CUSTOM_MODEL_DATA, true, false);
        final ItemStack antiAcidDiamondItem = makeItem(Material.DIAMOND, "Anti Acid Diamond", ANTI_ACID_CUSTOM_MODEL_DATA, true, false);
        final ItemStack antiAcidEmeraldItem = makeItem(Material.EMERALD, "Anti Acid Emerald", ANTI_ACID_CUSTOM_MODEL_DATA, true, false);

        final ArrayList<Material> antiAcidOreMaterials = new ArrayList<>(Arrays.asList(
                Material.GOLD_INGOT, Material.DIAMOND, Material.EMERALD
        ));
        final ArrayList<ItemStack> antiAcidOreItems = new ArrayList<>(Arrays.asList(
                antiAcidGoldItem, antiAcidDiamondItem, antiAcidEmeraldItem
        ));

        for (int i = 0; i < 3; i++)
        {
            final ItemStack antiAcidOreItem = antiAcidOreItems.get(i);
            final Material antiAcidOreMaterial = antiAcidOreMaterials.get(i);
            for (int j = 1; j <= 3; j++)
            {
                final ItemStack antiAcidOreItemWithCount = antiAcidOreItem.clone();
                antiAcidOreItemWithCount.setAmount(j);
                final ShapelessRecipe antiAcidOreRecipe = new ShapelessRecipe(new NamespacedKey(plugin, ("anti_acid_ore_" + i) + j), antiAcidOreItemWithCount);
                {
                    antiAcidOreRecipe.addIngredient(Material.AMETHYST_SHARD);
                    antiAcidOreRecipe.addIngredient(j, antiAcidOreMaterial);
                }
                recipes.add(antiAcidOreRecipe);
            }
        }


        final ArrayList<ArrayList<Material>> hazmatSuitMaterials = new ArrayList<>();
        hazmatSuitMaterials.add(new ArrayList<>(Arrays.asList(Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS)));
        hazmatSuitMaterials.add(new ArrayList<>(Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS)));
        hazmatSuitMaterials.add(new ArrayList<>(Arrays.asList(Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS)));

        final ArrayList<ArrayList<String>> hazmatSuitRecipeShapes = new ArrayList<>();
        hazmatSuitRecipeShapes.add(new ArrayList<>(Arrays.asList("XXX", "X X")));
        hazmatSuitRecipeShapes.add(new ArrayList<>(Arrays.asList("X X", "XXX", "XXX")));
        hazmatSuitRecipeShapes.add(new ArrayList<>(Arrays.asList("XXX", "X X", "X X")));
        hazmatSuitRecipeShapes.add(new ArrayList<>(Arrays.asList("X X", "X X")));

        for (int i = 0; i < 3; i++)
        {
            final ItemStack antiAcidOreItem = antiAcidOreItems.get(i);
            final int customModelData = 1 << (i + 4);
            for (int j = 0; j < 4; j++)
            {
                final ArrayList<String> recipeShape = hazmatSuitRecipeShapes.get(j);
                final Material material = hazmatSuitMaterials.get(i).get(j);
                final ItemStack hazmatSuitItem = makeItem(material, "Hazmat Suit - Tier " + (i + 1),
                        HAZMAT_SUIT_CUSTOM_MODEL_DATA | customModelData, true, true);
                final ShapedRecipe hazmatSuitRecipe = new ShapedRecipe(new NamespacedKey(plugin, ("hazmat_suit_" + i) + j), hazmatSuitItem);
                {
                    switch (recipeShape.size())
                    {
                        case 3 -> hazmatSuitRecipe.shape(recipeShape.get(0), recipeShape.get(1), recipeShape.get(2));
                        case 2 -> hazmatSuitRecipe.shape(recipeShape.get(0), recipeShape.get(1));
                        case 1 -> hazmatSuitRecipe.shape(recipeShape.get(0));
                    }
                    hazmatSuitRecipe.setIngredient('X', new RecipeChoice.ExactChoice(antiAcidOreItem));
                }
                recipes.add(hazmatSuitRecipe);
            }
        }


        //Turtle Shell
        final ItemStack turtleHoeItem = makeItem(Material.WOODEN_HOE, "Turtle Shell Hoe", TURTLE_HOE_CUSTOM_MODEL_DATA, false, true);

        final ShapedRecipe turtleHoeRecipe = new ShapedRecipe(new NamespacedKey(plugin, "turtle_hoe"), turtleHoeItem);
        {
            turtleHoeRecipe.shape("XX", " #", " #");
            turtleHoeRecipe.setIngredient('X', Material.SCUTE);
            turtleHoeRecipe.setIngredient('#', Material.STICK);
        }
        recipes.add(turtleHoeRecipe);


        final Server server = plugin.getServer();
        {
            final ArrayList<NamespacedKey> toRemoves = new ArrayList<>();

            Iterator<Recipe> it = server.recipeIterator();
            while (it.hasNext())
            {
                final Recipe recipe = it.next();
                if (recipe != null && REMOVE_RECIPES_OF.contains(recipe.getResult().getType()))
                {
                    toRemoves.add(((Keyed) recipe).getKey());
                }
            }

            for (NamespacedKey key : toRemoves)
            {
                server.removeRecipe(key);
            }
        }
        {
            for (Recipe recipe : recipes)
            {
                if (!server.addRecipe(recipe))
                {
                    System.out.println(recipe);
                }
            }
        }
    }
}
