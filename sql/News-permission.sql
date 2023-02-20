-- ----------------------------
-- 生成菜单信息
-- ----------------------------
INSERT INTO `z_permission` VALUES ('43d0b63e-1b86-40f8-bd98-c5b82b745b46', '-1', 'news-news', '新闻表-测试', '0', '新闻表-测试', '', '/news', '/news/index', NULL, 1, 1, 'noRedirect', 0, 1, 0, NULL, 100, '2023-2-17 20:12:16', '2023-2-17 20:12:16');

-- ----------------------------
-- 生成页面按钮信息（添加、修改、删除）
-- ----------------------------
INSERT INTO `z_permission` VALUES ('caad3539-d9ef-4561-aba8-c57282b28ecd', '43d0b63e-1b86-40f8-bd98-c5b82b745b46', 'news-news-add', '新闻表-测试-添加按钮', '1', '添加按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '2023-2-17 20:12:16', '2023-2-17 20:12:16');
INSERT INTO `z_permission` VALUES ('fbfec480-b534-4eee-a832-6d31393f2cb9', '43d0b63e-1b86-40f8-bd98-c5b82b745b46', 'news-news-update', '新闻表-测试-修改按钮', '1', '修改按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '2023-2-17 20:12:16', '2023-2-17 20:12:16');
INSERT INTO `z_permission` VALUES ('83959d0f-a692-4617-9f00-57c68358c8d0', '43d0b63e-1b86-40f8-bd98-c5b82b745b46', 'news-news-delete', '新闻表-测试-删除按钮', '1', '删除按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '2023-2-17 20:12:16', '2023-2-17 20:12:16');
INSERT INTO `z_permission` VALUES ('4f534b05-7b90-43ec-9884-613243db10fb', '43d0b63e-1b86-40f8-bd98-c5b82b745b46', 'news-news-exportExcel', '新闻表-测试-导出Excel按钮', '1', '导出Excel按钮', '', '', '', NULL, 1, 1, NULL, 0, 1, 0, NULL, 1, '2023-2-17 20:12:16', '2023-2-17 20:12:16');

-- ----------------------------
-- 生成接口信息
-- ----------------------------
INSERT INTO `z_api` VALUES ('98fec7a4-467f-4c2d-b3ec-ea71b1b101cb', NULL, '新增-新闻表-测试', 'news:news:add', '/news/news/add', 'POST', '新增-新闻表-测试', 'NewsController', 'add', 100, '2023-2-17 20:12:16', '2023-2-17 20:12:16');
INSERT INTO `z_api` VALUES ('65ef3a41-5c77-4395-adf3-d76ab5d3c37d', NULL, '修改-新闻表-测试', 'news:news:update', '/news/news/update', 'POST', '修改-新闻表-测试', 'NewsController', 'update', 100, '2023-2-17 20:12:16', '2023-2-17 20:12:16');
INSERT INTO `z_api` VALUES ('10fb6e84-2767-4d10-818c-fba3ad439b90', NULL, '删除-新闻表-测试', 'news:news:delete', '/news/news/delete', 'POST', '删除-新闻表-测试', 'NewsController', 'delete', 100, '2023-2-17 20:12:16', '2023-2-17 20:12:16');
INSERT INTO `z_api` VALUES ('e6eeef03-20fd-441e-afe6-b9735148927f', NULL, '导出Excel-新闻表-测试', 'news:news:export:excel', '/news/news/export/excel', 'GET', '导出Excel-新闻表-测试', 'NewsController', 'exportExcel', 100, '2023-2-17 20:12:16', '2023-2-17 20:12:16');
INSERT INTO `z_api` VALUES ('a1b3a310-1ed9-42ac-9423-4c71effd0ce5', NULL, '分页列表-新闻表-测试', 'news:news:list', '/news/news/list', 'GET', '分页列表-新闻表-测试', 'NewsController', 'list', 100, '2023-2-17 20:12:16', '2023-2-17 20:12:16');
INSERT INTO `z_api` VALUES ('9ea479ff-f0d6-4c3b-bce9-e1ce6249f7bf', NULL, '详情-新闻表-测试', 'news:news:getById', '/news/news/getById', 'GET', '详情-新闻表-测试', 'NewsController', 'getById', 100, '2023-2-17 20:12:16', '2023-2-17 20:12:16');

-- ----------------------------
-- 生成菜单/按钮，关联接口
-- ----------------------------
INSERT INTO `z_permission_api` VALUES ('caad3539-d9ef-4561-aba8-c57282b28ecd', '98fec7a4-467f-4c2d-b3ec-ea71b1b101cb');
INSERT INTO `z_permission_api` VALUES ('fbfec480-b534-4eee-a832-6d31393f2cb9', '65ef3a41-5c77-4395-adf3-d76ab5d3c37d');
INSERT INTO `z_permission_api` VALUES ('83959d0f-a692-4617-9f00-57c68358c8d0', '10fb6e84-2767-4d10-818c-fba3ad439b90');
INSERT INTO `z_permission_api` VALUES ('4f534b05-7b90-43ec-9884-613243db10fb', 'e6eeef03-20fd-441e-afe6-b9735148927f');
INSERT INTO `z_permission_api` VALUES ('43d0b63e-1b86-40f8-bd98-c5b82b745b46', 'a1b3a310-1ed9-42ac-9423-4c71effd0ce5');
INSERT INTO `z_permission_api` VALUES ('43d0b63e-1b86-40f8-bd98-c5b82b745b46', '9ea479ff-f0d6-4c3b-bce9-e1ce6249f7bf');
