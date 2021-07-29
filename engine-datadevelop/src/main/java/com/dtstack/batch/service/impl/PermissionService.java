package com.dtstack.batch.service.impl;

import com.dtstack.batch.dao.PermissionDao;
import com.dtstack.batch.domain.Dict;
import com.dtstack.batch.domain.Permission;
import com.dtstack.batch.domain.Role;
import com.dtstack.batch.domain.RolePermission;
import com.dtstack.batch.vo.RoleVO;
import com.dtstack.dtcenter.common.enums.DictType;
import com.dtstack.dtcenter.common.tree.Tree;
import com.dtstack.dtcenter.common.tree.TreeNode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PermissionService extends Tree {

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DictService dictService;

    private final Object lock = new Object();

    private TreeNode root;

    private Map<String, TreeNode> treeNodeMap = new ConcurrentHashMap<>();

    /**
     * 根据角色id获取角色下所有的权限点Id
     *
     * @param roleId
     * @return
     */
    public RoleVO getPermissionIdsByRoleId(Long roleId) {
        List<RolePermission> rolePermissions = rolePermissionService.listByRoleId(roleId);
        if (CollectionUtils.isEmpty(rolePermissions)) {
            return null;
        }
        List<Long> ids = rolePermissions.stream().map(t -> {
            Permission p = permissionDao.getOne(t.getPermissionId());
            return p.getId();
        }).collect(Collectors.toList());

        Role role = roleService.getRoleById(roleId);
        RoleVO roleVO = RoleVO.toVO(role);
        roleVO.setPermissionIds(ids);
        return roleVO;
    }

    public List<Permission> getAll() {
        List<Permission> validPermissions = permissionDao.listAll();
        // 在业务需要隐藏的权限点，界面不会呈现
        List<Dict> dicts = dictService.getDictByType(DictType.AUTH_HIDE.getValue());
        if (CollectionUtils.isNotEmpty(dicts)) {
            List<String> hideCodes = dicts.stream().map(dict -> dict.getDictName()).collect(Collectors.toList());
            Iterator<Permission> it = validPermissions.iterator();
            while (it.hasNext()) {
                if (hideCodes.contains(it.next().getCode())) {
                    it.remove();
                }
            }
        }
        return validPermissions;
    }

    /**
     * 权限树
     *
     * @return
     */
    public TreeNode tree() {
        if (getRootNode() == null) {
            synchronized (lock) {
                if (getRootNode() == null) {
                    reloadTree();
                }
            }
        }
        return getRootNode();
    }

    @Override
    public TreeNode getRootNode() {
        return root;
    }

    @Override
    public void setRootNode(TreeNode root) {
        this.root = root;
    }

    @Override
    public Map<String, TreeNode> getTreeNodeMaps() {
        return this.treeNodeMap;
    }

    @Override
    protected TreeNode transform(Object info) {
        Permission permission = (Permission) info;
        TreeNode node = new TreeNode();
        node.setNodeId(Long.toString(permission.getId()));
        node.setParentId(Long.toString(permission.getParentId()));
        node.setBindData(permission);
        return node;
    }

    private void reloadTree() {
        List nodes = this.getAll();
        super.reload(nodes);
    }

    /**
     * 新增记录
     *
     * @param p
     * @return
     */
    public Integer insert(Permission p) {
        return permissionDao.insert(p);
    }

    /**
     * 根据code 获取权限信息
     *
     * @param code
     * @return
     */
    public Permission getByCode(String code) {
        return permissionDao.getByCode(code);
    }

    /**
     * 获取所有的记录
     *
     * @return
     */
    public List<Permission> listAll() {
        return permissionDao.listAll();
    }

    /**
     * 根据codes 删除记录
     *
     * @param codes
     * @param timestamp
     * @return
     */
    public Integer deleteByCodes(List<String> codes, Timestamp timestamp) {
        return permissionDao.deleteByCodes(codes, timestamp);
    }

    /**
     * 根据id 获取信息
     *
     * @param permissionId
     * @return
     */
    public Permission getOne(@Param("id") Long permissionId) {
        return permissionDao.getOne(permissionId);
    }
}
