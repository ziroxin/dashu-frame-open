/**
 * 集成2个图床：
 * 1. 国外开放图床，商用无限制
 *    官网：https://unsplash.com/
 *    接口：https://api.unsplash.com/search/photos?query=&page=1&per_page=10&orientation=landscape
 *    Header: {Authorization: 'Client-ID LsH8s8dZyEFCVtaiiZE6KmJ_ZbvPO1i4MPCtItk6GKE'}
 *      参数说明：query=查询关键字；page=页码；per_page=每页条数；orientation=方向（landscape=横屏；portrait=竖屏； squarish=方形）
 *      Header参数说明：{Authorization: Client-ID YOUR_ACCESS_KEY}（其中YOUR_ACCESS_KEY，需要去官网注册开发者账号，并创建应用后得到）
 *      api文档详见：https://unsplash.com/documentation#search-photos
 * 2. 国内图床壁纸，不可商用
 *    官网：http://adesk.com/
 *    1.1 分类接口：http://service.picasso.adesk.com/v1/wallpaper/category
 *      说明：方向【vertical=竖屏;wallpaper=横屏】，GET，返回值json格式{msg:'success','res':{category:[count:50741, name: '分类名称', cover: '缩率图', id: '分类ID']}}
 *    1.2 图片列表接口：http://service.picasso.adesk.com/v1/wallpaper/category/4e4d610cdf714d2966000000/wallpaper?limit=30&skip=0&adult=true&first=0&order=new
 *      说明：/v1/方向/category/分类ID/方向?limit=查询几张图&skip=从第几张开始&adult=true&first=0&order=类型（new=最新;hot=最热）
 */
import axios from 'axios'

/**
 * 查询unsplash图片
 * （默认查询：关键字[cityscape]的图片）
 * @param query 关键字
 * @param page 页码
 * @param limit 每页条数
 * @param orientation 方向（landscape=横屏；portrait=竖屏； squarish=方形）
 */
export function imageUpsplash(query, page, limit, orientation) {
  return new Promise(async (resolve, reject) => {
    axios({
      url: 'https://api.unsplash.com/search/photos?query=' + (query || 'cityscape') +
        '&page=' + (page || 1) + '&per_page=' + (limit || 10) + '&orientation=' + (orientation || 'landscape'),
      method: 'get', headers: {Authorization: 'Client-ID LsH8s8dZyEFCVtaiiZE6KmJ_ZbvPO1i4MPCtItk6GKE'}
    }).then((response) => {
      console.log('imageUpsplash', response.data.results)
      resolve(response.data.results)
    })
  })
}

/**
 * 获取adesk横屏壁纸分类
 */
export function imageAdeskWallpaperType() {
  return new Promise(async (resolve, reject) => {
    axios({
      url: 'http://service.picasso.adesk.com/v1/wallpaper/category', method: 'get'
    }).then((response) => {
      console.log('imageAdeskWallpaperType', response.data.res.category)
      resolve(response.data.res.category)
    })
  })
}

/**
 * 获取adesk竖屏壁纸分类
 */
export function imageAdeskVerticalType() {
  return new Promise(async (resolve, reject) => {
    axios({
      url: 'http://service.picasso.adesk.com/v1/vertical/category', method: 'get'
    }).then((response) => {
      console.log('imageAdeskVerticalType', response.data.res.category)
      resolve(response.data.res.category)
    })
  })
}

/**
 * 获取adesk 横屏 图片列表
 * （默认查询：[城市]分类，前[20]条，[最新]图片）
 * @param typeId 分类ID
 * @param order 类型（new=最新;hot=最热）
 * @param limit 查询几条
 * @param skip 从第几条开始
 */
export function imageAdeskWallpaper(typeId, order, limit, skip) {
  return new Promise((resolve, reject) => {
    axios({
      url: 'http://service.picasso.adesk.com/v1/wallpaper/category/' + (typeId || '4fb47a195ba1c60ca5000222') + '/wallpaper' +
        '?limit=' + (limit || 20) + '&skip=' + (skip || 0) + '&adult=true&first=0&order=' + (order || 'hot'),
      method: 'get'
    }).then((response) => {
      resolve(response.data.res.wallpaper);
    })
  });
}

/**
 * 获取adesk 竖屏 图片列表
 * （默认查询：[城市]分类，前[20]条，[最新]图片）
 * @param typeId 分类ID
 * @param order 类型（new=最新;hot=最热）
 * @param limit 查询几条
 * @param skip 从第几条开始
 */
export function imageAdeskVertical(typeId, order, limit, skip) {
  return new Promise(async (resolve, reject) => {
    axios({
      url: 'http://service.picasso.adesk.com/v1/vertical/category/' + (typeId || '4fb47a305ba1c60ca5000223') + '/vertical' +
        '?limit=' + (limit || 20) + '&skip=' + (skip || 0) + '&adult=true&first=0&order=' + (order || 'hot'),
      method: 'get'
    }).then((response) => {
      resolve(response.data.res.vertical)
    })
  })
}