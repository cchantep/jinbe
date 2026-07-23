/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package io.github.cchantep.jinbe.spi;

/**
 * Dependency injection container.
 */
public interface Injector {
    public <T> T instanceOf(Class<T> cls);
}
