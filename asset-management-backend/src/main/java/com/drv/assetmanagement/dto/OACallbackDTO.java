package com.drv.assetmanagement.dto;

import lombok.Data;

/**
 * OA回调请求DTO
 */
@Data
public class OACallbackDTO {
    
    /**
     * 回调类型
     */
    private String callbackType;
    
    /**
     * 业务ID
     */
    private Long bizId;
    
    /**
     * 操作
     */
    private String action;
    
    /**
     * 操作人信息
     */
    private OAOperatorDTO operator;
    
    /**
     * 数据
     */
    private OAActionDataDTO data;
    
    /**
     * 时间戳
     */
    private String timestamp;
    
    /**
     * 签名
     */
    private String signature;
    
    @Data
    public static class OAOperatorDTO {
        private String userId;
        private String userName;
        private String deptName;
    }
    
    @Data
    public static class OAActionDataDTO {
        private String result;
        private String comment;
        private String attachments;
    }
}