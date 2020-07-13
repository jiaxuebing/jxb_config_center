/**
 * @desc 获取属性的值
 * 数组元素属性获取：groupList[index].groupName
 * 数组元素中的数组元素获取：groupList[index].interfaceList[index].infUrl
 * @param targetObj
 * @param propExpression
 * @returns {string}
 */
function getObjVal(targetObj,propExpression){
    var expression;
    if(typeof targetObj == 'string'){
        expression = 'JSON.parse(targetObj).'+propExpression;
    }
    if(typeof targetObj == 'object'){
        expression = 'targetObj.'+propExpression;
    }
    var ex = '';
    try{
        ex = eval('('+expression+')');
    }catch (e) {
        print("getObjVal----"+propExpression+"------"+e);
    }
    return ex;
}

/**
 * @desc 检查点比较符操作逻辑判断
 * @type {{stringEq: (function(*, *): boolean), stringNotInclude: (function(*, *=): boolean),
 * stringInclude: (function(*, *=): boolean), comparisonFun: (function(*, *=, *=): string),
 * numEq: (function(*, *): boolean), stringNotEq: (function(*, *): boolean),
 * numGt: (function(*, *): boolean), numLt: (function(*, *): boolean), stringIgEq: (function(*, *): boolean),
 * jsonEq: (function(*=, *=): boolean)}}
 */
var checkPointProcessor = {
    numEq:function(sourceData,targetData){
        return sourceData==targetData;
    },
    numLt:function(sourceData,targetData){
        return sourceData<targetData;
    },
    numGt:function(sourceData,targetData){
        return sourceData>targetData;
    },
    stringEq:function(sourceData,targetData){
        return sourceData == targetData;
    },
    stringIgEq:function(sourceData,targetData){
        return sourceData.toUpperCase() == targetData.toUpperCase();
    },
    stringNotEq:function(sourceData,targetData){
        return sourceData == targetData?false:true;
    },
    stringInclude:function(sourceData,targetData){
        return sourceData.indexOf(targetData)>-1?true:false;
    },
    stringNotInclude:function(sourceData,targetData){
        return sourceData.indexOf(targetData)>-1?false:true;
    },
    jsonEq:function(sourceData,targetData){
        if(typeof sourceData == 'string' && typeof targetData == 'string'){
            sourceData = JSON.parse(sourceData);
            targetData = JSON.parse(targetData);
        }
        sourceData = JSON.stringify(sourceData);
        targetData = JSON.stringify(targetData);
        return sourceData == targetData;
    },
    boolEq:function(sourceData,targetData){
        if(targetData != 'false' && targetData != 'true'){
            return false;
        }

        sourceData = sourceData.toString();
        return sourceData == targetData;
    },
    nullEq:function(sourceData,targetData){
        if(sourceData === null){
            sourceData = 'null';
        }
        return sourceData == targetData;
    },
    comparisonFun:function(comparison,sourceData,targetData){
        var result = 'success';
        var flag = false;
        var str = comparison.split(':');
        var comparisonType = str[0];
        var comparisonOpera = str[1];
        switch(comparisonType){
            case 'num':
                switch(comparisonOpera){
                    case 'eq':
                        flag = this.numEq(sourceData,targetData);
                        break;
                    case 'lt':
                        flag = this.numLt(sourceData,targetData);
                        break;
                    case 'gt':
                        flag = this.numGt(sourceData,targetData);
                        break;
                }
                break;
            case 'string':
                switch(comparisonOpera){
                    case 'eq':
                        flag = this.stringEq(sourceData,targetData);
                        break;
                    case 'igeq':
                        flag = this.stringIgEq(sourceData,targetData);
                        break;
                    case 'noteq':
                        flag = this.stringNotEq(sourceData,targetData);
                        break;
                    case 'include':
                        flag = this.stringInclude(sourceData,targetData);
                        break;
                    case 'notinclude':
                        flag = this.stringNotInclude(sourceData,targetData);
                        break;
                }
                break;
            case 'json':
                switch(comparisonOpera){
                    case 'eq':
                        flag = this.jsonEq(sourceData,targetData);
                }
                break;
            case 'bool':
                switch(comparisonOpera){
                    case 'eq':
                        flag = this.boolEq(sourceData,targetData);
                }
                break;
            case 'null':
                switch (comparisonOpera) {
                    case 'eq':
                        flag = this.nullEq(sourceData,targetData);
                }
        }
        if(!flag){
            result = 'fail'
        }
        return result;
    }

}


function splitGroupData(groupJson){
    if(groupJson == null || !(typeof groupJson == 'string') || typeof groupJson == 'undefined'){
      return ;
    }
    //转换json对象
    var groupObj = JSON.parse(groupJson);

}




