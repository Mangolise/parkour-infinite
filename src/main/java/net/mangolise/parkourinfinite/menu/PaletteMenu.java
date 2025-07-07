package net.mangolise.parkourinfinite.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.mangolise.gamesdk.util.InventoryMenu;
import net.mangolise.parkourinfinite.ParkourInfPlayer;
import net.mangolise.parkourinfinite.palette.Palette;
import net.mangolise.parkourinfinite.palette.Palettes;
import net.minestom.server.inventory.InventoryType;

import java.util.List;

public class PaletteMenu extends InventoryMenu {
    public static final PaletteMenu MENU = new PaletteMenu();

    public PaletteMenu() {
        super(InventoryType.CHEST_1_ROW, Component.text("Choose a Palette").color(TextColor.color(0x119677)));

        List<Palette> palettes = Palettes.allPalettes();
        for (int i = 0; i < palettes.size(); i++) {
            Palette palette = palettes.get(i);

            setMenuItem(i + 9 / 2 - palettes.size() / 2, palette.getIcon()).onLeftClick(e -> {
                e.player().getTag(ParkourInfPlayer.PLAYER_INF_TAG).setPalette(palette);
                e.player().closeInventory();
            });
        }
    }
}
