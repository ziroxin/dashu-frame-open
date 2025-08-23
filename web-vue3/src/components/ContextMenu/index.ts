import ContextMenu from './src/ContextMenu'
import { ElDropdown } from 'element-plus'
import type { RouteLocationNormalizedLoaded } from 'vue-router'

export interface ContextMenuExpose {
  elDropdownMenuRef: ComponentRef<typeof ElDropdown>
  tagItem: RouteLocationNormalizedLoaded
}

export { ContextMenu }
