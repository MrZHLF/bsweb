package com.lj.app.bsweb.upm.role.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lj.app.bsweb.upm.role.entity.UpmPermission;
import com.lj.app.core.common.base.service.BaseServiceImpl;
import com.lj.app.core.common.pagination.Page;
import com.lj.app.core.common.tree.BootStrapTreeView;
import com.lj.app.core.common.tree.BootStrapTreeViewCheck;

/**
 * 
 * 角色服务类
 *
 * @param <UpmRole> 角色对象
 */
@Service("upmRoleService")
public class UpmRoleServiceImpl<UpmRole> extends BaseServiceImpl<UpmRole> implements UpmRoleService<UpmRole> {

  @Autowired
  private UpmPermissionService<UpmPermission> upmPermissionService;

  /**
   * 分页查找当前登录人可分配的所有角色
   * 
   * @param page 分页对象
   * @param condition 过滤条件
   * @return 分页对象
   */
  public Page findUpmRolePageByAssignable(Page page, Map<String, Object> condition) throws Exception {
    // TODO:实现查找当前登录人可以分配角色
    return findPageList(page, condition);
  }

  /**
   * 判断是否是自己创建的角色
   * 
   * @param roleId 角色Id
   * @return 是否
   */
  public boolean isSelfCreateOfRole(Integer roleId, Integer operatorId) throws Exception {
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("id", roleId);
    condition.put("createBy", operatorId);
    Number result = (Number) this.queryForObject("isSelfCreateOfRole", condition);
    if (null == result || result.intValue() == 0) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * 将权限分配给角色
   * 
   * @param permissions 权限
   * @param appId 应用Id
   * @param roleId 角色Id
   */
  public void addPermissionToRole(String permissions, String appId, Integer roleId) throws Exception {
    this.deletePermissionById(roleId);

    String[] permission = new String[] {};
    if (permissions != null && !"".equals(permissions)) {
      permission = permissions.split(",");
    }
    for (int i = 0; i < permission.length; i++) {
      addPermissionToRole(roleId, appId, permission[i]);
    }
  }

  /**
   * 将权限分配给角色
   * 
   * @param roleId 角色ID
   * @param appId 应用Id
   * @param permissionId 权限
   */
  public void addPermissionToRole(Integer roleId, String appId, String permissionId) throws Exception {
    Map<String, Object> param = new HashMap<String, Object>();
    param.put("roleId", roleId);
    param.put("appId", appId);
    param.put("permissionId", permissionId);
    this.insertObject("saveRolePermisson", param);
  }

  /**
   * 获取角色树
   * 
   * @param roleId 角色ID
   * @param appId 应用Id
   * @return 获取角色树
   * @throws Exception 异常
   */
  public String getTreeDataJsonCheckBox(Integer roleId, String appId, Integer operatorId) throws Exception {
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("appId", appId);
    condition.put("operatorId", operatorId);
    condition.put("parentId", operatorId);

    List<UpmPermission> list = upmPermissionService.queryForList("findTreeData", condition);

    if (null != list && !list.isEmpty()) {
      List<Integer> permissionIds = getRolePermissionIds(roleId);
      List<BootStrapTreeView> treeNodeList = new ArrayList<BootStrapTreeView>();
      for (int i = 0; i < list.size(); i++) {
        UpmPermission up = list.get(i);
        String id = up.getId() + "";
        String text = up.getName();
        Boolean checked = false;
        for (int j = 0; j < permissionIds.size(); j++) {
          Integer tmpId = permissionIds.get(j);
          if (up.getId().equals(tmpId)) {
            checked = true;
            break;
          }
        }
        String parentId = up.getParentId().intValue() + "";
        treeNodeList.add(BootStrapTreeViewCheck.createNew(id, text, checked, parentId));
      }
      Integer rootId = upmPermissionService.findRootPermissionIdByAppId(appId);

      BootStrapTreeViewCheck simpleTree = BootStrapTreeViewCheck.valueOf(treeNodeList, rootId.toString());

      if (null != simpleTree) {
        return simpleTree.toJsonString();
      }
    }
    return null;
  }

  /**
   * 查找角色对应的权限
   * 
   * @param roleId 角色ID
   * @return 权限ID列表
   */
  public List<Integer> getRolePermissionIds(Integer roleId) {
    Map<String, Integer> condition = new HashMap<String, Integer>();
    condition.put("roleId", roleId);
    List<Integer> permissionIds = queryForList("findRolePermissionIds", condition);
    return permissionIds;
  }

  /**
   * @Description : 查询权限菜单树（没有复选框）
   * 
   * @param roleId 角色ID
   * @param appId 应用Id
   * @return 查询权限菜单树
   * @throws Exception 异常
   */
  public String getPermissionTreeDataJson(Integer roleId, String appId, Integer operatorId) throws Exception {
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("appId", appId);
    condition.put("operatorId", operatorId);
    condition.put("parentId", 0);
    List<UpmPermission> list = upmPermissionService.queryForList("findTreeDataNot2", condition);

    if (null != list && !list.isEmpty()) {
      List<Integer> permissionIds = getRolePermissionIds(roleId);
      List<BootStrapTreeView> treeNodeList = new ArrayList<BootStrapTreeView>();
      for (int i = 0; i < list.size(); i++) {
        UpmPermission up = list.get(i);
        String id = up.getId() + "";
        String text = up.getName();
        Boolean checked = false;
        for (int j = 0; j < permissionIds.size(); j++) {
          Integer tmpId = permissionIds.get(j);
          if (up.getId().equals(tmpId)) {
            checked = true;
            break;
          }
        }
        String parentId = up.getParentId().intValue() + "";
        treeNodeList.add(BootStrapTreeViewCheck.createNew(id, text, checked, parentId));
      }

      Integer rootId = upmPermissionService.findRootPermissionIdByAppId(appId);
      return BootStrapTreeViewCheck.valueOfString(treeNodeList, rootId.toString());
    }
    return null;
  }

  /**
   * @Description : 查看菜单json
   * 
   * @param appId 应用Id
   * @return 查看菜单json
   * @throws Exception 异常
   */
  public String getPermissionTreeMenuDataJson(String appId, List<UpmPermission> list) throws Exception {
    if (null != list && !list.isEmpty()) {
      List<BootStrapTreeView> treeNodeList = new ArrayList<BootStrapTreeView>();
      for (int i = 0; i < list.size(); i++) {
        UpmPermission up = list.get(i);
        String id = up.getId() + "";
        String text = up.getName();
        String parentId = up.getParentId().intValue() + "";
        String url = up.getUrl();
        treeNodeList.add(BootStrapTreeViewCheck.createNew(id, text, true, parentId, url));
      }

      Integer rootId = upmPermissionService.findRootPermissionIdMenuByAppId(appId);
      BootStrapTreeViewCheck bootStrapTreeViewCheck = new BootStrapTreeViewCheck(String.valueOf(rootId), "菜单");

      bootStrapTreeViewCheck.getRootNode().setNodes(treeNodeList);

      return bootStrapTreeViewCheck.toJsonString();
    }
    return null;
  }

  /**
   * 查询当前登录这人员的权限
   * 
   * @param page 分页
   * @param treeNodeId 树节点Id
   * @param appId 应用Id
   * @return 查询当前登录这人员的权限
   * @throws Exception 异常
   */
  public Page pageQueryPermission(Page page, String treeNodeId, String appId, Integer operatorId) throws Exception {
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("operatorId", operatorId);
    condition.put("appId", appId);
    condition.put("parentId", treeNodeId);
    return findPageList(page, condition, "pageQueryPermission");
  }

  @Override
  public void delete(Object obj) {
    deletePermissionById(obj);
    delete("delete", obj);
  }

  /**
   * 根据角色编号删除权限
   * 
   * @param roleId 角色Id
   */
  public void deletePermissionById(Object roleId) {
    Map<String, Object> param = new HashMap<String, Object>();
    param.put("roleId", roleId);
    delete("deleteRolePermissionByRoleId", param);
  }

  public int getSysRoleId(String appId) {
    return 1;// 设置超级管理员
  }

  /**
   * 根据权限查找对应的角色信息
   * 
   * @param permissionId
   *          权限ID
   * @return 根据权限查找对应的角色信息
   */
  public List<UpmRole> findRoleByPermisonId(Integer permissionId) {
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("permissionId", permissionId);
    return queryForList("findRoleByPermisonId", condition);
  }
}
