package com.lj.app.bsweb.upm.user.web;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.lj.app.bsweb.upm.AbstractBaseUpmAction;
import com.lj.app.bsweb.upm.user.entity.UpmUserGroup;
import com.lj.app.bsweb.upm.user.service.UpmUserGroupService;
import com.lj.app.core.common.base.entity.BaseEntity;
import com.lj.app.core.common.base.service.BaseService;
import com.lj.app.core.common.exception.BusinessException;
import com.lj.app.core.common.tree.BootStrapTreeView;
import com.lj.app.core.common.tree.BootStrapTreeViewCheck;
import com.lj.app.core.common.util.AjaxResult;
import com.lj.app.core.common.util.DateUtil;
import com.lj.app.core.common.util.StringUtil;
import com.lj.app.core.common.web.AbstractBaseAction;
import com.lj.app.core.common.web.Struts2Utils;

@Controller
@Namespace("/jsp/user")
@Results({
		@Result(name = AbstractBaseAction.INPUT, location = "/jsp/user/upmUserGroup-input.jsp"),
		@Result(name = AbstractBaseAction.SAVE, location = "upmUserGroupAction!edit.action",type=AbstractBaseAction.REDIRECT),
		@Result(name = AbstractBaseAction.LIST, location = "/jsp/user/upmUserGroupList.jsp", type=AbstractBaseAction.REDIRECT)
})

@Action("upmUserGroupAction")
public class UpmUserGroupAction extends AbstractBaseUpmAction<UpmUserGroup> {
	
	private java.lang.Integer id;
	private String userGroupCode;
	private String bussinessCode;
	private String userGroupName;
	private java.lang.Integer parentId;
	private java.lang.Integer createBy;
	private java.util.Date createDate;
	private java.lang.Integer updateBy;
	private java.util.Date updateDate;
	private String enableFlag;
	private String lockStatus;
	
	private Long treeNodeId;
	private Long rootId=0l;

	public Long getTreeNodeId() {
		return treeNodeId;
	}

	public void setTreeNodeId(Long treeNodeId) {
		this.treeNodeId = treeNodeId;
	}

	public Long getRootId() {
		return rootId;
	}

	public void setRootId(Long rootId) {
		this.rootId = rootId;
	}

	public UpmUserGroupService getUpmUserGroupService() {
		return upmUserGroupService;
	}

	public void setUpmUserGroupService(UpmUserGroupService upmUserGroupService) {
		this.upmUserGroupService = upmUserGroupService;
	}

	public UpmUserGroup getUpmUserGroup() {
		return upmUserGroup;
	}

	public void setUpmUserGroup(UpmUserGroup upmUserGroup) {
		this.upmUserGroup = upmUserGroup;
	}

	@Autowired
	private UpmUserGroupService<UpmUserGroup> upmUserGroupService;
	
	private UpmUserGroup upmUserGroup;
	
	public   BaseService<UpmUserGroup> getBaseService(){
		return upmUserGroupService;
	}
	
