-- ----------------------------
-- 生成菜单信息
-- ----------------------------
INSERT INTO `z_permission` VALUES ('f01cde7e-5e47-42f6-9701-8e605025920e', '-1', 'atest-aTest', '测试表', '0', '测试表', '', '/atest', '/atest/index', NULL, 1, 1, 'noRedirect', 0, 1, 0, NULL, 100, '2023-3-30 9:46:07', '2023-3-30 9:46:07');

-- ----------------------------
-- 生成页面按钮信息（添加、修改、删除）
-- ----------------------------
INSERT INTO `z_permission` VALUES ('22590c52-b5b1-41e4-a3b3-4efac675bb64', 'f01cde7e-5e47-42f6-9701-8e605025920e', 'atest-aTest-add', '测试表-添加按钮', '1', '添加按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '2023-3-30 9:46:07', '2023-3-30 9:46:07');
INSERT INTO `z_permission` VALUES ('38530fea-fafe-4a68-aefd-4f1ae734340f', 'f01cde7e-5e47-42f6-9701-8e605025920e', 'atest-aTest-update', '测试表-修改按钮', '1', '修改按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '2023-3-30 9:46:07', '2023-3-30 9:46:07');
INSERT INTO `z_permission` VALUES ('68c8f2ae-dfbf-4cdf-a575-5f0acd10544e', 'f01cde7e-5e47-42f6-9701-8e605025920e', 'atest-aTest-delete', '测试表-删除按钮', '1', '删除按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '2023-3-30 9:46:07', '2023-3-30 9:46:07');
INSERT INTO `z_permission` VALUES ('33a6f29c-5293-46a3-8aa2-240cc8cd0fc9', 'f01cde7e-5e47-42f6-9701-8e605025920e', 'atest-aTest-exportExcel', '测试表-导出Excel按钮', '1', '导出Excel按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '2023-3-30 9:46:07', '2023-3-30 9:46:07');
INSERT INTO `z_permission` VALUES ('37127897-aaae-421a-bf07-de4a91ebdd7a', 'f01cde7e-5e47-42f6-9701-8e605025920e', 'atest-aTest-importExcel', '测试表-导入Excel按钮', '1', '导入Excel按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '2023-3-30 9:46:07', '2023-3-30 9:46:07');

-- ----------------------------
-- 生成接口信息
-- ----------------------------
INSERT INTO `z_api` VALUES ('9fc93976-2aa4-4b34-abb5-2c502d9b81e7', NULL, '新增-测试表', 'atest:aTest:add', '/atest/aTest/add', 'POST', '新增-测试表', 'ATestController', 'add', 100, '2023-3-30 9:46:07', '2023-3-30 9:46:07');
INSERT INTO `z_api` VALUES ('87f05fb6-460f-4173-9123-2c19f4cba098', NULL, '修改-测试表', 'atest:aTest:update', '/atest/aTest/update', 'POST', '修改-测试表', 'ATestController', 'update', 100, '2023-3-30 9:46:07', '2023-3-30 9:46:07');
INSERT INTO `z_api` VALUES ('108b796f-06d5-4951-869d-6b2d452a7f29', NULL, '删除-测试表', 'atest:aTest:delete', '/atest/aTest/delete', 'POST', '删除-测试表', 'ATestController', 'delete', 100, '2023-3-30 9:46:07', '2023-3-30 9:46:07');
INSERT INTO `z_api` VALUES ('a0342906-499b-43fa-be1d-c4589f184139', NULL, '导出Excel-测试表', 'atest:aTest:export:excel', '/atest/aTest/export/excel', 'GET', '导出Excel-测试表', 'ATestController', 'exportExcel', 100, '2023-3-30 9:46:07', '2023-3-30 9:46:07');
INSERT INTO `z_api` VALUES ('28267e22-4bd1-46cd-9b8a-68a94b921784', NULL, '导入Excel-测试表', 'atest:aTest:import:excel', '/atest/aTest/import/excel', 'POST', '导入Excel-测试表', 'ATestController', 'importExcel', 100, '2023-3-30 9:46:07', '2023-3-30 9:46:07');
INSERT INTO `z_api` VALUES ('cc205321-bb02-4b84-90be-c62b221cf601', NULL, '分页列表-测试表', 'atest:aTest:list', '/atest/aTest/list', 'GET', '分页列表-测试表', 'ATestController', 'list', 100, '2023-3-30 9:46:07', '2023-3-30 9:46:07');
INSERT INTO `z_api` VALUES ('78ff9af5-d9ba-432e-bd77-4d7872ed0cfa', NULL, '详情-测试表', 'atest:aTest:getById', '/atest/aTest/getById', 'GET', '详情-测试表', 'ATestController', 'getById', 100, '2023-3-30 9:46:07', '2023-3-30 9:46:07');

-- ----------------------------
-- 生成菜单/按钮，关联接口
-- ----------------------------
INSERT INTO `z_permission_api` VALUES ('22590c52-b5b1-41e4-a3b3-4efac675bb64', '9fc93976-2aa4-4b34-abb5-2c502d9b81e7');
INSERT INTO `z_permission_api` VALUES ('38530fea-fafe-4a68-aefd-4f1ae734340f', '87f05fb6-460f-4173-9123-2c19f4cba098');
INSERT INTO `z_permission_api` VALUES ('68c8f2ae-dfbf-4cdf-a575-5f0acd10544e', '108b796f-06d5-4951-869d-6b2d452a7f29');
INSERT INTO `z_permission_api` VALUES ('33a6f29c-5293-46a3-8aa2-240cc8cd0fc9', 'a0342906-499b-43fa-be1d-c4589f184139');
INSERT INTO `z_permission_api` VALUES ('37127897-aaae-421a-bf07-de4a91ebdd7a', '28267e22-4bd1-46cd-9b8a-68a94b921784');
INSERT INTO `z_permission_api` VALUES ('f01cde7e-5e47-42f6-9701-8e605025920e', 'cc205321-bb02-4b84-90be-c62b221cf601');
INSERT INTO `z_permission_api` VALUES ('f01cde7e-5e47-42f6-9701-8e605025920e', '78ff9af5-d9ba-432e-bd77-4d7872ed0cfa');
