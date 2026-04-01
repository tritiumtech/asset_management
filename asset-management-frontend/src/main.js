import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'

// Vant 组件库
import { 
  Button, 
  Cell, 
  CellGroup, 
  NavBar, 
  Tabbar, 
  TabbarItem,
  Search,
  List,
  PullRefresh,
  Toast,
  Dialog,
  ActionSheet,
  Field,
  Form,
  Picker,
  Popup,
  DatePicker,
  TimePicker,
  Icon,
  Tag,
  Card,
  Empty,
  Loading,
  Skeleton,
  Image,
  Tabs,
  Tab,
  Badge,
  Grid,
  GridItem,
  NoticeBar,
  SwipeCell,
  Steps,
  Step,
  Progress,
  Col,
  Row
} from 'vant'

// Vant 样式
import 'vant/lib/index.css'

const app = createApp(App)

// 注册 Vant 组件
const components = [
  Button, Cell, CellGroup, NavBar, Tabbar, TabbarItem,
  Search, List, PullRefresh, Toast, Dialog, ActionSheet,
  Field, Form, Picker, Popup, DatePicker, TimePicker, Icon, Tag,
  Card, Empty, Loading, Skeleton, Image, Tabs, Tab, Badge,
  Grid, GridItem, NoticeBar, SwipeCell, Steps, Step, Progress,
  Col, Row
]

components.forEach(component => {
  app.use(component)
})

app.use(createPinia())
app.use(router)

app.mount('#app')