	public UpmUserGroup getModel() {
		return upmUserGroup;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id!=null ){
			upmUserGroup = (UpmUserGroup)upmUserGroupService.getInfoByKey(id);
		}else {
			upmUserGroup = new UpmUserGroup();
			upmUserGroup.setParentId(parentId);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String list() throws Exception {
		try {
			Map<String,Object> condition = new HashMap<String,Object>();
			condition.put("userGroupCode",  userGroupCode);
			condition.put("userGroupName",  userGroupName);
			
			String jsonStr = "";
			if(treeNodeId==null){
				treeNodeId = 0L;
			}
			List<UpmUserGroup> UpmUserGroupList=upmUserGroupService.findUpmUserByParentId(treeNodeId);
			
			List<BootStrapTreeView> treeNodeList = new ArrayList<BootStrapTreeView>();
			
			for (UpmUserGroup group : UpmUserGroupList) {
				String text = group.getUserGroupName()+"["+group.getUserGroupCode()+"]"+"["+group.getBussinessCode()+"]";
				treeNodeList.add(BootStrapTreeViewCheck.createNew(String.valueOf(group.getId()), text, String.valueOf(group.getParentId())));
			}
			
			jsonStr = BootStrapTreeViewCheck.valueOfString(treeNodeList, treeNodeId.toString());
			
			Struts2Utils.renderText(jsonStr);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public String input() throws Exception {
		return INPUT;
	}
	
	@Override
	public String commonSaveOrUpdate() throws Exception {
		try{
			if (StringUtil.isEqualsIgnoreCase(operate, AbstractBaseAction.EDIT)) {
				BaseEntity entity = (BaseEntity)getModel();
				entity.setUpdateBy(this.getLoginUserId());
				entity.setUpdateByUname(this.getUserName());
				entity.setUpdateDate(DateUtil.getNowDateYYYYMMddHHMMSS());
				
				getBaseService().updateObject(entity);
				returnMessage = UPDATE_SUCCESS;
			}else{
				BaseEntity entity = (BaseEntity)getModel();
				entity.setCreateBy(this.getLoginUserId());
				entity.setCreateByUname(this.getUserName());
				entity.setCreateDate(DateUtil.getNowDateYYYYMMddHHMMSS());
				
				getBaseService().insertObject(entity);
				returnMessage = CREATE_SUCCESS;
			}
			
		}catch(Exception e){
			returnMessage = CREATE_FAILURE;
			e.printStackTrace();
			throw e;
		}
		
		
		return input();
	}
	
	@Override
	public String save() throws Exception {
		
	try{
			if (StringUtil.isEqualsIgnoreCase(operate, AbstractBaseAction.EDIT)) {
				upmUserGroup = new UpmUserGroup();
				upmUserGroup.setId(parentId);
				upmUserGroup.setUserGroupCode(userGroupCode);
				upmUserGroup.setBussinessCode(bussinessCode);
				upmUserGroup.setUserGroupName(userGroupName);
				//upmUserGroup.setParentId(parentId);
				upmUserGroup.setEnableFlag(enableFlag);
				upmUserGroup.setLockStatus(lockStatus);
				upmUserGroup.setUpdateBy(getLoginUserId());
				upmUserGroup.setUpdateDate(DateUtil.getNowDateYYYYMMddHHMMSS());
				upmUserGroupService.updateObject("update2",upmUserGroup);
				
				returnMessage = UPDATE_SUCCESS;
			}else{
				upmUserGroup = new UpmUserGroup();
				upmUserGroup.setUserGroupCode(userGroupCode);
				upmUserGroup.setBussinessCode(bussinessCode);
				upmUserGroup.setUserGroupName(userGroupName);
				upmUserGroup.setParentId(parentId);
				upmUserGroup.setEnableFlag(enableFlag);
				upmUserGroup.setLockStatus(lockStatus);
				
				upmUserGroup.setCreateBy(getLoginUserId());
				upmUserGroup.setCreateDate(DateUtil.getNowDateYYYYMMddHHMMSS());
				upmUserGroupService.insertObject(upmUserGroup);
				returnMessage = CREATE_SUCCESS;
			}
			
			AjaxResult ar = new AjaxResult();
			ar.setOpResult(returnMessage);
			
			Struts2Utils.renderJson(ar);
			return null;
			
		}catch(Exception e){
			returnMessage = CREATE_FAILURE;
			e.printStackTrace();
			throw e;
		}finally{
		}
		
	}

	/**
	 * 删除校验
	 */
	@Override
	public void multideleteValidate(Integer deleteId) throws BusinessException {
		if(deleteId==null || deleteId==0){
			throw new BusinessException("根节点不能进行删除");
		}
		
		List<UpmUserGroup> list=upmUserGroupService.findUpmUserByParentId(Long.parseLong(String.valueOf(deleteId)));
		
		if(list!=null && list.size()>0){
			throw new BusinessException("删除失败,有[" + list.size() + "]个子节点,要解除所有子节点后，才可以进行删除");
		}
	}
	
	public void setId(java.lang.Integer value) {
		this.id = value;
	}
	
	public java.lang.Integer getId() {
		return this.id;
	}
	public void setUserGroupCode(String value) {
		this.userGroupCode = value;
	}
	
	public String getUserGroupCode() {
		return this.userGroupCode;
	}
	public void setUserGroupName(String value) {
		this.userGroupName = value;
	}
	
	public String getUserGroupName() {
		return this.userGroupName;
	}
	public void setParentId(java.lang.Integer value) {
		this.parentId = value;
	}
	
	public java.lang.Integer getParentId() {
		return this.parentId;
	}
	public void setCreateBy(java.lang.Integer value) {
		this.createBy = value;
	}
	
	public java.lang.Integer getCreateBy() {
		return this.createBy;
	}
	public void setCreateDate(java.util.Date value) {
		this.createDate = value;
	}
	
	public java.util.Date getCreateDate() {
		return this.createDate;
	}
	public void setUpdateBy(java.lang.Integer value) {
		this.updateBy = value;
	}
	
	public java.lang.Integer getUpdateBy() {
		return this.updateBy;
	}
	public void setUpdateDate(java.util.Date value) {
		this.updateDate = value;
	}
	
	public java.util.Date getUpdateDate() {
		return this.updateDate;
	}
	public void setEnableFlag(String value) {
		this.enableFlag = value;
	}
	
	public String getEnableFlag() {
		return this.enableFlag;
	}
	public void setLockStatus(String value) {
		this.lockStatus = value;
	}
	
	public String getLockStatus() {
		return this.lockStatus;
	}

	public String getBussinessCode() {
		return bussinessCode;
	}

	public void setBussinessCode(String bussinessCode) {
		this.bussinessCode = bussinessCode;
	}
}

