function logout(){
    if(confirm("是否退出？")){
        jQuery.ajax({
            url:"/logout",
            type:"POST",
            data:{},
            success:function(res){
                if(res.code==200 && res.data==1){
                    // 注销成功
                    location.href = "blog_list.html";
                }else{
                    alert("抱歉：操作失败！"+res.msg);
                }
            }
        });
    }
}