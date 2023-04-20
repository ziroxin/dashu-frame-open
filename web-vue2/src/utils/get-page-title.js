import defaultSettings from '@/settings'

const title = defaultSettings.title || '大树快速开发平台'

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
