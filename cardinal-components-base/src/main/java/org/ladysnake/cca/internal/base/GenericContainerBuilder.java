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
package org.ladysnake.cca.internal.base;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentContainer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.internal.base.asm.CcaAsmHelper;
import org.ladysnake.cca.internal.base.asm.StaticComponentLoadingException;
import org.ladysnake.cca.internal.base.asm.StaticComponentPluginBase;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class GenericContainerBuilder<I, R> {

    private boolean built;
    private String factoryNameSuffix;
    private final Map<ComponentKey<?>, QualifiedComponentFactory<I>> factories = new LinkedHashMap<>();
    private final List<Class<?>> argClasses;
    private final R emptyFactory;
    private final Class<? super I> componentFactoryClass;
    private final Class<? super R> containerFactoryType;

    public GenericContainerBuilder(Class<? super I> componentFactoryClass, Class<? super R> containerFactoryType, List<Class<?>> argClasses, R emptyFactory) {
        this.argClasses = argClasses;
        this.emptyFactory = emptyFactory;
        this.componentFactoryClass = componentFactoryClass;
        this.containerFactoryType = containerFactoryType;
    }

    @ApiStatus.Experimental
    public void checkDuplicate(ComponentKey<?> key, Function<I, String> msgFactory) {
        if (this.factories.containsKey(key)) {
            throw new StaticComponentLoadingException(msgFactory.apply(this.factories.get(key).factory()));
        }
    }

    public GenericContainerBuilder<I, R> factoryNameSuffix(String factoryNameSuffix) {
        Preconditions.checkState(this.factoryNameSuffix == null);

        this.factoryNameSuffix = factoryNameSuffix;

        return this;
    }

    @Contract(mutates = "this")
    public <C extends Component> GenericContainerBuilder<I, R> component(ComponentKey<? super C> key, Class<C> implClass, I factory, Set<ComponentKey<?>> dependencies) {
        this.addComponent(key, new QualifiedComponentFactory<>(factory, implClass, dependencies));
        return this;
    }

    protected <C extends Component> void addComponent(ComponentKey<? super C> key, QualifiedComponentFactory<I> value) {
        this.factories.put(key, value);
    }

    public R build() {
        if (this.built) {
            throw new IllegalStateException("Cannot build more than one container factory with the same builder");
        }

        try {
            this.built = true;

            if (this.factories.isEmpty()) {
                return this.emptyFactory;
            }

            Class<? extends ComponentContainer> containerClass = CcaAsmHelper.spinComponentContainer(
                this.componentFactoryClass, this.factories
            );
            Class<? extends R> factoryClass = StaticComponentPluginBase.spinContainerFactory(
                this.containerFactoryType, containerClass, this.argClasses
            );
            return ComponentsInternals.createFactory(factoryClass);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
