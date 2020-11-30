const {app, BrowserWindow,Menu,MenuItem, ipcMain} = require('electron')
const url = require('url')
const path = require('path')
const keytar = require('keytar')


let win

function createWindow() {
   win = new BrowserWindow({
       width: 800, 
       height: 600,
       webPreferences: {
           nodeIntegration: true
       }
    })
   win.loadURL(url.format ({
      pathname: path.join(__dirname, 'index.html'),
      protocol: 'file:',
      slashes: true
   }))

   win.webContents.openDevTools()
}

app.on('ready', createWindow)

ipcMain.on('get-credentials', (event) => {
    event.returnValue = keytar.findCredentials('HomeControllerService')
})