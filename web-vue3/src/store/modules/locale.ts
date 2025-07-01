import { defineStore } from 'pinia'
import { store } from '../index'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
import { LocaleDropdownType } from '@/components/LocaleDropdown'
import storageKeys from '@/utils/storage-keys'

const elLocaleMap = {'zh-CN': zhCn, en: en}

interface LocaleState {
  currentLocale: LocaleDropdownType
  localeMap: LocaleDropdownType[]
}

export const useLocaleStore = defineStore('locales', {
  state: (): LocaleState => {
    const currentLang: any = localStorage.getItem(storageKeys.l_lang) || 'zh-CN'
    return {
      currentLocale: {
        lang: currentLang,
        elLocale: elLocaleMap[currentLang]
      },
      // 多语言
      localeMap: [{lang: 'zh-CN', name: '简体中文'}, {lang: 'en', name: 'English'}]
    }
  },
  getters: {
    getCurrentLocale(): LocaleDropdownType {
      return this.currentLocale
    },
    getLocaleMap(): LocaleDropdownType[] {
      return this.localeMap
    }
  },
  actions: {
    setCurrentLocale(localeMap: LocaleDropdownType) {
      this.currentLocale.lang = localeMap?.lang
      this.currentLocale.elLocale = elLocaleMap[localeMap?.lang]
      localStorage.setItem(storageKeys.l_lang, localeMap?.lang)
    }
  }
})

export const useLocaleStoreWithOut = () => {
  return useLocaleStore(store)
}
