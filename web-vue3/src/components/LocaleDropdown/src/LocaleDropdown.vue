<template>
  <el-dropdown :class="prefixCls" trigger="click" @command="setLang">
    <my-icon :size="18" icon="vi-ion:language-sharp" class="cursor-pointer !p-0" :class="$attrs.class" :color="color"/>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item v-for="item in langMap" :key="item.lang" :command="item.lang">
          {{ item.name }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { useLocaleStore } from '@/store/modules/locale'
import { useLocale } from '@/hooks/web/useLocale'
import { propTypes } from '@/utils/propTypes'
import { useDesign } from '@/hooks/web/useDesign'

const prefixCls = useDesign().getPrefixCls('locale-dropdown')

defineProps({color: propTypes.string.def('')})

const localeStore = useLocaleStore()
const langMap = computed(() => localeStore.getLocaleMap)
const currentLang = computed(() => localeStore.getCurrentLocale)
const setLang = (lang: LocaleType) => {
  if (lang === unref(currentLang).lang) {
    return
  }
  localeStore.setCurrentLocale({lang})
  useLocale().changeLocale(lang)
  // 需要重新加载页面让整个语言多初始化
  window.location.reload()
}
</script>
