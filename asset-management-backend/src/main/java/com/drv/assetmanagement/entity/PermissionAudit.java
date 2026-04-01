package com.drv.assetmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 权限审计实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("permission_audit")
public class PermissionAudit {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 被操作用户ID
     */
    private Long targetUserId;

    /**
     * 被操作用户姓名
     */
    private String targetUserName;

    /**
     * 变更类型 - ADD/REMOVE/UPDATE
     */
    private String changeType;

    /**
     * 变更前角色
     */
    private String oldRoles;

    /**
     * 变更后角色
     */
    private String newRoles;

    /**
     * 变更原因
     */
    private String changeReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}