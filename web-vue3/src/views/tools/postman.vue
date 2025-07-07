<template>
  <div class="app-container postman1" :style="'height: ' + ($windowHeight-120) + 'px;'">
    <h2 style="text-align: center;">本项目接口测试工具</h2>
    <div class="row">
      <div class="label"><span style="color:#d7000f;">*</span>接口地址：</div>
      <div class="input">
        <el-input type="text" v-model="url" placeholder="请输入API地址"></el-input>
        <div style="width: 100%;">
          <el-button type="text" @click="demo('get')">Get示例(查询)</el-button>
          <el-button type="text" @click="demo('post')">Post示例(新增)</el-button>
          <el-button type="text" @click="demo('put')">Put示例(修改)</el-button>
          <el-button type="text" @click="demo('delete')">Delete示例(删除)</el-button>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="label">请求头(Header)：</div>
      <div class="input">
        <div v-for="(item, index) in headers" :key="index" class="input-item">
          <el-checkbox v-model="item.select" class="select"></el-checkbox>
          <el-input type="text" v-model="item.key" @input="changeHeader(item, index)"
                    placeholder="Content-Type" class="key">
            <template slot="prepend">KEY</template>
          </el-input>
          <el-input type="text" v-model="item.value" placeholder="text/json;charset=utf-8" class="value">
            <template slot="prepend">VALUE</template>
          </el-input>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="label"><span style="color:#d7000f;">*</span>请求方式：</div>
      <div class="input">
        <el-radio v-model="method" label="GET">GET</el-radio>
        <el-radio v-model="method" label="POST">POST</el-radio>
        <el-radio v-model="method" label="PUT">PUT</el-radio>
        <el-radio v-model="method" label="DELETE">DELETE</el-radio>
      </div>
    </div>
    <div class="row" v-if="method==='POST'||method==='PUT'||method==='DELETE'">
      <div class="label">参数类型：</div>
      <div class="input">
        <el-radio v-model="dataType" label="json">json</el-radio>
        <el-radio v-model="dataType" label="json-str">json-str</el-radio>
        <el-radio v-model="dataType" label="x-www-form-urlencoded">x-www-form-urlencoded</el-radio>
        <el-radio v-model="dataType" label="form-data">form-data</el-radio>
        <el-radio v-model="dataType" label="text">text</el-radio>
        <div style="font-size: 12px;color: #d7000f;letter-spacing: 1px;">{{ dataTypeInfo[dataType] }}</div>
      </div>
    </div>
    <div class="row" v-if="method==='POST'||method==='PUT'||method==='DELETE'">
      <div class="label">请求体(Body)：</div>
      <div class="input" v-if="dataType==='json'||dataType==='x-www-form-urlencoded'">
        <div v-for="(item, index) in bodys" :key="index" class="input-item">
          <el-checkbox v-model="item.select" class="select"></el-checkbox>
          <el-input type="text" v-model="item.key" @input="changeBody(item, index)"
                    placeholder="参数名" class="key">
            <template slot="prepend">参数名</template>
          </el-input>
          <el-input type="text" v-model="item.value" placeholder="参数值" class="value">
            <template slot="prepend">参数值</template>
          </el-input>
        </div>
      </div>
      <div class="input" v-if="dataType==='form-data'">
        <input type="file" ref="myfile" name="myfile" class="myfile"/>
      </div>
      <div class="input" v-if="dataType==='text'||dataType==='json-str'">
        <textarea v-model="bodyText" class="el-input__inner"
                  style="text-align: left;letter-spacing: 0px;width: 100%;height: 100px;"></textarea>
      </div>
    </div>
    <div class="row">
      <div class="label mobileHidden"></div>
      <div class="input">
        <el-button type="primary" @click="sendRequest" icon="el-icon-plus" round>发送请求</el-button>
        <el-button icon="el-icon-refresh" round @click="resetForm">重置</el-button>
        <el-select placeholder="历史请求记录（永久保存；清空缓存可清理；重复请求会覆盖上次）"
                   filterable clearable class="history" v-model="historyUrl" @change="changeHistoryUrl">
          <el-option :value="item"
                     v-for="(item, index) in historyUrlList" :key="index">
            <span class="historyContent" :title="item">{{ item }}</span>
            <el-button type="text" icon="el-icon-delete" @click="removeHistory(item)"
                       class="historyDelete" title="删除"></el-button>
          </el-option>
        </el-select>
      </div>
    </div>
    <el-divider></el-divider>
    <div style="margin-top: 20px;letter-spacing: 0px;">
      <div v-if="error">
        <div style="color: #d7000f;margin: 10px 0px;">请求错误，错误信息：</div>
        <json-viewer v-if="error.isJson" :value="error.value" :expand-depth="2" boxed sort></json-viewer>
        <div v-else style="color: #d7000f;margin: 10px 0px;">{{ error.value }}</div>
      </div>
      <div v-if="data">
        <div style="color: #2C7EEA;margin: 10px 0px;">请求成功，响应信息：</div>
        <json-viewer v-if="data.isJson" :value="data.value||'返回值为null'"
                     :expand-depth="2" boxed sort></json-viewer>
        <p v-else style="color: #2C7EEA;margin: 10px 0px;">{{ data.value }}</p>
      </div>
    </div>
  </div>
