package xt9.deepmoblearningbm;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xt9.deepmoblearningbm.util.MathHelper;
import net.minecraftforge.common.config.*;

/**
 * Created by xt9 on 2018-06-30.
 */
@SuppressWarnings("WeakerAccess")
@Mod.EventBusSubscriber
@Config(modid = ModConstants.MODID)
public class ModConfig {
    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ModConstants.MODID)) {
            ConfigManager.sync(ModConstants.MODID, Config.Type.INSTANCE);
        }
    }

    @Config.Comment("Change Essence Multiplier Settings")
    @Config.Name("Essence Multiplier Settings")
    public static CatalystMultiplierSubCategory essenceMultiplierSubCat = new CatalystMultiplierSubCategory();

    @Config.Comment("Change cost of essence.")
    @Config.Name("Data Model Essence Costs")
    public static EssenceBaseAmountsSubCategory essenceAmountSubCat = new EssenceBaseAmountsSubCategory();

    @Config.Comment({
        "Min: 1",
        "Max: 2,147,483,647"
    })
    @Config.Name("RF tick cost of the Digital Agonizer")//2147483647
    public static int agonizerRFCost = 128;
    public static int getAgonizerRFCost() {
        return MathHelper.ensureRange(agonizerRFCost, 1, 2147483647);
    }

    @Config.Comment({
            "Min: 1",
            "Max: 2,147,483,647"
    })
    @Config.Name("Digital Agonizer RF Storage Size")//2147483647
    public static int agonizerRFStorage = 100000;
    public static int getagonizerRFStorage() {
        return MathHelper.ensureRange(agonizerRFStorage, 1, 2147483647);
    }

    @Config.Comment({
            "Min: 1",
            "Max: 2,147,483,647"
    })
    @Config.Name("Digital Agonizer Input RF a Tick")//2147483647
    public static int agonizerRFInput = 25600;
    public static int getagonizerRFInput() {
        return MathHelper.ensureRange(agonizerRFInput, 1, 2147483647);
    }

    @Config.Name("Allow multiple agonizers linked with one Altar")
    public static boolean isMultipleAgonizersAllowed = false;

    public static class EssenceBaseAmountsSubCategory {
        @Config.Comment({"Default: 50"})
        @Config.Name("Tier 1 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 10000)
        public int tierOneEssenceAmount = 50;

        @Config.Comment({"Default: 75"})
        @Config.Name("Tier 2 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 20000)
        public int tierTwoEssenceAmount = 75;

        @Config.Comment({"Default: 150"})
        @Config.Name("Tier 3 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 30000)
        public int tierThreeEssenceAmount = 150;

        @Config.Comment({"Default: 300"})
        @Config.Name("Tier 4 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 40000)
        public int tierFourEssenceAmount = 300;

        @Config.Comment({"Default: 400"})
        @Config.Name("Tier 5 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 50000)
        public int tierFiveEssenceAmount = 600;

        @Config.Comment({"Default: 500"})
        @Config.Name("Tier 6 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 60000)
        public int tierSixEssenceAmount = 780;

        @Config.Comment({"Default: 600"})
        @Config.Name("Tier 7 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 70000)
        public int tierSevenEssenceAmount = 890;

        @Config.Comment({"Default: 700"})
        @Config.Name("Tier 8 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 80000)
        public int tierEightEssenceAmount = 1010;

        @Config.Comment({"Default: 800"})
        @Config.Name("Tier 9 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 90000)
        public int tierNineEssenceAmount = 1210;

        @Config.Comment({"Default: 900"})
        @Config.Name("Tier 10 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 100000)
        public int tierTenEssenceAmount = 1420;

        @Config.Comment({"Default: 1,000"})
        @Config.Name("Tier 11 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 200000)
        public int tierElevenEssenceAmount = 1580;

        @Config.Comment({"Default: 1,100"})
        @Config.Name("Tier 12 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 300000)
        public int tierTwelveEssenceAmount = 1750;

        @Config.Comment({"Default: 1,200"})
        @Config.Name("Tier 13 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 400000)
        public int tierThirteenEssenceAmount = 1900;

        @Config.Comment({"Default: 1,300"})
        @Config.Name("Tier 14 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 500000)
        public int tierFourteenEssenceAmount = 2150;

        @Config.Comment({"Default: 1,400"})
        @Config.Name("Tier 15 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 600000)
        public int tierFifteenEssenceAmount = 2450;

        @Config.Comment({"Default: 1,500"})
        @Config.Name("Tier 16 Data Model - Essence per operation")
        @Config.RangeInt(min = 1, max = 700000)
        public int tierSixteenEssenceAmount = 3000;

        public int getTierEssenceAmount(int tier) {
            switch (tier) {
                case 1: return MathHelper.ensureRange(tierOneEssenceAmount, 1, 10000);
                case 2: return MathHelper.ensureRange(tierTwoEssenceAmount, 1, 20000);
                case 3: return MathHelper.ensureRange(tierThreeEssenceAmount, 1, 30000);
                case 4: return MathHelper.ensureRange(tierFourEssenceAmount, 1, 40000);
                case 5: return MathHelper.ensureRange(tierFiveEssenceAmount, 1, 50000);
                case 6: return MathHelper.ensureRange(tierSixEssenceAmount, 1, 60000);
                case 7: return MathHelper.ensureRange(tierSevenEssenceAmount, 1, 70000);
                case 8: return MathHelper.ensureRange(tierEightEssenceAmount, 1, 80000);
                case 9: return MathHelper.ensureRange(tierNineEssenceAmount, 1, 90000);
                case 10: return MathHelper.ensureRange(tierTenEssenceAmount, 1, 100000);
                case 11: return MathHelper.ensureRange(tierElevenEssenceAmount, 1, 200000);
                case 12: return MathHelper.ensureRange(tierTwelveEssenceAmount, 1, 300000);
                case 13: return MathHelper.ensureRange(tierThirteenEssenceAmount, 1, 400000);
                case 14: return MathHelper.ensureRange(tierFourteenEssenceAmount, 1, 500000);
                case 15: return MathHelper.ensureRange(tierFifteenEssenceAmount, 1, 600000);
                case 16: return MathHelper.ensureRange(tierSixteenEssenceAmount, 1, 700000);
                default: return 1;
            }
        }
    }

    public static class CatalystMultiplierSubCategory {
        @Config.Comment({"Default: 5.0"})
        @Config.Name("#1 Multiplier of Corrupted Glitch Heart")
        @Config.RangeDouble(min = 1.0, max = 10.0)
        public double heartCatalystMultiplier = 5.0;
        public double getHeartCatalystMultiplier() {
            return MathHelper.ensureRange(heartCatalystMultiplier, 1.0, 10.0);
        }

        @Config.Comment({"Default: 2.2"})
        @Config.Name("#2 Multiplier of Overworldian Living matter")
        @Config.RangeDouble(min = 1.0, max = 10.0)
        public double overworldianCatalystMultiplier = 2.2;
        public double getOverworldianCatalystMultiplier() {
            return MathHelper.ensureRange(overworldianCatalystMultiplier, 1.0, 10.0);
        }

        @Config.Comment({"Default: 2.4"})
        @Config.Name("#3 Multiplier of Hellish Living matter")
        @Config.RangeDouble(min = 1.0, max = 10.0)
        public double hellishCatalystMultiplier = 2.4;
        public double getHellishCatalystMultiplier() {
            return MathHelper.ensureRange(hellishCatalystMultiplier, 1.0, 10.0);
        }

        @Config.Comment({"Default: 2.7"})
        @Config.Name("#4 Multiplier of Extraterrestrial Living matter")
        @Config.RangeDouble(min = 1.0, max = 10.0)
        public double extraterrestrialCatalystMultiplier = 2.7;
        public double getExtraterrestrialCatalystMultiplier() {
            return MathHelper.ensureRange(extraterrestrialCatalystMultiplier, 1.0, 10.0);
        }

        @Config.Comment({"Default: 2.5"})
        @Config.Name("#5 Multiplier of Twilight Living matter")
        @Config.RangeDouble(min = 1.0, max = 10.0)
        public double twilightCatalystMultiplier = 2.5;
        public double getTwilightCatalystMultiplier() {
            return MathHelper.ensureRange(twilightCatalystMultiplier, 1.0, 10.0);
        }
    }


}
