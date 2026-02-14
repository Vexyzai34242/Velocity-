package velocity.client.modules.combat;

import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.decoration.EndCrystalEntity;

import velocity.client.modules.Module;
import velocity.client.settings.Setting;

public class FastCrystal extends Module {

    private final Setting<Integer> placeDelay = register(new Setting<>("PlaceDelay", 0, 0, 10));
    private final Setting<Integer> breakDelay = register(new Setting<>("BreakDelay", 0, 0, 10));

    private int placeTimer = 0;
    private int breakTimer = 0;

    public FastCrystal() {
        super("FastCrystal", Category.COMBAT, "Places and breaks crystals faster");
    }

    @Override
    public void onUpdate() {

        if (mc.player == null || mc.world == null) return;

        // Fast Break
        breakTimer++;
        if (breakTimer >= breakDelay.getValue()) {

            mc.world.getEntities().forEach(entity -> {

                if (entity instanceof EndCrystalEntity) {

                    mc.player.networkHandler.sendPacket(
                            PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking())
                    );

                    breakTimer = 0;
                }

            });

        }

        // Fast Place (example using crosshair target)
        placeTimer++;
        if (placeTimer >= placeDelay.getValue()) {

            if (mc.crosshairTarget instanceof BlockHitResult hitResult) {

                mc.player.networkHandler.sendPacket(
                        new PlayerInteractBlockC2SPacket(
                                Hand.MAIN_HAND,
                                hitResult,
                                0
                        )
                );

                placeTimer = 0;
            }
        }
    }
}

