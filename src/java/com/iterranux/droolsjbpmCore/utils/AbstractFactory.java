package com.iterranux.droolsjbpmCore.utils;

import java.util.*;

/**
 * Abstract Generic Factory that can be easily extended with new manufacturables.
 * The extending Factory just needs to provide a List of fully qualified ClassNames
 * by implementing the getManufacturableClassNames() method.
 *
 * @param <T>
 */
public abstract class AbstractFactory<T> {

    private final Map<String, Class<? extends T>> manufacturableClasses;

    public AbstractFactory() {

        this.manufacturableClasses = new HashMap<String, Class<? extends T>>();

        try {
            loadClasses();
        } catch (ClassNotFoundException classNotFoundException) {
            throw new IllegalStateException(
                    "Could not find manufacturables class for the supplied className.",
                    classNotFoundException);
        } catch (NullPointerException nullPointerException) {
            throw new IllegalStateException(
                    "There were no classes of manufacturables supplied.",
                    nullPointerException);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadClasses() throws ClassNotFoundException {

        Collection<String> classNames = getManufacturableClassNames();

        for (String className : classNames){
            manufacturableClasses.put(
                    className,
                    (Class<? extends T>) Class.forName(className, true, Thread.currentThread().getContextClassLoader()));

        }
    }

    public Set<String> getLoadedManufacturableClassNames() {
        return manufacturableClasses.keySet();
    }

    public T getInstance(String manufacturableName)
            throws InstantiationException, IllegalAccessException {

        return manufacturableClasses.get(manufacturableName).newInstance();
    }

    /**
     * Abstract method that needs to be implemented by the factory to discover
     * the classes of the manufacturables.
     *
     * @return Collection of manufacturables classNames
     */
    abstract protected Collection<String> getManufacturableClassNames();
}

