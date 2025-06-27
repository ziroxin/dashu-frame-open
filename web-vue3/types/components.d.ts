declare module 'vue' {
  export interface GlobalComponents {
    MyIcon: (typeof import('../src/components/MyIcon/index'))['MyIcon']
    Permission: (typeof import('../src/components/Permission/index'))['Permission']
    BaseButton: (typeof import('../src/components/Button/index'))['BaseButton']
  }
}

export {}
