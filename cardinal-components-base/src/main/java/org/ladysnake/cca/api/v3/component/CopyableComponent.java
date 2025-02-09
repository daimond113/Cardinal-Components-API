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
package org.ladysnake.cca.api.v3.component;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

/**
 * A component that can copy its data from another component of the same type.
 *
 * @param <C> the type of components that this component may copy
 * @since 2.3.0
 */
public interface CopyableComponent<C extends Component> extends Component {
    /**
     * Copies the data from {@code other} into {@code this}.
     *
     * @implSpec The default implementation {@linkplain Component#writeToNbt(NbtCompound, RegistryWrapper.WrapperLookup) serializes}
     * the component data to a {@link NbtCompound} and calls {@link Component#readFromNbt(NbtCompound, RegistryWrapper.WrapperLookup)}.
     * @implNote The default implementation should generally be overridden.
     * The serialization done by the default implementation assumes NBT consistency
     * between implementations, and is generally slower than a direct copy.
     * Implementing classes can nearly always provide a better implementation.
     */
    void copyFrom(C other, RegistryWrapper.WrapperLookup registryLookup);
}
