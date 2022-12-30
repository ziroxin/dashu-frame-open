-- ----------------------------
-- 生成菜单信息
-- ----------------------------
INSERT INTO `z_permission` VALUES ('b8e0acf4-ddb0-4eb4-bd61-4234b2cc56b8', '-1', 'zsafety-zSafety', '密码安全等设置', '0', '密码安全等设置', '', '/system/safety', '/system/safety/index', NULL, '1', '1', 100, '2022-12-30 9:09:42', '2022-12-30 9:09:42');

-- ----------------------------
-- 生成页面按钮信息（添加、修改、删除）
-- ----------------------------
INSERT INTO `z_permission` VALUES ('caf4c50b-8d55-443e-81ed-9ed20ed1f3cd', 'b8e0acf4-ddb0-4eb4-bd61-4234b2cc56b8', 'zsafety-zSafety-add', '密码安全等设置-添加按钮', '1', '添加按钮', '', '', '', NULL, '1', '1', 1, '2022-12-30 9:09:42', '2022-12-30 9:09:42');
INSERT INTO `z_permission` VALUES ('6accaf71-635e-48e2-ac9e-e64a72aaf3a0', 'b8e0acf4-ddb0-4eb4-bd61-4234b2cc56b8', 'zsafety-zSafety-update', '密码安全等设置-修改按钮', '1', '修改按钮', '', '', '', NULL, '1', '1', 1, '2022-12-30 9:09:42', '2022-12-30 9:09:42');
INSERT INTO `z_permission` VALUES ('f3b2bd1a-6773-4240-ae4f-09a19291929f', 'b8e0acf4-ddb0-4eb4-bd61-4234b2cc56b8', 'zsafety-zSafety-delete', '密码安全等设置-删除按钮', '1', '删除按钮', '', '', '', NULL, '1', '1', 1, '2022-12-30 9:09:42', '2022-12-30 9:09:42');
INSERT INTO `z_permission` VALUES ('9ba4d071-dc5f-4f9c-91d6-48f2dc1b2c9a', 'b8e0acf4-ddb0-4eb4-bd61-4234b2cc56b8', 'zsafety-zSafety-exportExcel', '密码安全等设置-导出Excel按钮', '1', '导出Excel按钮', '', '', '', NULL, '1', '1', 1, '2022-12-30 9:09:42', '2022-12-30 9:09:42');

-- ----------------------------
-- 生成接口信息
-- ----------------------------
INSERT INTO `z_api` VALUES ('b15dad7b-766b-4302-a4aa-c67964d589c7', NULL, '新增-密码安全等设置', 'zsafety:zSafety:add', '/zsafety/zSafety/add', 'POST', '新增-密码安全等设置', 'ZSafetyController', 'add', 100, '2022-12-30 9:09:42', '2022-12-30 9:09:42');
INSERT INTO `z_api` VALUES ('f79e96d2-7fe3-457e-a3a9-ebc33b610634', NULL, '修改-密码安全等设置', 'zsafety:zSafety:update', '/zsafety/zSafety/update', 'POST', '修改-密码安全等设置', 'ZSafetyController', 'update', 100, '2022-12-30 9:09:42', '2022-12-30 9:09:42');
INSERT INTO `z_api` VALUES ('5a442795-2cf8-424a-b36d-9a849fc8a197', NULL, '删除-密码安全等设置', 'zsafety:zSafety:delete', '/zsafety/zSafety/delete', 'POST', '删除-密码安全等设置', 'ZSafetyController', 'delete', 100, '2022-12-30 9:09:42', '2022-12-30 9:09:42');
INSERT INTO `z_api` VALUES ('b7cd21d6-f59d-4f53-880b-3a4f52c17c32', NULL, '导出Excel-密码安全等设置', 'zsafety:zSafety:export:excel', '/zsafety/zSafety/export/excel', 'GET', '导出Excel-密码安全等设置', 'ZSafetyController', 'exportExcel', 100, '2022-12-30 9:09:42', '2022-12-30 9:09:42');
INSERT INTO `z_api` VALUES ('88506f77-df37-48d0-983d-db8a5568f05f', NULL, '分页列表-密码安全等设置', 'zsafety:zSafety:list', '/zsafety/zSafety/list', 'GET', '分页列表-密码安全等设置', 'ZSafetyController', 'list', 100, '2022-12-30 9:09:42', '2022-12-30 9:09:42');
INSERT INTO `z_api` VALUES ('1cd0d688-4b39-4ca8-a01c-d4b1c737d297', NULL, '详情-密码安全等设置', 'zsafety:zSafety:getById', '/zsafety/zSafety/getById', 'GET', '详情-密码安全等设置', 'ZSafetyController', 'getById', 100, '2022-12-30 9:09:42', '2022-12-30 9:09:42');

-- ----------------------------
-- 生成菜单/按钮，关联接口
-- ----------------------------
INSERT INTO `z_permission_api` VALUES ('caf4c50b-8d55-443e-81ed-9ed20ed1f3cd', 'b15dad7b-766b-4302-a4aa-c67964d589c7');
INSERT INTO `z_permission_api` VALUES ('6accaf71-635e-48e2-ac9e-e64a72aaf3a0', 'f79e96d2-7fe3-457e-a3a9-ebc33b610634');
INSERT INTO `z_permission_api` VALUES ('f3b2bd1a-6773-4240-ae4f-09a19291929f', '5a442795-2cf8-424a-b36d-9a849fc8a197');
INSERT INTO `z_permission_api` VALUES ('9ba4d071-dc5f-4f9c-91d6-48f2dc1b2c9a', 'b7cd21d6-f59d-4f53-880b-3a4f52c17c32');
INSERT INTO `z_permission_api` VALUES ('b8e0acf4-ddb0-4eb4-bd61-4234b2cc56b8', '88506f77-df37-48d0-983d-db8a5568f05f');
INSERT INTO `z_permission_api` VALUES ('b8e0acf4-ddb0-4eb4-bd61-4234b2cc56b8', '1cd0d688-4b39-4ca8-a01c-d4b1c737d297');
