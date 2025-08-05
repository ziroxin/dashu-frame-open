declare module 'vue' {
  export interface GlobalComponents {
    MyIcon: (typeof import('../src/components/MyIcon/index'))['MyIcon']
    BaseButton: (typeof import('../src/components/BaseButton/index'))['BaseButton']
  }
}

export {}
