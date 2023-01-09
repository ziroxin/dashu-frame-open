-- ----------------------------
-- 生成菜单信息
-- ----------------------------
INSERT INTO `z_permission` VALUES ('12a8f4cb-d1a4-474a-aab1-c15b0b0868bf', '-1', 'zlog-zOperateLog', '操作日志表', '0', '操作日志表', '', '/system/log', '/system/log/index', NULL, '1', '1', 100, '2023-1-7 17:36:25', '2023-1-7 17:36:25');

-- ----------------------------
-- 生成页面按钮信息（添加、修改、删除）
-- ----------------------------
INSERT INTO `z_permission` VALUES ('495c7ed0-f055-4744-bbd0-4ab135d10020', '12a8f4cb-d1a4-474a-aab1-c15b0b0868bf', 'zlog-zOperateLog-delete', '操作日志表-删除按钮', '1', '删除按钮', '', '', '', NULL, '1', '1', 1, '2023-1-7 17:36:25', '2023-1-7 17:36:25');
INSERT INTO `z_permission` VALUES ('044ca078-54ad-44c8-87bb-29f5938e403c', '12a8f4cb-d1a4-474a-aab1-c15b0b0868bf', 'zlog-zOperateLog-exportExcel', '操作日志表-导出Excel按钮', '1', '导出Excel按钮', '', '', '', NULL, '1', '1', 1, '2023-1-7 17:36:25', '2023-1-7 17:36:25');

-- ----------------------------
-- 生成接口信息
-- ----------------------------
INSERT INTO `z_api` VALUES ('eab60c29-7f2c-4fed-b350-1164e4107694', NULL, '删除-操作日志表', 'zlog:zOperateLog:delete', '/zlog/zOperateLog/delete', 'POST', '删除-操作日志表', 'ZOperateLogController', 'delete', 100, '2023-1-7 17:36:25', '2023-1-7 17:36:25');
INSERT INTO `z_api` VALUES ('8d137faf-d578-4a6a-81ea-6c770ccb050c', NULL, '导出Excel-操作日志表', 'zlog:zOperateLog:export:excel', '/zlog/zOperateLog/export/excel', 'GET', '导出Excel-操作日志表', 'ZOperateLogController', 'exportExcel', 100, '2023-1-7 17:36:25', '2023-1-7 17:36:25');
INSERT INTO `z_api` VALUES ('01d17c0e-2db0-4960-a497-3a5fd757c237', NULL, '分页列表-操作日志表', 'zlog:zOperateLog:list', '/zlog/zOperateLog/list', 'GET', '分页列表-操作日志表', 'ZOperateLogController', 'list', 100, '2023-1-7 17:36:25', '2023-1-7 17:36:25');
INSERT INTO `z_api` VALUES ('44fe7e2b-e8f2-486f-b7d0-6c57acc8013a', NULL, '详情-操作日志表', 'zlog:zOperateLog:getById', '/zlog/zOperateLog/getById', 'GET', '详情-操作日志表', 'ZOperateLogController', 'getById', 100, '2023-1-7 17:36:25', '2023-1-7 17:36:25');

-- ----------------------------
-- 生成菜单/按钮，关联接口
-- ----------------------------
INSERT INTO `z_permission_api` VALUES ('495c7ed0-f055-4744-bbd0-4ab135d10020', 'eab60c29-7f2c-4fed-b350-1164e4107694');
INSERT INTO `z_permission_api` VALUES ('044ca078-54ad-44c8-87bb-29f5938e403c', '8d137faf-d578-4a6a-81ea-6c770ccb050c');
INSERT INTO `z_permission_api` VALUES ('12a8f4cb-d1a4-474a-aab1-c15b0b0868bf', '01d17c0e-2db0-4960-a497-3a5fd757c237');
INSERT INTO `z_permission_api` VALUES ('12a8f4cb-d1a4-474a-aab1-c15b0b0868bf', '44fe7e2b-e8f2-486f-b7d0-6c57acc8013a');
