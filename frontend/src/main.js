import Vue from 'vue'
import ElementUI from 'element-ui'
import './styles/element-variables.scss'
import './styles/global.scss'
import App from './App.vue'
import router from './router'
// 默认组件尺寸用 small，和参考系统的紧凑密度一致
Vue.use(ElementUI, { size: 'small' })
new Vue({ router, render: h => h(App) }).$mount('#app')
