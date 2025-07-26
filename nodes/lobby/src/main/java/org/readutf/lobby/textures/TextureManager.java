package org.readutf.lobby.textures;

import org.readutf.ui.container.MenuOverlay;
import team.unnamed.creative.base.Writable;

public class TextureManager {

    private static MenuOverlay gameSelector = MenuOverlay.chest(
            Writable.resource(TextureManager.class.getClassLoader(), "ui/game-selector.png")
    );

}
