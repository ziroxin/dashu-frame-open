/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.kg.component.generator.query;

import com.kg.component.generator.config.po.TableInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author nieqiurong 2021/1/6.
 * @since 3.5.0
 */
public interface IDatabaseQuery {

    /**
     * 获取表信息
     *
     * @return 表信息
     */
    @NotNull
    List<TableInfo> queryTables();

}
