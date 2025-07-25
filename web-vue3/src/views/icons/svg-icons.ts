/**
 * 扫描svg目录下所有svg文件
 */
const svgs = import.meta.glob('../../assets/svgs/*.svg')
const svgIcons = []
Object.keys(svgs).forEach(name => {
  const svg = name.replace('../../assets/svgs/', '').replace(/(\.\/|\.svg)/g, '')
  svgIcons.push(svg)
})
export default svgIcons