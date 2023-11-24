function Login(config) {
    // 初始化方法
    this.init = (config) => {
        this.handler = document.getElementById(config.handler); // 控制按钮
        this.target = document.getElementById(config.target);   // 目标：点击控制按钮后，会把target里的所有内容发送改变
        this.baseURL = config.baseURL;                          // 用户后台的接口

        this.login_url = "/auth/login";

        this.loginButton = document.createElement("button");
        this.loginButton.innerText = "登录";
        this.loginButton.onclick = this.login;
        this.handler.addEventListener("click", this.render);
    }

    // 渲染方法
    this.render =(e) => {
        this.target.innerHTML =
            "<label>用户名：</label>" +
            "<input id=\"username\" type=\"text\" value='nihil'>" +
            "<br />" +
            "<label>密码：</label>" +
            "<input id=\"password\" type=\"password\" value='123456'>" +
            "<br />";
        this.target.appendChild(this.loginButton);
    }

    // 用户登录方法
    this.login = () => {
        let username = document.getElementById("username").value;
        let password = document.getElementById("password").value;

        url = this.baseURL + this.login_url
        headers = {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
        body = `username=${username}&password=${password}`
        fetch(url, {method:'POST', headers, body}).then(res=>res.json()).then(res=>{
            if(res.data){
                localStorage.setItem("token", res.data)
                console.log("用户登录成功")
            }
        })

    }

    // 初始化方法调用
    this.init(config);
}