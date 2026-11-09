package dev.gogeta.ascension.form;

import net.minecraft.ChatFormatting;

public enum Form {
    NONE("none", "None", 1.0f, 0, 0, false, 0x000000, ChatFormatting.GRAY),
    BASE("base", "Base Gogeta", 1.0f, 1, 1, false, 0x111111, ChatFormatting.WHITE),
    SSJ("ssj", "Super Saiyan", 3.0f, 2, 2, true, 0xFFDD00, ChatFormatting.YELLOW),
    SSJ2("ssj2", "Super Saiyan 2", 7.0f, 3, 3, true, 0xFFEE33, ChatFormatting.GOLD),
    BLUE("blue", "Super Saiyan Blue", 20.0f, 4, 4, true, 0x00CCFF, ChatFormatting.AQUA),
    SS4("ss4", "Super Saiyan 4", 30.0f, 5, 5, true, 0xCC1100, ChatFormatting.RED),
    ULTRA("ultra", "Ultra Instinct", 75.0f, 6, 6, true, 0xE0E8FF, ChatFormatting.LIGHT_PURPLE);

    private final String id;
    private final String displayName;
    private final float damageMultiplier;
    private final int strengthLevel;
    private final int speedLevel;
    private final boolean canFly;
    private final int auraColor;
    private final ChatFormatting chatColor;

    Form(String id, String displayName, float damageMultiplier, int strengthLevel, int speedLevel, boolean canFly, int auraColor, ChatFormatting chatColor) {
        this.id = id;
        this.displayName = displayName;
        this.damageMultiplier = damageMultiplier;
        this.strengthLevel = strengthLevel;
        this.speedLevel = speedLevel;
        this.canFly = canFly;
        this.auraColor = auraColor;
        this.chatColor = chatColor;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public int getStrengthLevel() {
        return strengthLevel;
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    public boolean canFly() {
        return canFly;
    }

    public int getAuraColor() {
        return auraColor;
    }

    public ChatFormatting getChatColor() {
        return chatColor;
    }

    public static Form fromId(String id) {
        if (id == null) return NONE;
        for (Form form : values()) {
            if (form.id.equalsIgnoreCase(id)) {
                return form;
            }
        }
        return NONE;
    }
}
