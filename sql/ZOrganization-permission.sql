-- ----------------------------
-- 生成菜单信息
-- ----------------------------
INSERT INTO `z_permission` VALUES ('f816e966-12fb-426b-82e1-3fdf42e98808', '-1', 'zorg-zOrganization', '组织机构表', '0', '组织机构表', '', '/system/org', '/system/org/index', NULL, '1', '1', 100, '2023-1-11 11:21:38', '2023-1-11 11:21:38');

-- ----------------------------
-- 生成页面按钮信息（添加、修改、删除）
-- ----------------------------
INSERT INTO `z_permission` VALUES ('e01e4eae-968f-4c16-a6e2-99dbcda1b849', 'f816e966-12fb-426b-82e1-3fdf42e98808', 'zorg-zOrganization-add', '组织机构表-添加按钮', '1', '添加按钮', '', '', '', NULL, '1', '1', 1, '2023-1-11 11:21:38', '2023-1-11 11:21:38');
INSERT INTO `z_permission` VALUES ('1291fbd6-beea-44d9-8312-6a2b7e386b3d', 'f816e966-12fb-426b-82e1-3fdf42e98808', 'zorg-zOrganization-update', '组织机构表-修改按钮', '1', '修改按钮', '', '', '', NULL, '1', '1', 1, '2023-1-11 11:21:38', '2023-1-11 11:21:38');
INSERT INTO `z_permission` VALUES ('15840f25-bdf0-4ffe-9100-9c0f93a6d9d7', 'f816e966-12fb-426b-82e1-3fdf42e98808', 'zorg-zOrganization-delete', '组织机构表-删除按钮', '1', '删除按钮', '', '', '', NULL, '1', '1', 1, '2023-1-11 11:21:38', '2023-1-11 11:21:38');
INSERT INTO `z_permission` VALUES ('741dbe03-d688-4f98-9684-b46da15da1fa', 'f816e966-12fb-426b-82e1-3fdf42e98808', 'zorg-zOrganization-exportExcel', '组织机构表-导出Excel按钮', '1', '导出Excel按钮', '', '', '', NULL, '1', '1', 1, '2023-1-11 11:21:38', '2023-1-11 11:21:38');

-- ----------------------------
-- 生成接口信息
-- ----------------------------
INSERT INTO `z_api` VALUES ('4025f5c7-77cf-43e7-beba-5e47a246fa99', NULL, '新增-组织机构表', 'zorg:zOrganization:add', '/zorg/zOrganization/add', 'POST', '新增-组织机构表', 'ZOrganizationController', 'add', 100, '2023-1-11 11:21:38', '2023-1-11 11:21:38');
INSERT INTO `z_api` VALUES ('0cc45591-f45a-47c1-b877-922aebd10670', NULL, '修改-组织机构表', 'zorg:zOrganization:update', '/zorg/zOrganization/update', 'POST', '修改-组织机构表', 'ZOrganizationController', 'update', 100, '2023-1-11 11:21:38', '2023-1-11 11:21:38');
INSERT INTO `z_api` VALUES ('4fe72e12-7d99-4664-8fc3-67f9bfe71fa7', NULL, '删除-组织机构表', 'zorg:zOrganization:delete', '/zorg/zOrganization/delete', 'POST', '删除-组织机构表', 'ZOrganizationController', 'delete', 100, '2023-1-11 11:21:38', '2023-1-11 11:21:38');
INSERT INTO `z_api` VALUES ('c1661220-19df-4fc9-8a11-7dfe823f78de', NULL, '导出Excel-组织机构表', 'zorg:zOrganization:export:excel', '/zorg/zOrganization/export/excel', 'GET', '导出Excel-组织机构表', 'ZOrganizationController', 'exportExcel', 100, '2023-1-11 11:21:38', '2023-1-11 11:21:38');
INSERT INTO `z_api` VALUES ('85bca0a8-bbe1-4018-85e1-94e5d38f3b49', NULL, '分页列表-组织机构表', 'zorg:zOrganization:list', '/zorg/zOrganization/list', 'GET', '分页列表-组织机构表', 'ZOrganizationController', 'list', 100, '2023-1-11 11:21:38', '2023-1-11 11:21:38');
INSERT INTO `z_api` VALUES ('18b8d79d-e7ef-4cd3-87aa-f9bf7e1aa5dc', NULL, '详情-组织机构表', 'zorg:zOrganization:getById', '/zorg/zOrganization/getById', 'GET', '详情-组织机构表', 'ZOrganizationController', 'getById', 100, '2023-1-11 11:21:38', '2023-1-11 11:21:38');

-- ----------------------------
-- 生成菜单/按钮，关联接口
-- ----------------------------
INSERT INTO `z_permission_api` VALUES ('e01e4eae-968f-4c16-a6e2-99dbcda1b849', '4025f5c7-77cf-43e7-beba-5e47a246fa99');
INSERT INTO `z_permission_api` VALUES ('1291fbd6-beea-44d9-8312-6a2b7e386b3d', '0cc45591-f45a-47c1-b877-922aebd10670');
INSERT INTO `z_permission_api` VALUES ('15840f25-bdf0-4ffe-9100-9c0f93a6d9d7', '4fe72e12-7d99-4664-8fc3-67f9bfe71fa7');
INSERT INTO `z_permission_api` VALUES ('741dbe03-d688-4f98-9684-b46da15da1fa', 'c1661220-19df-4fc9-8a11-7dfe823f78de');
INSERT INTO `z_permission_api` VALUES ('f816e966-12fb-426b-82e1-3fdf42e98808', '85bca0a8-bbe1-4018-85e1-94e5d38f3b49');
INSERT INTO `z_permission_api` VALUES ('f816e966-12fb-426b-82e1-3fdf42e98808', '18b8d79d-e7ef-4cd3-87aa-f9bf7e1aa5dc');
