/**
 * 一个标准的组件模板
 * @param config
 * @constructor
 */
function T(config) {
    // 初始化方法
    this.init = (config) => {
        this.handler = document.getElementById(config.handler); // 控制按钮
        this.target = document.getElementById(config.target);   // 目标：点击控制按钮后，会把target里的所有内容发送改变
        this.baseURL = config.baseURL;                          // 用户后台的接口

        this.handler.addEventListener("click", this.render);
        this.domRootDiv = document.createElement("div");

        this.xxxx_url = "/xxxx";

    }

    // 子渲染方法：
    this._renderXxx = () =>{
        this.domRootDiv.innerHTML = "hello world";
    }

    // 渲染方法
    this.render =(e) => {
        this.target.innerHTML = ""
        this._renderXxx();
        this.target.appendChild(this.domRootDiv);
    }

    // 初始化方法调用
    this.init(config);
}