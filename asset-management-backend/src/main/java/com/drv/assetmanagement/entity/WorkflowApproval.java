package com.drv.assetmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 流程审批记录表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("workflow_approval")
public class WorkflowApproval {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联流程实例ID
     */
    private Long workflowId;

    /**
     * 第几轮审批
     */
    private Integer round;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批人姓名
     */
    private String approverName;

    /**
     * 审批人类型 - MANAGER/DEPT_HEAD/HR/FINANCE
     */
    private String approverType;

    /**
     * 操作 - SUBMIT/APPROVE/REJECT/RETURN/CLOSE
     */
    private String action;

    /**
     * 结果 - APPROVED/REJECTED/NEED_INFO
     */
    private String result;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 附件（JSON）
     */
    private String attachments;

    /**
     * 下一审批人（用于多级审批）
     */
    private Long nextApproverId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}