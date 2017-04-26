﻿<%@page language="java" isELIgnored="false"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/jsp/common/taglibs.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <title>角色管理</title>
    <%@ include file="/jsp/common/meta.jsp" %>
    <%@ include file="/jsp/common/resource/scripts_all.jsp" %>
    <%@ include file="/jsp/common/resource/styles_all.jsp" %>
	
<script  type="text/javascript">
	$(document).ready(function(){
			     
	      $(".datetimepicker").datetimepicker({
	      		language: 'zh-CN',
	             format: 'yyyy-mm-dd hh:ii:ss',//格式化时间,
	             autoclose:true,//日期选择完成后是否关闭选择框
	             //minView: "month",//设置只显示到月份
	             clearBtn:true // 自定义属性,true 显示 清空按钮 false 隐藏 默认:true
	         });
	});

</script>

</head>
<body>
<div class="container">
<form action="upmRoleAction!commonSaveOrUpdate.action" class="form-horizontal" method="post" name="upmRoleForm" id="upmRoleForm" role="form">
<input type="hidden" name="id" id="id" value="${id}"/>
<input type="hidden" name="upmRole.id" id="upmRole.id" value="${upmRole.id}"/>
<input type="hidden" name="operate" id="operate" value="${operate}" />
		 
      		<div class="form-group">
			 <label for="roleCode">角色编码</label>
		 	  <input class="form-control" type="text"  name="upmRole.roleCode" id="roleCode"  value="${upmRole.roleCode}" />
		 </div>
		 
      		<div class="form-group">
			 <label for="appId">应用ID</label>
		 	  <input class="form-control" type="text"  name="upmRole.appId" id="appId"  value="${upmRole.appId}" />
		 </div>
		 
      		<div class="form-group">
			 <label for="roleName">角色名称</label>
		 	  <input class="form-control" type="text"  name="upmRole.roleName" id="roleName"  value="${upmRole.roleName}" />
		 </div>
		 
      		<div class="form-group">
			 <label for="roleDesc">角色描述</label>
		 	   <textarea  name="upmRole.roleDesc" id="roleDesc" class="form-control" rows="3">${upmRole.roleDesc}</textarea> 
		 </div>
		 
       
 	<div class="form-group"> 
        		  <button type="submit" id="save"  class="btn btn-default">保存</button> 
        		   <button type="button" id="backToHomeButton"  class="btn btn-default">取消</button> 
        </div>	
       
    
</form>

</div>

<script   type="text/javascript">

		$("#upmRoleForm").bootstrapValidator({
			
			fields: {
		 		 "upmRole.roleCode": {
					message: '角色编码不能为空',
					validators: {  
                       				 notEmpty: {  
                        				message: '角色编码不能为空'  
                        				},
                        				remote: {
                        					url: "upmRoleAction!checkRoleIsExist.action",
                                            message: '角色已存在',//提示消息
                                            delay :  2000,
                                            type: 'POST',
                                            data: function(validator) {
                                                  return {
                                                	  "strRoleId": "${upmRole.id}",
                          		         			"appId": "${appId}",
                          		         			"roleName": function(){return $("#roleName").val();
                          		         			}
                                                  };
                                               }
                                        },
                    			}  
				
		       },
		 		 "upmRole.appId": {
					message: '应用ID不能为空',
					validators: {  
                       				 notEmpty: {  
                        				message: '应用ID不能为空'  
                        				} 
                    			}  
				
		       },
		 		 "upmRole.roleName": {
					message: '角色名称不能为空',
					validators: {  
                       				 notEmpty: {  
                        				message: '角色名称不能为空'  
                        				} 
                    			}  
				
		       }
		    },
 		submitHandler: function(validator, form, submitButton) {  
               		 validator.defaultSubmit();  
            	}  
			
		});
		
        $("#backToHomeButton").click(function() {
			window.parent.location.href="${ctx}/index.jsp";
        });
</script>
</body>
</html>
