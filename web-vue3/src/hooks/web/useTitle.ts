import { watch, ref } from 'vue'
import { isString } from '@/utils/is'
import { useAppStoreWithOut } from '@/store/modules/app'
import { useI18n } from '@/hooks/web/useI18n'

export const useTitle = (newTitle?: string) => {
  const {t} = useI18n()
  const appStore = useAppStoreWithOut()
  const title = ref(newTitle ? `${t(newTitle as string)} - ${appStore.getTitle}` : appStore.getTitle)

  watch(title, (n, o) => {
    if (isString(n) && n !== o && document) {
      document.title = n
    }
  }, {immediate: true})
  
  return title
}
