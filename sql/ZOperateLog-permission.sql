-- ----------------------------
-- 生成菜单信息
-- ----------------------------
INSERT INTO `z_permission` VALUES ('b6962d4c-910c-4641-b638-129439f97726', '-1', 'zlog-zOperateLog', '操作日志表', '0', '操作日志表', '', '/system/log', '/system/log/index', NULL, '1', '1', 100, '2023-1-7 17:27:20', '2023-1-7 17:27:20');

-- ----------------------------
-- 生成页面按钮信息（添加、修改、删除）
-- ----------------------------
INSERT INTO `z_permission` VALUES ('c792c99a-c3f2-4390-9cea-78c35853c532', 'b6962d4c-910c-4641-b638-129439f97726', 'zlog-zOperateLog-add', '操作日志表-添加按钮', '1', '添加按钮', '', '', '', NULL, '1', '1', 1, '2023-1-7 17:27:20', '2023-1-7 17:27:20');
INSERT INTO `z_permission` VALUES ('503fc98e-16e7-48d0-ad6a-331456a62a25', 'b6962d4c-910c-4641-b638-129439f97726', 'zlog-zOperateLog-update', '操作日志表-修改按钮', '1', '修改按钮', '', '', '', NULL, '1', '1', 1, '2023-1-7 17:27:20', '2023-1-7 17:27:20');
INSERT INTO `z_permission` VALUES ('5f329aae-4c2a-425f-a957-70149f18f104', 'b6962d4c-910c-4641-b638-129439f97726', 'zlog-zOperateLog-delete', '操作日志表-删除按钮', '1', '删除按钮', '', '', '', NULL, '1', '1', 1, '2023-1-7 17:27:20', '2023-1-7 17:27:20');
INSERT INTO `z_permission` VALUES ('dae2806b-78fd-4e7c-97a7-2801867dddae', 'b6962d4c-910c-4641-b638-129439f97726', 'zlog-zOperateLog-exportExcel', '操作日志表-导出Excel按钮', '1', '导出Excel按钮', '', '', '', NULL, '1', '1', 1, '2023-1-7 17:27:20', '2023-1-7 17:27:20');

-- ----------------------------
-- 生成接口信息
-- ----------------------------
INSERT INTO `z_api` VALUES ('f5b1d2c7-88a3-4ce4-bfce-b904c62abeb7', NULL, '新增-操作日志表', 'zlog:zOperateLog:add', '/zlog/zOperateLog/add', 'POST', '新增-操作日志表', 'ZOperateLogController', 'add', 100, '2023-1-7 17:27:20', '2023-1-7 17:27:20');
INSERT INTO `z_api` VALUES ('8507b7ac-93f5-49f3-9926-0a0147eae377', NULL, '修改-操作日志表', 'zlog:zOperateLog:update', '/zlog/zOperateLog/update', 'POST', '修改-操作日志表', 'ZOperateLogController', 'update', 100, '2023-1-7 17:27:20', '2023-1-7 17:27:20');
INSERT INTO `z_api` VALUES ('50886878-bb69-48d2-b2bf-bd25310ac849', NULL, '删除-操作日志表', 'zlog:zOperateLog:delete', '/zlog/zOperateLog/delete', 'POST', '删除-操作日志表', 'ZOperateLogController', 'delete', 100, '2023-1-7 17:27:20', '2023-1-7 17:27:20');
INSERT INTO `z_api` VALUES ('67ec253e-ef38-4e10-ba64-6e6f2d762626', NULL, '导出Excel-操作日志表', 'zlog:zOperateLog:export:excel', '/zlog/zOperateLog/export/excel', 'GET', '导出Excel-操作日志表', 'ZOperateLogController', 'exportExcel', 100, '2023-1-7 17:27:20', '2023-1-7 17:27:20');
INSERT INTO `z_api` VALUES ('60e5c552-ddcd-49ab-adb4-5078beee9c99', NULL, '分页列表-操作日志表', 'zlog:zOperateLog:list', '/zlog/zOperateLog/list', 'GET', '分页列表-操作日志表', 'ZOperateLogController', 'list', 100, '2023-1-7 17:27:20', '2023-1-7 17:27:20');
INSERT INTO `z_api` VALUES ('5493298d-065d-42a5-9ad8-9c38ee96c701', NULL, '详情-操作日志表', 'zlog:zOperateLog:getById', '/zlog/zOperateLog/getById', 'GET', '详情-操作日志表', 'ZOperateLogController', 'getById', 100, '2023-1-7 17:27:20', '2023-1-7 17:27:20');

-- ----------------------------
-- 生成菜单/按钮，关联接口
-- ----------------------------
INSERT INTO `z_permission_api` VALUES ('c792c99a-c3f2-4390-9cea-78c35853c532', 'f5b1d2c7-88a3-4ce4-bfce-b904c62abeb7');
INSERT INTO `z_permission_api` VALUES ('503fc98e-16e7-48d0-ad6a-331456a62a25', '8507b7ac-93f5-49f3-9926-0a0147eae377');
INSERT INTO `z_permission_api` VALUES ('5f329aae-4c2a-425f-a957-70149f18f104', '50886878-bb69-48d2-b2bf-bd25310ac849');
INSERT INTO `z_permission_api` VALUES ('dae2806b-78fd-4e7c-97a7-2801867dddae', '67ec253e-ef38-4e10-ba64-6e6f2d762626');
INSERT INTO `z_permission_api` VALUES ('b6962d4c-910c-4641-b638-129439f97726', '60e5c552-ddcd-49ab-adb4-5078beee9c99');
INSERT INTO `z_permission_api` VALUES ('b6962d4c-910c-4641-b638-129439f97726', '5493298d-065d-42a5-9ad8-9c38ee96c701');
