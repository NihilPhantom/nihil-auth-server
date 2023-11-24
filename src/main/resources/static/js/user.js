function User(config) {
    // 初始化方法
    this.init = (config) => {
        this.handler = document.getElementById(config.handler); // 控制按钮
        this.target = document.getElementById(config.target);   // 目标：点击控制按钮后，会把target里的所有内容发送改变
        this.baseURL = config.baseURL;                          // 用户后台的接口

        this.handler.addEventListener("click", this.render);
        this.domRootDiv = document.createElement("div");

        this.user_list_url = "/user/list";
        this.user_role_and_res_url = "/auth/role_and_res";
        this.res_inDB_url = "/res/resIndatabase";
        this.list_role_url = "/role/list";

        this.currentUserPage = 1;       // 当前用户查询页数
        this.pageSize = 50;             // 当前用户查询每页数据数量
        this.userList = []              // 存放用户列表
        this.selectUserId = -1;         // 当前正在操作的用户Id
        this.roleList = [];             // 存放【角色列表查询】功能列表
        this.resInDatabase = [];        // 存放数据库中的所有资源
        this.userRoleIdList = [];         // 用户拥有的角色
        this.userResIdList  = [];         // 用户拥有的资源


        let title1 = document.createElement("h3");
        title1.innerText = "用户列表";

        let title2 = document.createElement("h3");
        title2.innerText = "用户资源";

        this.domInputCurrentPage = document.createElement("input");
        this.domInputCurrentPage.defaultValue = this.currentUserPage;

        this.domInputPageSize = document.createElement("input");
        this.domInputPageSize.defaultValue = this.pageSize;

        let domUserSearchBotton = document.createElement("button")
        domUserSearchBotton.innerText = "查询"
        domUserSearchBotton.onclick = this.getUserList;

        let domChangeAuthorityButton = document.createElement("button")
        domChangeAuthorityButton.innerText = "确认更新"
        domChangeAuthorityButton.onclick = this.changeUserAuthority;

        this.domUserTable = document.createElement("table");

        this.domUserAuthorityDiv = document.createElement("div")


        this.domRootDiv.appendChild(title1);
        let p1 =document.createElement("p");
        let p1_span1 = document.createElement("span");
        let p1_span2 = document.createElement("span");
        p1_span1.innerText = "页数:"
        p1_span2.innerText = " page-size:"
        p1.appendChild(p1_span1)
        p1.appendChild(this.domInputCurrentPage)
        p1.appendChild(p1_span2)
        p1.appendChild(this.domInputPageSize);
        p1.appendChild(domUserSearchBotton);
        this.domRootDiv.appendChild(p1)
        this.domRootDiv.appendChild(this.domUserTable)

        this.domRootDiv.appendChild(title1);
        this.domRootDiv.appendChild(domChangeAuthorityButton);
        this.domRootDiv.appendChild(this.domUserAuthorityDiv);

        // 注册方法绑定：
        // 注册点击方法：角色展示 table 中操作按钮被点击时触发的事件
        this.domUserTable.onclick = ((e)=>{
            if(e.target.tagName === "BUTTON"){
                if(e.target.innerText == "资源管理"){
                    this.selectUserId = e.target.dataset.id;
                    this.getUserRoleRes();
                }else if(e.target.innerText == "删除"){

                }
            }
        })
    }

    // 子渲染方法：渲染用户列表
    this._renderUserTable = () =>{
        let renderString =
            "    <thead>" +
            "        <tr><td>ID值</td><td>用户名</td><td>操作</td></tr>" +
            "    </thead>" +
            "    <tbody>";

        this.userList.forEach(item=>{
            renderString +=
                "<tr>" +
                "<td>"+ item.id +"</td><td>" + item.username + "</td>" +
                "<td>" +
                "    <button data-id=\""+ item.id +"\">修改</button>" +
                "    <button data-id=\""+ item.id +"\">删除</button>" +
                "    <button data-id=\""+ item.id +"\">资源管理</button>" +
                "</td>" +
                "</tr>";
        })
        renderString +="</tbody>"
        this.domUserTable.innerHTML = renderString;
    }


    // 子渲染方法：渲染用户权限
    this._renderUserAuthority = () => {
        // 前提限制
        if(this.roleList.length ===0 || this.resInDatabase.length === 0){
            return ;
        }
        // 显示用户名
        renderText = "<p>当前用户Id：" + this.selectUserId + "</p>";
        // 显示角色列表
        this.roleList.forEach( item =>{
            let check = this.userRoleIdList.indexOf(item.id)==-1?"":"checked";
            renderText +=`<p><input type="checkbox" class="checkbox-role" data-id="${item.id}" ${check} />${item.name}</p>`
        })
        // 显示资源列表
        this.resInDatabase.forEach(item => {
            let check = this.userResIdList.indexOf(item.id)==-1?"":"checked";
            renderText += `<p><input type="checkbox" class="checkbox-res" data-id="${item.id}" ${check} />
                ${item.url}|${item.method}</p>`
        })
        this.domUserAuthorityDiv.innerHTML = renderText;
    }


    // 渲染方法
    this.render =(e) => {
        this.target.innerHTML = ""
        this.loadResInDatabase();
        this.getRoleList();
        this.target.appendChild(this.domRootDiv);
    }

    // 前后端交互方法：获取 用户列表
    this.getUserList = () => {
        let _cp = this.domInputCurrentPage.value;
        let _sz = this.domInputPageSize.value;
        fetch(this.baseURL + this.user_list_url + `?page=${_cp}&size=${_sz}`, {
            method: "GET",
            headers:{
                token: localStorage.getItem("token")||"",
            }
        }).then(res=>res.json()).then(res=>{
            this.userList = res.data
            this._renderUserTable();
        })
    }

    // 前后端交互方法：获取当前用户所拥有的 角色 和 资源
    this.getUserRoleRes = () => {
        fetch(this.baseURL + this.user_role_and_res_url + "?uid=" + this.selectUserId, {
            method: "GET",
            headers:{
                token: localStorage.getItem("token")||"",
            }
        }).then(res=>res.json()).then(res=>{
            this.userRoleIdList = res.data.roleList;
            this.userResIdList = res.data.resList;
            this._renderUserAuthority();
            console.log(res)
        })
    }

    // 前后端交互方法：加载所有 数据库 资源的方法
    this.loadResInDatabase = () => {
        fetch(this.baseURL + this.res_inDB_url, {
            method: "GET",
            headers:{ token: localStorage.getItem("token")||""}
        }).then(res=>res.json()).then(res=>{
            this.resInDatabase = res.data;
            this._renderUserAuthority();
        })
    }

    // 前后端交方法：获取角色list
    this.getRoleList = () =>{
        fetch(this.baseURL + this.list_role_url , {
            method: "GET",
            headers:{ token: localStorage.getItem("token")||""}
        }).then(res=>res.json()).then(res=>{
            this.roleList = res.data;
            this._renderUserAuthority();
        })
    }

    // 前后端交互方法：修改角色权限方法
    this.changeUserAuthority = () => {
        let checkboxRoleList = this.domUserAuthorityDiv.getElementsByClassName('checkbox-role');
        let checkboxResList = this.domUserAuthorityDiv.getElementsByClassName('checkbox-res');

       this.userRoleIdList = []
       this.userResIdList = []

        for(let i=0; i<checkboxRoleList.length; i++){
            if(checkboxRoleList[i].checked){
                this.userRoleIdList.push(checkboxRoleList[i].dataset.id)
            }
        }

        for(let i=0; i<checkboxResList.length; i++){
            if(checkboxResList[i].checked){
                this.userResIdList.push(checkboxResList[i].dataset.id)
            }
        }

        fetch(this.baseURL + this.user_role_and_res_url + "?uid=" + this.selectUserId , {
            method: "PUT",
            headers:{
                token: localStorage.getItem("token")||"",
                'Content-Type': 'application/json',
            },
            body:JSON.stringify({
                roleList: this.userRoleIdList,
                resList: this.userResIdList,
            })
        }).then(res=>res.json()).then(res=>{
            console.log("变更成功：", res)
        })
    }

    // 初始化方法调用
    this.init(config);
}