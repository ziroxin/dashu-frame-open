-- ----------------------------
-- 生成菜单信息
-- ----------------------------
INSERT INTO `z_permission` VALUES ('${menuId}', '-1', '${permissionName}', '${table.comment!}', '0', '${table.comment!}', '', '${permissionRouter}', '${permissionCompontent}', NULL, 1, 1, 'noRedirect', 0, 1, 0, NULL, 100, '${.now}', '${.now}');

-- ----------------------------
-- 生成页面按钮信息（添加、修改、删除）
-- ----------------------------
INSERT INTO `z_permission` VALUES ('${addBtnId}', '${menuId}', '${permissionName}-add', '${table.comment!}-添加按钮', '1', '添加按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '${.now}', '${.now}');
INSERT INTO `z_permission` VALUES ('${updateBtnId}', '${menuId}', '${permissionName}-update', '${table.comment!}-修改按钮', '1', '修改按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '${.now}', '${.now}');
INSERT INTO `z_permission` VALUES ('${deleteBtnId}', '${menuId}', '${permissionName}-delete', '${table.comment!}-删除按钮', '1', '删除按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '${.now}', '${.now}');
INSERT INTO `z_permission` VALUES ('${exportExcelBtnId}', '${menuId}', '${permissionName}-exportExcel', '${table.comment!}-导出Excel按钮', '1', '导出Excel按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '${.now}', '${.now}');

-- ----------------------------
-- 生成接口信息
-- ----------------------------
INSERT INTO `z_api` VALUES ('${addApiId}', NULL, '新增-${table.comment!}', '${controllerAuthorizePre}add', '${controllerMapping}/add', 'POST', '新增-${table.comment!}', '${table.controllerName}', 'add', 100, '${.now}', '${.now}');
INSERT INTO `z_api` VALUES ('${updateApiId}', NULL, '修改-${table.comment!}', '${controllerAuthorizePre}update', '${controllerMapping}/update', 'POST', '修改-${table.comment!}', '${table.controllerName}', 'update', 100, '${.now}', '${.now}');
INSERT INTO `z_api` VALUES ('${deleteApiId}', NULL, '删除-${table.comment!}', '${controllerAuthorizePre}delete', '${controllerMapping}/delete', 'POST', '删除-${table.comment!}', '${table.controllerName}', 'delete', 100, '${.now}', '${.now}');
INSERT INTO `z_api` VALUES ('${exportExcelApiId}', NULL, '导出Excel-${table.comment!}', '${controllerAuthorizePre}export:excel', '${controllerMapping}/export/excel', 'GET', '导出Excel-${table.comment!}', '${table.controllerName}', 'exportExcel', 100, '${.now}', '${.now}');
INSERT INTO `z_api` VALUES ('${listApiId}', NULL, '分页列表-${table.comment!}', '${controllerAuthorizePre}list', '${controllerMapping}/list', 'GET', '分页列表-${table.comment!}', '${table.controllerName}', 'list', 100, '${.now}', '${.now}');
INSERT INTO `z_api` VALUES ('${getByIdApiId}', NULL, '详情-${table.comment!}', '${controllerAuthorizePre}getById', '${controllerMapping}/getById', 'GET', '详情-${table.comment!}', '${table.controllerName}', 'getById', 100, '${.now}', '${.now}');

-- ----------------------------
-- 生成菜单/按钮，关联接口
-- ----------------------------
INSERT INTO `z_permission_api` VALUES ('${addBtnId}', '${addApiId}');
INSERT INTO `z_permission_api` VALUES ('${updateBtnId}', '${updateApiId}');
INSERT INTO `z_permission_api` VALUES ('${deleteBtnId}', '${deleteApiId}');
INSERT INTO `z_permission_api` VALUES ('${exportExcelBtnId}', '${exportExcelApiId}');
INSERT INTO `z_permission_api` VALUES ('${menuId}', '${listApiId}');
INSERT INTO `z_permission_api` VALUES ('${menuId}', '${getByIdApiId}');
