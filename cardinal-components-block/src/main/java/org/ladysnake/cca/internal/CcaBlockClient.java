/*
 * Cardinal-Components-API
 * Copyright (C) 2019-2024 Ladysnake
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ladysnake.cca.internal;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.ComponentProvider;
import org.ladysnake.cca.internal.base.CcaClientInternals;
import qouteall.imm_ptl.core.ClientWorldLoader;

import java.util.Optional;

public class CcaBlockClient {
    private static boolean hasImmersivePortals = false;

    public static void initClient() {
        hasImmersivePortals = FabricLoader.getInstance().isModLoaded("immersive_portals");

        if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
            CcaClientInternals.registerComponentSync(CardinalComponentsBlock.PACKET_ID,
                (payload, ctx) -> payload.componentKey().flatMap(key -> {
                    World world;
                    if (hasImmersivePortals) {
                        world = ClientWorldLoader.getOptionalWorld(payload.targetData().worldKey());
                        if (world == null) {
                            return Optional.empty();
                        }
                    } else {
                        world = ctx.client().world;
                    }

                    return key.maybeGet(payload.targetData().beType().get(
                        world,
                        payload.targetData().bePos()
                    ));
                }
            ));
        }
        if (FabricLoader.getInstance().isModLoaded("fabric-lifecycle-events-v1")) {
            ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((be, world) -> ((ComponentProvider) be).getComponentContainer().onServerLoad());
            ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((be, world) -> ((ComponentProvider) be).getComponentContainer().onServerUnload());
        }
    }
}
