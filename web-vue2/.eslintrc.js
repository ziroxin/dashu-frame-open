module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es6: true,
  },
  'extends': [
    'plugin:vue/essential',
    'eslint:recommended'
  ],
  parserOptions: {
    parser: 'babel-eslint'
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'error' : 'off',// 生产环境下禁止console.log
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',// 生产环境下禁止debugger
    'no-unused-vars': 'off',// 不验证未使用的变量
  }
}