</template>
<script>
import JsonViewer from 'vue-json-viewer'

export default {
  components: {JsonViewer},
  data() {
    return {
      url: '',
      method: 'GET',
      headers: [{select: false, key: '', value: ''}],
      bodys: [{select: false, key: '', value: ''}],
      bodyText: '',
      dataType: 'json',
      dataTypeInfo: {
        'json': 'JSON格式参数，在下方body中填写，会自动转换，示例：{"name": "John", "age": 30}',
        'json-str': 'JSON格式参数字符串，示例：{"name": "John", "age": 30}',
        'form-data': '表单格式参数，主要用于文件上传',
        'x-www-form-urlencoded': '普通键值对数据，在下方body中填写，会自动转换，示例：name=John&age=30',
        'text': '文本类型参数，内容自定义'
      },
      dataTypeContent: {
        'json': 'application/json',
        'json-str': 'application/json',
        'x-www-form-urlencoded': 'application/x-www-form-urlencoded',
        'form-data': 'multipart/form-data',
        'text': 'text/plain'
      },
      error: null,
      data: null,
      historyUrl: '',
      historyUrlList: [],
    }
  },
  created() {
    this.loadHistoryFromCookie()
  },
  methods: {
    // 发送请求
    sendRequest() {
      if (this.url === '') {
        this.$message({type: 'error', message: '请求地址不能为空'})
        return
      }
      this.saveHistory();
      // 处理请求头，返回一个{key:value}格式的对象
      let headerParams = {}
      this.headers.filter(item => item.select && item.key).forEach(item => {
        headerParams[item.key] = item.value
      })
      this.data = this.error = null;
      // 发送请求
      if (this.method === 'GET') {
        this.$request({url: this.url, method: 'get', headers: headerParams}).then((response) => {
          this.data = this.checkJsonType(response.data)
        }).catch(error => {
          this.error = this.checkJsonType(error)
        })
      } else {
        // 处理body
        let bodyParams, data;
        if (this.dataType === 'json') {
          bodyParams = {}
          this.bodys.filter(item => item.select && item.key).forEach(item => {
            bodyParams[item.key] = item.value
          })
          data = {...bodyParams}
        } else if (this.dataType === 'json-str') {
          bodyParams = JSON.parse(this.bodyText)
          data = bodyParams
        } else if (this.dataType === 'x-www-form-urlencoded') {
          let params = new URLSearchParams();
          this.bodys.filter(item => item.select && item.key).forEach(item => {
            params.append(item.key, item.value)
          })
          bodyParams = params.toString()
          data = {...bodyParams}
        } else if (this.dataType === 'form-data') {
          bodyParams = new FormData();
          // 把myfile文件，循环放入bodyParams中
          console.log(this.$refs.myfile.files.length)
          if (this.$refs.myfile.files.length > 0) {
            for (let i = 0; i < this.$refs.myfile.files.length; i++) {
              bodyParams.append('myfile' + i, this.$refs.myfile.files[i])
            }
          }
          data = {...bodyParams}
        } else if (this.dataType === 'text') {
          data = this.bodyText
        }
        // 请求头增加文件类型
        headerParams['Content-Type'] = this.dataTypeContent[this.dataType]
        if (this.method === 'POST') {
          this.$request({url: this.url, method: 'post', data, headers: headerParams}).then((response) => {
            this.data = this.checkJsonType(response.data)
          }).catch((error) => {
            this.error = this.checkJsonType(error)
          })
        } else if (this.method === 'PUT') {
          this.$request({url: this.url, method: 'put', data, headers: headerParams}).then((response) => {
            this.data = this.checkJsonType(response.data)
          }).catch(error => {
            this.error = this.checkJsonType(error)
          })
        } else if (this.method === 'DELETE') {
          this.$request({url: this.url, method: 'delete', data, headers: headerParams}).then((response) => {
            this.data = this.checkJsonType(response.data)
          }).catch(error => {
            this.error = this.checkJsonType(error)
          })
        }
      }
    },
    // 重置
    resetForm() {
      this.url = ''
      this.method = 'GET'
      this.headers = [{select: false, key: '', value: ''}]
      this.bodys = [{select: false, key: '', value: ''}]
      this.bodyText = this.historyUrl = ''
      this.data = this.error = null
    },
    // 处理header变化
    changeHeader(item, index) {
      this.headers[index].select = (item.key.trim() !== '')
      if (index === this.headers.length - 1) {
        // 最后一行输入内容，则追加一空行
        this.headers.push({select: false, key: '', value: ''})
      }
    },
    // 处理body变化
    changeBody(item, index) {
      this.bodys[index].select = (item.key.trim() !== '')
      if (index === this.bodys.length - 1) {
        // 最后一行输入内容则追加一空行
        this.bodys.push({select: false, key: '', value: ''})
      }
    },
    checkJsonType(str) {
      try {
        const value1 = JSON.parse(str);
        return {isJson: true, value: value1};
      } catch (error) {
        try {
          const value2 = JSON.parse(JSON.stringify(str));
          return {isJson: true, value: value2};
        } catch (error) {
          return {isJson: false, value: str};
        }
      }
    },
    demo(method) {
      if (method === 'get') {
        this.url = '/userTheme/zUserTheme/list?page=1&limit=10'
        this.method = 'GET'
      } else if (method === 'delete') {
        this.url = '/userTheme/zUserTheme/delete'
        this.method = 'DELETE'
        this.dataType = 'json-str'
        this.bodyText = JSON.stringify(new Array('999'))
      } else if (method === 'post') {
        this.url = '/userTheme/zUserTheme/add'
        this.method = 'POST'
        this.dataType = 'json'
        this.bodys = [
          {select: true, key: 'themeId', value: '999'},
          {select: true, key: 'userId', value: '999'},
          {select: true, key: 'themeJson', value: '999'}
        ]
      } else if (method === 'put') {
        this.url = '/userTheme/zUserTheme/update'
        this.method = 'PUT'
        this.dataType = 'json'
        this.bodys = [
          {select: true, key: 'themeId', value: '999'},
          {select: true, key: 'userId', value: '999'},
          {select: true, key: 'themeJson', value: '999'}
        ]
      }
    },
    saveHistory() {
      const key = '[' + this.method + ']' + this.url;
      const value = {
        url: this.url,
        method: this.method,
        headers: this.headers,
        bodys: this.bodys,
        bodyText: this.bodyText,
        dataType: this.dataType
      };
      localStorage.setItem(key, JSON.stringify(value));// 保存新的key
      this.historyUrlList.push(key);// 添加上新key
      this.historyUrlList = [...new Set(this.historyUrlList)]// 去重
      localStorage.setItem(this.$storageKeys.historyKeys, JSON.stringify(this.historyUrlList));
    },
    loadHistoryFromCookie() {
      this.historyUrl = ''
      const allKeys = localStorage.getItem(this.$storageKeys.historyKeys);
      if (allKeys) {
        this.historyUrlList = JSON.parse(allKeys);
      }
    },
    removeHistory(key) {
      this.$confirm('确定要删除吗?', '删除提醒', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        localStorage.removeItem(key);
        this.historyUrlList = this.historyUrlList.filter(item => item !== key);
        localStorage.setItem(this.$storageKeys.historyKeys, JSON.stringify(this.historyUrlList));
      })
    },
    changeHistoryUrl(url) {
      let value = localStorage.getItem(url);
      if (value) {
        value = JSON.parse(value);
        this.url = value.url;
        this.method = value.method;
        this.headers = value.headers;
        this.bodys = value.bodys;
        this.bodyText = value.bodyText;
        this.dataType = value.dataType;
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.el-checkbox__inner {
  width: 28px !important;
  height: 28px !important;
  letter-spacing: 0px;

  &:after {
    top: 3px;
    left: 9px;
    width: 6px;
    height: 14px;
  }
}
</style>
<style scoped lang="scss">
.postman1 {
  overflow: auto;

  .row {
    line-height: 50px;
    margin-bottom: 10px;
    border: 1px dashed #eeeeee;
    border-radius: 10px;

    .label {
      margin-right: 10px;
      width: 200px;
      text-align: right;
      display: inline-block;
      vertical-align: top;
      @media screen and (max-width: 768px) {
        /* 手机端 */
        width: 100%;
        margin-right: 0px;
        text-align: left;
      }
    }

    @media screen and (max-width: 768px) {
      /* 手机端 */
      .mobileHidden {
        display: none;
      }
    }

    .input {
      display: inline-block;
      width: calc(100% - 270px);
      @media screen and (max-width: 768px) {
        /* 手机端 */
        width: 100%;
      }

      .input-item {
        .key {
          width: 30%;
          margin: auto 10px;

          @media screen and (max-width: 768px) {
            /* 手机端 */
            width: calc(100% - 38px);
            margin-right: 0px;
          }
        }

        .value {
          width: calc(70% - 48px);
          @media screen and (max-width: 768px) {
            /* 手机端 */
            width: 100%;
          }
        }
      }

      .myfile {
        width: 100%;
        color: #606266;
        border: 1px solid #c0c4cc;
        border-radius: 4px;
        padding: 5px 15px;
      }

      .history {
        width: calc(100% - 260px);
        float: right;
        @media screen and (max-width: 768px) {
          /* 手机端 */
          width: 100%;
        }
      }
    }
  }
}

.historyContent {
  overflow: hidden;
  display: inline-block;
  max-width: 800px;
  @media screen and (max-width: 768px) {
    /* 手机端 */
    max-width: 300px;
  }
}

.historyDelete {
  float: left;
  margin-right: 10px;
  color: #d7000f;
}
</style>
