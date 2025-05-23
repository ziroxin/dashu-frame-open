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
package com.kg.component.generator.config;

/**
 * 输出文件类型
 *
 * @author hubin
 * @since 2021-06-01
 */
public enum OutputFile {
    entity,
    dto,
    convert,
    excelConstant,
    excelOut,
    excelImport,
    service,
    serviceImpl,
    mapper,
    xml,
    controller,
    indexVue,
    permissionSql,
    /**
     * 已弃用，已重构自定义文件生成，3.5.4版本会删除
     */
    @Deprecated
    other,
    parent;
}
