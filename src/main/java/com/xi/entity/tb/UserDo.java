package com.xi.entity.tb;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("tb_user")
public class UserDo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private String userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 用户邮箱
     */
    private String userMail;

    /**
     * 登录密码
     */
    private String loginPassword;

    /**
     * 支付密码
     */
    private String payPassword;

    /**
     * 手机号码
     */
    private String userMobile;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * M(男) or F(女)
     */
    private String sex;

    /**
     * 例如：2009-11-27
     */
    private String birthDate;

    /**
     * 头像图片路径
     */
    private String pic;

    /**
     * 状态 1 正常 0 无效
     */
    private Integer status;

    /**
     * 用户积分
     */
    private Integer score;
}
