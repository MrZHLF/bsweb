﻿<%@page language="java" isELIgnored="false"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/jsp/common/taglibs.jsp" %>

<!DOCTYPE html>
<html>
<head>
<title>角色管理</title>
<%@ include file="/jsp/common/meta.jsp" %>
<%@ include file="/jsp/common/resource/scripts_all.jsp" %>

<script language="javascript">
		
		$(document).ready(function(){
			 var oTable = new TableInit();
		     oTable.Init();
		     
		     var oTable2= new TableInit2();
		     oTable2.Init2();
		});
		
		var TableInit = function () {
	        var oTableInit = new Object();
	        //初始化Table
	        oTableInit.Init = function () {
	            $('#tableList').bootstrapTable({
	                url: '${ctx}/jsp/role/upmRoleAction!bootStrapList.action',         //请求后台的URL（*）
	                method: 'post',                     //请求方式（*）
	                dataType: "json",
	                contentType : "application/x-www-form-urlencoded",
	                dataField: "rows",//服务端返回数据键值 就是说记录放的键值是rows，分页时使用总记录数的键值为total
	                totalField: 'total',
	                toolbar: '#toolbar',                //工具按钮用哪个容器
	                striped: true,                      //是否显示行间隔色
	                cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
	                pagination: true,                   //是否显示分页（*）
	                smartDisplay:false,
	                showRefresh:true,
	                showColumns:true,
	                searchOnEnterKey:true,
	                showFooter:true,
	                search:false,
	                sortable: true,                     //是否启用排序
	                sortOrder: "asc",                   //排序方式
	                singleSelect:false,
	                clickToSelect: true,
	                smartDisplay:true,
	                queryParams: oTableInit.queryParams,//传递参数（*）
	                queryParamsType:'',					//  queryParamsType = 'limit' 参数: limit, offset, search, sort, order;
	                									//  queryParamsType = '' 参数: pageSize, pageNumber, searchText, sortName, sortOrder.
	                sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
	                pageNumber:1,                       //初始化加载第一页，默认第一页
	                pageSize: 25,                       //每页的记录行数（*）
	                pageList: [5,10, 25, 40, 50, 100,'all'],        //可供选择的每页的行数（*）
	                strictSearch: true,
	                clickToSelect: true,                //是否启用点击选中行
	                height: 460,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
	                uniqueId: "id",                     //每一行的唯一标识，一般为主键列
	                cardView: false,                    //是否显示详细视图
	                detailView: false,                   //是否显示父子表
	                columns: [  
				{ field: 'checkStatus', title: '',checkbox:true }, 
	                           {field : 'Number', title : '行号', formatter : function(value, row, index) {  
	                        	   			return index+1;
	                           			}  
	                           },
				 	{field:'id',title:'编号', sortable:true},
				 	{field:'roleCode',title:'角色编码', sortable:true},
				 	{field:'appId',title:'应用编码', sortable:true},
				 	{field:'roleName',title:'角色名称', sortable:true},
	                        ],               		
	             	formatLoadingMessage: function () {
	             		return "请稍等，正在加载中...";
	             	},
	             	formatNoMatches: function () { //没有匹配的结果
	             		return '无符合条件的记录';
	             	},
	             	onLoadError: function (data) {
	             		$('#tableList').bootstrapTable('removeAll');
	             	},
	             	responseHandler: function (res) {
	             	    return {
	             	        total: res.total,
	             	        rows: res.rows
	             	    };
	             	}
	              
	            });
	            
	        };
	 
	        //得到查询的参数
	      oTableInit.queryParams = function (params) {
				var roleCode = $("#roleCode").val();
		    	var roleName = $("#roleName").val();
		    	var appId = $("#appId").val();

	            var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
	                //limit: params.limit, //第几条记录
	                //offset: params.offset, //显示一页多少记录
	                //maxrows: params.limit,
	                //pageindex:params.pageNumber,
	                rows:params.rows,
	                page:params.page,
	                total:params.total,
	                pageSize:params.limit,
	                offset:params.offset,
	                "sortName":this.sortName,
	                "sortOrder":this.sortOrder,
					"upmRole.roleCode":roleCode,
					"upmRole.roleName":roleName,
					"upmRole.appId":appId
	            };
	            return temp;
	        };
	        return oTableInit;
	    };
	    
	    var TableInit2 = function () {
	        var oTableInit2 = new Object();
	        //初始化Table
	        oTableInit2.Init2 = function () {
	            $('#tableList2').bootstrapTable({
	                url: '${ctx}/jsp/role/upmRoleAction!findAssignedRoleList.action?userGroupId=${param.userGroupId}',         //请求后台的URL（*）
	                method: 'post',                     //请求方式（*）
	                dataType: "json",
	                contentType : "application/x-www-form-urlencoded",
	                dataField: "rows",//服务端返回数据键值 就是说记录放的键值是rows，分页时使用总记录数的键值为total
	                totalField: 'total',
	                toolbar: '#toolbar',                //工具按钮用哪个容器
	                striped: true,                      //是否显示行间隔色
	                cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
	                pagination: true,                   //是否显示分页（*）
	                smartDisplay:false,
	                showRefresh:true,
	                showColumns:true,
	                searchOnEnterKey:true,
	                showFooter:true,
	                search:false,
	                sortable: true,                     //是否启用排序
	                sortOrder: "asc",                   //排序方式
	                singleSelect:false,
	                clickToSelect: true,
	                smartDisplay:true,
	                queryParams: oTableInit2.queryParams,//传递参数（*）
	                queryParamsType:'',					//  queryParamsType = 'limit' 参数: limit, offset, search, sort, order;
	                									//  queryParamsType = '' 参数: pageSize, pageNumber, searchText, sortName, sortOrder.
	                sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
	                pageNumber:1,                       //初始化加载第一页，默认第一页
	                pageSize: 25,                       //每页的记录行数（*）
	                pageList: [5,10, 25, 40, 50, 100,'all'],        //可供选择的每页的行数（*）
	                strictSearch: true,
	                clickToSelect: true,                //是否启用点击选中行
	                height: 460,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
	                uniqueId: "id",                     //每一行的唯一标识，一般为主键列
	                cardView: false,                    //是否显示详细视图
	                detailView: false,                   //是否显示父子表
	                columns: [  
							{ field: 'checkStatus', title: '',checkbox:true }, 
	                           {field : 'Number', title : '行号', formatter : function(value, row, index) {  
	                        	   			return index+1;
	                           			}  
	                           },
					 	{field:'userGroupRoleRelId',title:'关联ID', sortable:true},
					 	{field:'id',title:'编号', sortable:true},
					 	{field:'roleCode',title:'角色编码', sortable:true},
					 	{field:'appId',title:'应用编号', sortable:true},
					 	{field:'roleName',title:'角色名称', sortable:true}
						 
	                        ],               		
	             	formatLoadingMessage: function () {
	             		return "请稍等，正在加载中...";
	             	},
	             	formatNoMatches: function () { //没有匹配的结果
	             		return '无符合条件的记录';
	             	},
	             	onLoadError: function (data) {
	             		$('#tableList').bootstrapTable('removeAll');
	             	},
	             	responseHandler: function (res) {
	             	    return {
	             	        total: res.total,
	             	        rows: res.rows
	             	    };
	             	}
	              
	            });
	            
	        };
	 
	        //得到查询的参数
	      oTableInit2.queryParams = function (params) {
	            var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
	                //limit: params.limit, //第几条记录
	                //offset: params.offset, //显示一页多少记录
	                //maxrows: params.limit,
	                //pageindex:params.pageNumber,
	                rows:params.rows,
	                page:params.page,
	                total:params.total,
	                pageSize:params.limit,
	                offset:params.offset,
	                "sortName":this.sortName,
	                "sortOrder":this.sortOrder
	            };
	            return temp;
	        };
	        return oTableInit2;
	    };
	    
		
