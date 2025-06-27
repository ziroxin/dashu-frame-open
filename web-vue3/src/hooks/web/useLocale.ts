import { i18n } from '@/plugins/vueI18n'
import { useLocaleStoreWithOut } from '@/store/modules/locale'
import { setHtmlPageLang } from '@/plugins/vueI18n/helper'

const setI18nLanguage = (locale: LocaleType) => {
  const localeStore = useLocaleStoreWithOut()
  if (i18n.mode === 'legacy') {
    i18n.global.locale = locale
  } else {
    (i18n.global.locale as any).value = locale
  }
  localeStore.setCurrentLocale({lang: locale})
  setHtmlPageLang(locale)
}

export const useLocale = () => {
  // 切换语言（更改useI18n的locale设置）
  const changeLocale = async (locale: LocaleType) => {
    const globalI18n = i18n.global
    // 支持远程读取语言数据（在这里改成api请求即可）
    const langModule = await import(`../../locales/${locale}.ts`)
    globalI18n.setLocaleMessage(locale, langModule.default)
    setI18nLanguage(locale)
  }

  return {
    changeLocale
  }
}
