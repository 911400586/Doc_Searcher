function getParamByKey(key){
    var params = location.search;
    params = params.substring(1); // 去掉前面"?"
    var paramArr = params.split("&");
    if(paramArr!=null && paramArr.length>0){
      for(var i=0;i<paramArr.length;i++){
        var item = paramArr[i]; // ex:"id=1"
        var itemArr = item.split("=");
        // key == 目标 key
        if(itemArr.length==2 && itemArr[0]==key){
            return itemArr[1]; // 返回 value
        }
      }
    }
    return null;
}