package com.envyful.acaf.api.injector;

public interface ArgumentInjector {

    Class<?> getSuperClass();

    Object instantiateClass();

}
