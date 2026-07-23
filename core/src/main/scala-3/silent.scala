/*
 * Copyright (C) 2018-2026 Zengularity SA (FaberNovel Technologies) <https://www.zengularity.com>
Copyright (C) 2026 Cédric Chantepie <https://github.com/cchantep>
 */

package com.github.ghik.silencer

class silent(s: String = ".*") extends scala.annotation.nowarn(s"msg=$s")
