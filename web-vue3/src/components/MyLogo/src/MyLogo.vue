<template>
  <div>
    <router-link to="/"
                 :class="[prefixCls,layout!=='classic'?`${prefixCls}__Top`:'',
                          'flex !h-[var(--logo-height)] items-center cursor-pointer pl-8px relative decoration-none overflow-hidden']">
      <img src="@/assets/imgs/logo.png" class="w-[calc(var(--logo-height)-10px)] h-[calc(var(--logo-height)-10px)]"/>
      <div v-if="show"
           :class="['ml-10px text-16px font-700',
                   layout==='classic'?'text-[var(--logo-title-text-color)]':'text-[var(--top-header-text-color)]']">
        {{ title }}
      </div>
    </router-link>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/store/modules/app'
import { useDesign } from '@/hooks/web/useDesign'

const prefixCls = useDesign().getPrefixCls('logo')
const appStore = useAppStore()
const show = ref(true)
const title = computed(() => appStore.getTitle)
const layout = computed(() => appStore.getLayout)
const collapse = computed(() => appStore.getCollapse)

onMounted(() => { if (unref(collapse)) show.value = false})
watch(() => collapse.value, (collapse: boolean) => {
  show.value = (unref(layout) === 'topLeft' || unref(layout) === 'cutMenu') ? true : !collapse
})
watch(() => layout.value, (layout) => {
  show.value = (layout === 'top' || layout === 'cutMenu') ? true : !unref(collapse)
})
</script>
