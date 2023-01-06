-- ----------------------------
-- 生成菜单信息
-- ----------------------------
INSERT INTO `z_permission` VALUES ('9ba96f7f-7812-4407-90f7-c6c5e016f52e', '-1', 'zuserlock-zUserLock', '用户锁定', '0', '用户锁定', '', '/system/userlock', '/system/userlock/index', NULL, '1', '1', 100, '2023-1-5 15:13:32', '2023-1-5 15:13:32');

-- ----------------------------
-- 生成页面按钮信息（添加、修改、删除）
-- ----------------------------
INSERT INTO `z_permission` VALUES ('ccdaa101-b2a8-4d27-95ee-3f55cea1b8b0', '9ba96f7f-7812-4407-90f7-c6c5e016f52e', 'zuserlock-zUserLock-unlock', '用户解锁', '1', '解锁按钮', '', '', '', NULL, '1', '1', 1, '2023-1-5 15:13:32', '2023-1-5 15:13:32');

-- ----------------------------
-- 生成接口信息
-- ----------------------------
INSERT INTO `z_api` VALUES ('b2285a87-b869-4585-bc1b-b469e2b37f49', NULL, '用户解锁', 'zuserlock:zUserLock:unlock', '/zuserlock/zUserLock/unlock', 'POST', '用户解锁', 'ZUserLockController', 'unlock', 100, '2023-1-5 15:13:32', '2023-1-5 15:13:32');
INSERT INTO `z_api` VALUES ('1db593ca-0715-4f78-9b29-ff2fdf93ec5d', NULL, '永久锁定用户列表', 'zuserlock:zUserLock:list', '/zuserlock/zUserLock/list', 'GET', '永久锁定用户列表', 'ZUserLockController', 'list', 100, '2023-1-5 15:13:32', '2023-1-5 15:13:32');
INSERT INTO `z_api` VALUES ('206cb14f-1c22-4ac4-bd6c-12be4d2389bb', NULL, '缓存锁定用户列表', 'zuserlock:zUserLock:cacheList', '/zuserlock/zUserLock/cacheList', 'GET', '缓存锁定用户列表', 'ZUserLockController', 'cacheList', 100, '2023-1-5 15:13:32', '2023-1-5 15:13:32');

-- ----------------------------
-- 生成菜单/按钮，关联接口
-- ----------------------------
INSERT INTO `z_permission_api` VALUES ('ccdaa101-b2a8-4d27-95ee-3f55cea1b8b0', 'b2285a87-b869-4585-bc1b-b469e2b37f49');
INSERT INTO `z_permission_api` VALUES ('9ba96f7f-7812-4407-90f7-c6c5e016f52e', '1db593ca-0715-4f78-9b29-ff2fdf93ec5d');
INSERT INTO `z_permission_api` VALUES ('9ba96f7f-7812-4407-90f7-c6c5e016f52e', '206cb14f-1c22-4ac4-bd6c-12be4d2389bb');
