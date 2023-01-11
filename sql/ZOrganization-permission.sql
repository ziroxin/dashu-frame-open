-- ----------------------------
-- 生成菜单信息
-- ----------------------------
INSERT INTO `z_permission` VALUES ('dc3395c1-1c1b-41ef-859d-e18b130ef209', '-1', 'zorg-zOrganization', '组织机构表', '0', '组织机构表', '', '/system/org', '/system/org/index', NULL, '1', '1', 100, '2023-1-11 14:13:51', '2023-1-11 14:13:51');

-- ----------------------------
-- 生成页面按钮信息（添加、修改、删除）
-- ----------------------------
INSERT INTO `z_permission` VALUES ('7fc63a94-bec0-461f-8c4e-12c2d6d77529', 'dc3395c1-1c1b-41ef-859d-e18b130ef209', 'zorg-zOrganization-add', '组织机构表-添加按钮', '1', '添加按钮', '', '', '', NULL, '1', '1', 1, '2023-1-11 14:13:51', '2023-1-11 14:13:51');
INSERT INTO `z_permission` VALUES ('97d509be-85e5-4a64-bf33-c669df91ba53', 'dc3395c1-1c1b-41ef-859d-e18b130ef209', 'zorg-zOrganization-update', '组织机构表-修改按钮', '1', '修改按钮', '', '', '', NULL, '1', '1', 1, '2023-1-11 14:13:51', '2023-1-11 14:13:51');
INSERT INTO `z_permission` VALUES ('8e81c817-350a-4063-a219-44cf4d0df891', 'dc3395c1-1c1b-41ef-859d-e18b130ef209', 'zorg-zOrganization-delete', '组织机构表-删除按钮', '1', '删除按钮', '', '', '', NULL, '1', '1', 1, '2023-1-11 14:13:51', '2023-1-11 14:13:51');
INSERT INTO `z_permission` VALUES ('fed08350-a5d1-4633-bd03-6972ed651109', 'dc3395c1-1c1b-41ef-859d-e18b130ef209', 'zorg-zOrganization-exportExcel', '组织机构表-导出Excel按钮', '1', '导出Excel按钮', '', '', '', NULL, '1', '1', 1, '2023-1-11 14:13:51', '2023-1-11 14:13:51');

-- ----------------------------
-- 生成接口信息
-- ----------------------------
INSERT INTO `z_api` VALUES ('097abf79-00a2-49ef-91c6-acdd08afed30', NULL, '新增-组织机构表', 'zorg:zOrganization:add', '/zorg/zOrganization/add', 'POST', '新增-组织机构表', 'ZOrganizationController', 'add', 100, '2023-1-11 14:13:51', '2023-1-11 14:13:51');
INSERT INTO `z_api` VALUES ('204ee757-ab6e-467c-ae4c-b9a1d2d4cc29', NULL, '修改-组织机构表', 'zorg:zOrganization:update', '/zorg/zOrganization/update', 'POST', '修改-组织机构表', 'ZOrganizationController', 'update', 100, '2023-1-11 14:13:51', '2023-1-11 14:13:51');
INSERT INTO `z_api` VALUES ('02955bbb-ae0f-441a-8f75-79524a630614', NULL, '删除-组织机构表', 'zorg:zOrganization:delete', '/zorg/zOrganization/delete', 'POST', '删除-组织机构表', 'ZOrganizationController', 'delete', 100, '2023-1-11 14:13:51', '2023-1-11 14:13:51');
INSERT INTO `z_api` VALUES ('2ed7f103-880d-45f0-b597-31ef350ee4c8', NULL, '导出Excel-组织机构表', 'zorg:zOrganization:export:excel', '/zorg/zOrganization/export/excel', 'GET', '导出Excel-组织机构表', 'ZOrganizationController', 'exportExcel', 100, '2023-1-11 14:13:51', '2023-1-11 14:13:51');
INSERT INTO `z_api` VALUES ('c44babb2-413e-48f4-9e7c-330b37891ffd', NULL, '分页列表-组织机构表', 'zorg:zOrganization:list', '/zorg/zOrganization/list', 'GET', '分页列表-组织机构表', 'ZOrganizationController', 'list', 100, '2023-1-11 14:13:51', '2023-1-11 14:13:51');
INSERT INTO `z_api` VALUES ('d9eaa3ae-5f3f-4ef6-99e6-0e03f1643334', NULL, '详情-组织机构表', 'zorg:zOrganization:getById', '/zorg/zOrganization/getById', 'GET', '详情-组织机构表', 'ZOrganizationController', 'getById', 100, '2023-1-11 14:13:51', '2023-1-11 14:13:51');

-- ----------------------------
-- 生成菜单/按钮，关联接口
-- ----------------------------
INSERT INTO `z_permission_api` VALUES ('7fc63a94-bec0-461f-8c4e-12c2d6d77529', '097abf79-00a2-49ef-91c6-acdd08afed30');
INSERT INTO `z_permission_api` VALUES ('97d509be-85e5-4a64-bf33-c669df91ba53', '204ee757-ab6e-467c-ae4c-b9a1d2d4cc29');
INSERT INTO `z_permission_api` VALUES ('8e81c817-350a-4063-a219-44cf4d0df891', '02955bbb-ae0f-441a-8f75-79524a630614');
INSERT INTO `z_permission_api` VALUES ('fed08350-a5d1-4633-bd03-6972ed651109', '2ed7f103-880d-45f0-b597-31ef350ee4c8');
INSERT INTO `z_permission_api` VALUES ('dc3395c1-1c1b-41ef-859d-e18b130ef209', 'c44babb2-413e-48f4-9e7c-330b37891ffd');
INSERT INTO `z_permission_api` VALUES ('dc3395c1-1c1b-41ef-859d-e18b130ef209', 'd9eaa3ae-5f3f-4ef6-99e6-0e03f1643334');
