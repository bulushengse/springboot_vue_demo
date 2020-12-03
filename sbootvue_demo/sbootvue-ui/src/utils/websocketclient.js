
import { MessageBox } from 'element-ui'
import store from '@/store'

var ws

export function webSocketOpen(token) {

  if(typeof(window.WebSocket) == "undefined") {
    console.log("您的浏览器不支持WebSocket");
  }else{
    ws = new WebSocket("ws://"+process.env.VUE_APP_WEBSOCKET_IP+":"+process.env.VUE_APP_WEBSOCKET_PORT+"/websocket");

    ws.onopen = function() {
      ws.send("[join]"+token);
    };

    ws.onmessage = function(msg) {
      var obj = JSON.parse(msg.data)
      if(obj.type == 'getOut'){
        store.dispatch('LogOut').then(() => {
          MessageBox.alert('您的账号在其他设备登录，请确认密码安全！','系统提示',{
            confirmButtonText: '重新登录',
            callback: () => {
              location.reload()
            }
          })
        })
      }else if(obj.type == 'goOut'){
        store.dispatch('LogOut').then(() => {
          MessageBox.alert('您的账号被管理员强制下线，请与管理员联系！','系统提示',{
            confirmButtonText: '重新登录',
            callback: () => {
              location.reload()
            }
          })
        })
      }
    };

    ws.onclose = function() {
      console.log("WebSocket Connection closed.");

    };

    ws.onerror = function() {
      console.log("WebSocket Connection error");

    };
  }
}

export function webSocketSend(type,token) {
  ws.send("["+type+"]"+token);
}
