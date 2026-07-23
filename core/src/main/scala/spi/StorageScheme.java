/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe.spi;

/**
 * Each implementation must be registered in a 
 * `META-INF/services/io.github.cchantep.jinbe.spi.StorageScheme` resource.
 */
public interface StorageScheme {
    /** 
     * The storage scheme (e.g. `s3`)
     */
    public String scheme();

    /**
     * The class of the provider that can be resolved 
     * using a dependency aware context.
     */
    public Class<? extends StorageFactory> factoryClass();
}
