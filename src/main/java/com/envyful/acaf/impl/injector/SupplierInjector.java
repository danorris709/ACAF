package com.envyful.acaf.impl.injector;

import com.envyful.acaf.api.injector.ArgumentInjector;

import java.util.function.Supplier;

public class SupplierInjector implements ArgumentInjector {

    private final Class<?> superClass;
    private final Supplier<?> supplier;

    public SupplierInjector(Class<?> superClass, Supplier<?> supplier) {
        this.superClass = superClass;
        this.supplier = supplier;
    }

    @Override
    public Class<?> getSuperClass() {
        return this.superClass;
    }

    @Override
    public Object instantiateClass() {
        return this.supplier.get();
    }
}