</script>
</head>

<body>
	<div class="container">
		<div class="row">
		<div class="sm-col-12">
			<input type="hidden"  id = "userGroupId" name="userGroupId"  value="${param.userGroupId}"/>
		    
		    <div class="panel-body" style="padding-bottom:0px;">
		        <div class="panel panel-default">
		            <div class="panel-heading">分配角色</div>
		            <div class="panel-body">
		                <form id="formSearch" class="form-horizontal">
		                    <div class="form-group" style="margin-top:15px">
						 	<label class="control-label col-sm-1" for="roleCode">角色编码</label>
							<div class="col-sm-2"> <input type="text" class="form-control" id="roleCode"></div>
								<label class="control-label col-sm-1" for="roleName">角色名称</label>
							<div class="col-sm-2"> <input type="text" class="form-control" id="roleName"></div>
						 	<label class="control-label col-sm-1" for="appId">应用编码</label>
							<div class="col-sm-2"> <input type="text" class="form-control" id="appId"></div>
			                   <div class="col-sm-6" style="text-align:left;">
			                       <button type="button" style="margin-left:50px" id="btn_query" class="btn btn-primary">查询</button>
			                   </div>
		                    </div>
		                </form>
		            </div>
		        </div>       
		
		        <div id="toolbar" class="btn-group">
		            <button id="btn_add" type="button" class="btn btn-default" onclick="batchSave()" >
		                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>分配角色
		            </button>
		            <button id="btn_delete" type="button" class="btn btn-default" onclick="doDelete()">
		                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除已分配角色
		            </button>
		        </div>
		        
		        <table id="tableList"></table>
		    </div>
		    
		      <div class="panel-body" style="padding-bottom:0px;">
		        <div class="panel panel-default">
		            <div class="panel-heading">已经分配角色</div>
		        <table id="tableList2"></table>
		    </div>
	    </div>
	    </div>
    </div>
    

    <script type="text/javascript">
    var $tableList = $('#tableList');
    var $tableList2 = $('#tableList2');
    var $btn_query = $('#btn_query');
    
    $btn_query.click(function () {
      	 refreshGrid();
      });
       
     	function refreshGrid(){
     		$tableList.bootstrapTable('refresh');
     	};
	    
		 function batchSave(){
			 var ids = $.map($tableList.bootstrapTable('getSelections'), function (row) {
                 return row.id;
             });
        	 
        	if(ids == ''|| ids==null){
        		bootbox.alert("请选择至少一条记录");
        		return;
        	}

        	bootbox.confirm('确认要提交么?',function (result) {  
                if(result) {  
                	doBatchSave();
                }
        	});
        }
        	
        function doBatchSave(){
            var userGroupId =$("#userGroupId").val();
            var ids = $.map($tableList.bootstrapTable('getSelections'), function (row) {
                return row.id;
            });
        	
            var result = jQuery.ajax({
		      	  url:"${ctx}/jsp/user/upmUserGroupAndRoleRelAction!doBatchSaveRel.action?multiSelected=" + ids+"&userGroupId="+userGroupId,
		          async:false,
		          cache:false,
		          dataType:"json"
		      }).responseText;
			var obj = eval("("+result+")");
			bootbox.alert(obj.opResult);
			refreshGrid2();
        }
        
		//删除
        function mulDelete(){
        	 var ids = $.map($tableList2.bootstrapTable('getSelections'), function (row) {
                 return row.id;
             });
        	 
        	if(ids == ''|| ids==null){
        		bootbox.alert("请选择至少一条记录");
        		return;
        	}

        	bootbox.confirm('确认要删除么?',function (result) {  
                if(result) {  
                	doDelete();
                }
        	});
        	
        }
        	
        function doDelete(){
        	 var ids = $.map($tableList2.bootstrapTable('getSelections'), function (row) {
                 return row.userGroupRoleRelId;
             });
            var result = jQuery.ajax({
		      	  url:"${ctx}/jsp/user/upmUserGroupAndRoleRelAction!multidelete.action?multidelete=" + ids,
		          async:false,
		          cache:false,
		          dataType:"json"
		      }).responseText;
			var obj = eval("("+result+")");
			bootbox.alert(obj.opResult);
			refreshGrid2();
        }
      	
      	function refreshGrid2(){
      		$tableList2.bootstrapTable('refresh');
      	}
      	
    </script>


</body>
</html>
