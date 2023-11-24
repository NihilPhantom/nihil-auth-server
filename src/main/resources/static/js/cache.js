function Cache(config) {
    // 初始化方法
    this.init = (config) => {
        this.handler = document.getElementById(config.handler); // 控制按钮
        this.target = document.getElementById(config.target);   // 目标：点击控制按钮后，会把target里的所有内容发送改变
        this.baseURL = config.baseURL;                          // 用户后台的接口

        this.list_role_url = "/role/list";
        this.list_role_res_url = "/role/resList"
        this.res_online_url = "/res/resOnline";
        this.res_inDB_url = "/res/resIndatabase";
        this.cache_res_role_list_url = "/cache/getRoleResCache";

        this.roleList = [];        // 存放【角色列表查询】功能列表
        this.resMap = {};          // 存放所有系统 Controller 的资源
        this.selectRoleId = -1;    // 当前被选中的角色 Id
        this.roleResIdList = [];   // 存放所有当前角色所拥有的资源
        this.resInDatabase = [];   // 存放数据库中的所有资源
        this.cachedResMap = {};    // 缓存中资源到用户的映射表


        this.handler.addEventListener("click", this.render);


        /* 模块根 dom */
        this.domRootDiv = document.createElement("div");

        /* 渲染展示表格 */
        this.domRoleTable = document.createElement("table");
        this.domRoleTable.id = "role-table";

        /* 存放所有的资源整合后的结果，用于用户角色管理 */
        this.domAllResTreeDiv = document.createElement("div");


        /* 各个标题 */
        let title1 = document.createElement("h3");
        title1.innerText = "角色列表"

        let title4 = document.createElement("h3");
        title4.innerText = "角色拥有资源"


        this.domRootDiv.appendChild(title1);
        this.domRootDiv.appendChild(this.domRoleTable);

        this.domRootDiv.appendChild(title4);
        this.domRootDiv.appendChild(this.domAllResTreeDiv);


        // 注册点击方法：角色展示 table 中操作按钮被点击时触发的事件
        this.domRoleTable.onclick = ((e)=>{
            if(e.target.tagName === "BUTTON"){
                if(e.target.innerText == "资源管理"){
                    this.selectRoleId = e.target.dataset.id;
                    this.getResIdListByRoleId();
                }else if(e.target.innerText == "删除"){
                }
            }
        })
    }

    // 子渲染方法: 渲染展示表格
    this._renderTable = () => {
        let renderString =
            "    <thead>" +
            "        <tr><td>ID值</td><td>角色名</td><td>操作</td></tr>" +
            "    </thead>" +
            "    <tbody>";

        this.roleList.forEach(item=>{
            renderString +=
                "<tr>" +
                "<td>"+ item.id +"</td><td>" + item.name + "</td>" +
                "<td>" +
                "    <button data-id=\""+ item.id +"\">修改</button>" +
                "    <button data-id=\""+ item.id +"\">删除</button>" +
                "    <button data-id=\""+ item.id +"\">资源管理</button>" +
                "</td>" +
                "</tr>";
        })
        renderString +="</tbody>"
        this.domRoleTable.innerHTML = renderString;
    }

    // 子渲染方法：渲染一个用户所拥有的所有资源
    this._renderRoleResTree = () => {
        // 数据库，系统资源必须加载
        if(this.resInDatabase.length === 0 || this.resMap === {} || this.cachedResMap === {}){
            return;
        }
        let renderString = "<p> 当前角色为id为：" + this.selectRoleId + " </p>"

        // 渲染 Controller 中的所有资源
        for(let key in this.resMap){
            // 添加资源列表（作为树的子结点）
            let subRenderString = ""
            let fatherCheck = "checked";   // 父节点是否被选择
            this.resMap[key].forEach(item => {
                for(let i=0; i<this.resInDatabase.length; i++){
                    if(item.url == this.resInDatabase[i].url && item.method == this.resInDatabase[i].method){
                        item.id = this.resInDatabase[i].id;
                        this.resInDatabase.slice(i,1);
                        break;
                    }
                }
                if(!item.id){
                    console.error("数据库有误:请先同步资源")
                    return;
                }
                let check = "checked";
                if(this.roleResIdList.indexOf(item.id)==-1){
                    check = "";
                    fatherCheck = "";
                }
                subRenderString +=
                    "<div class=\"tree-node-2\">" +
                    "<input data-id='"+ item.id +"' class=\"checkbox2\" type=\"checkbox\" "+ check +" /><span>" +
                    item.url + "|" + item.method + "|" +
                    (this.cachedResMap[item.id]?this.cachedResMap[item.id].toString():"") +
                    "</span></div>"
            })

            renderString +=
                "<div>" +
                "    <div class=\"tree-node-1\">" +
                "    <input class=\"checkbox1\" type=\"checkbox\" "+ fatherCheck +"/><span>" + key + "</span>" +
                "    </div>" +
                subRenderString +
                "</div>"
        }

        // 加载数据库中的，不在 Controller 当中的
        let subRenderString = ""
        let fatherCheck = "checked";   // 父节点是否被选择
        this.resInDatabase.forEach(item=>{
            if(item.permanent){
                if(this.roleResIdList.indexOf(item.id)==-1){
                    check = "";
                    fatherCheck = "";
                }
                subRenderString +=
                    "<div class=\"tree-node-2\">" +
                    "<input data-id='"+ item.id +"' class=\"checkbox2\" type=\"checkbox\" "+ check +" /><span>" +
                    item.url + "|" + item.method + "|" +
                    (this.cachedResMap[item.id]?this.cachedResMap[item.id].toString():"") +
                    "</span></div>"
            }
        })
        renderString +=
            "<div>" +
            "    <div class=\"tree-node-1\">" +
            "    <input class=\"checkbox1\" type=\"checkbox\" "+ fatherCheck +"/><span> 数据库 </span>" +
            "    </div>" +
            subRenderString +
            "</div>"

        this.domAllResTreeDiv.innerHTML = renderString;
    }

    // 渲染方法
    this.render = (e) => {
        this.target.innerHTML = "";
        // 初始化请求
        this.getRoleList();
        this.loadResOnlineRes();
        this.loadResInDatabase();
        this.getRoleResCache();
        this.target.appendChild(this.domRootDiv);
    }

    // 前后端交互方法：加载所有 Controller 资源的方法
    this.loadResOnlineRes = () => {
        fetch(this.baseURL + this.res_online_url, {
            method: "GET",
            headers:{ token: localStorage.getItem("token")||""}
        }).then(res=>res.json()).then(res=>{
            for (let key in res.data) {
                console.log("Key: " + key);
                console.log("Value: " + res.data[key]);
            }
            this.resMap = res.data;
            this._renderRoleResTree()
        })
    }

    // 前后端交互方法：加载所有 数据库 资源的方法
    this.loadResInDatabase = () => {
        fetch(this.baseURL + this.res_inDB_url, {
            method: "GET",
            headers:{ token: localStorage.getItem("token")||""}
        }).then(res=>res.json()).then(res=>{
            this.resInDatabase = res.data;
            this._renderRoleResTree()
        })
    }

    // 前后端交方法：获取角色list
    this.getRoleList = () =>{
        fetch(this.baseURL + this.list_role_url , {
            method: "GET",
            headers:{ token: localStorage.getItem("token")||""}
        }).then(res=>res.json()).then(res=>{
            this.roleList = res.data;
            this._renderTable();
        })
    }

    // 前后端交互方法：获取角色拥有的资源列表
    this.getResIdListByRoleId = () => {
        fetch(this.baseURL + this.list_role_res_url + "?roleId=" + this.selectRoleId , {
            method: "GET",
            headers:{ token: localStorage.getItem("token")||""}
        }).then(res=>res.json()).then(res=>{
            this.roleResIdList = res.data.map(item=>item.resourceId)
            this._renderRoleResTree()
        })
    }

    // 前后端交互方法: 获取缓存中的 资源到用户列表的映射
    this.getRoleResCache = () => {
        fetch(this.baseURL + this.cache_res_role_list_url, {
            method: "GET",
            headers:{ token: localStorage.getItem("token")||""}
        }).then(res=>res.json()).then(res=>{
            this.cachedResMap = res.data
            this._renderRoleResTree()
        })
    }

    // 初始化方法调用
    this.init(config);
}
