<template>
  <el-row :gutter="20" justify="space-between" :class="prefixCls">
    <el-col :xl="6" :lg="6" :md="12" :sm="12" :xs="24">
      <el-card shadow="hover" class="mb-20px">
        <el-skeleton :loading="isLoading" animated :rows="2">
          <template #default>
            <div :class="`${prefixCls}__item flex justify-between`">
              <div>
                <div :class="`${prefixCls}__item--icon ${prefixCls}__item--peoples p-16px inline-block rounded-6px`">
                  <my-icon icon="svg-peoples" :size="40"/>
                </div>
              </div>
              <div class="flex flex-col justify-between">
                <div :class="`${prefixCls}__item--text text-16px text-gray-500 text-right`">
                  {{ t('analysis.newUser') }}
                </div>
                <count-to class="text-20px font-700 text-right" :start-val="0" :end-val="totalState.users"
                          :duration="2600"/>
              </div>
            </div>
          </template>
        </el-skeleton>
      </el-card>
    </el-col>

    <el-col :xl="6" :lg="6" :md="12" :sm="12" :xs="24">
      <el-card shadow="hover" class="mb-20px">
        <el-skeleton :loading="isLoading" animated :rows="2">
          <template #default>
            <div :class="`${prefixCls}__item flex justify-between`">
              <div>
                <div :class="`${prefixCls}__item--icon ${prefixCls}__item--message p-16px inline-block rounded-6px`">
                  <my-icon icon="svg-message" :size="40"/>
                </div>
              </div>
              <div class="flex flex-col justify-between">
                <div :class="`${prefixCls}__item--text text-16px text-gray-500 text-right`">
                  {{ t('analysis.unreadInformation') }}
                </div>
                <count-to class="text-20px font-700 text-right" :start-val="0" :end-val="totalState.messages"
                          :duration="2600"/>
              </div>
            </div>
          </template>
        </el-skeleton>
      </el-card>
    </el-col>

    <el-col :xl="6" :lg="6" :md="12" :sm="12" :xs="24">
      <el-card shadow="hover" class="mb-20px">
        <el-skeleton :loading="isLoading" animated :rows="2">
          <template #default>
            <div :class="`${prefixCls}__item flex justify-between`">
              <div>
                <div :class="`${prefixCls}__item--icon ${prefixCls}__item--money p-16px inline-block rounded-6px`">
                  <my-icon icon="svg-money" :size="40"/>
                </div>
              </div>
              <div class="flex flex-col justify-between">
                <div :class="`${prefixCls}__item--text text-16px text-gray-500 text-right`">
                  {{ t('analysis.transactionAmount') }}
                </div>
                <count-to class="text-20px font-700 text-right" :start-val="0" :end-val="totalState.moneys"
                          :duration="2600"/>
              </div>
            </div>
          </template>
        </el-skeleton>
      </el-card>
    </el-col>

    <el-col :xl="6" :lg="6" :md="12" :sm="12" :xs="24">
      <el-card shadow="hover" class="mb-20px">
        <el-skeleton :loading="isLoading" animated :rows="2">
          <template #default>
            <div :class="`${prefixCls}__item flex justify-between`">
              <div>
                <div :class="`${prefixCls}__item--icon ${prefixCls}__item--shopping p-16px inline-block rounded-6px`">
                  <my-icon icon="svg-shopping" :size="40"/>
                </div>
              </div>
              <div class="flex flex-col justify-between">
                <div :class="`${prefixCls}__item--text text-16px text-gray-500 text-right`">
                  {{ t('analysis.totalShopping') }}
                </div>
                <count-to class="text-20px font-700 text-right" :start-val="0" :end-val="totalState.shoppings"
                          :duration="2600"/>
              </div>
            </div>
          </template>
        </el-skeleton>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { CountTo } from '@/components/CountTo'
import { useDesign } from '@/hooks/web/useDesign'
import { useI18n } from '@/hooks/web/useI18n'
// 国际化
const {t} = useI18n()
const prefixCls = useDesign().getPrefixCls('panel')

// 数据
const totalState = ref({users: 102400, messages: 81212, moneys: 9280, shoppings: 13600})
// 是否加载中
const isLoading = ref(true)
setTimeout(() => {
  isLoading.value = false// 模拟延迟加载数据
}, 1000)
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-panel';

.@{prefix-cls} {
  &__item {
    &--peoples {
      color: #40c9c6;
    }

    &--message {
      color: #36a3f7;
    }

    &--money {
      color: #f4516c;
    }

    &--shopping {
      color: #34bfa3;
    }

    &:hover {
      :deep(.@{adminNamespace}-icon) {
        color: #fff !important;
      }
      .@{prefix-cls}__item--icon {
        transition: all 0.38s ease-out;
      }
      .@{prefix-cls}__item--peoples {
        background: #40c9c6;
      }
      .@{prefix-cls}__item--message {
        background: #36a3f7;
      }
      .@{prefix-cls}__item--money {
        background: #f4516c;
      }
      .@{prefix-cls}__item--shopping {
        background: #34bfa3;
      }
    }
  }
}
</style>
