drop table if exists tb_shop;

create table tb_shop
(
    shop_id            bigint auto_increment COMMENT '店铺ID'
        primary key,
    shop_name          varchar(50)    NULL COMMENT '店铺名称',
    user_id            varchar(36)    NULL COMMENT '店长用户ID',
    shop_type          tinyint(1)     NULL COMMENT '店铺类型',
    intro              varchar(200)   NULL COMMENT '店铺简介',
    shop_notice        varchar(50)    NULL COMMENT '店铺公告',
    shop_industry      tinyint(1)     NULL COMMENT '店铺行业',
    shop_owner         varchar(20)    NULL COMMENT '店长',
    mobile             varchar(20)    NULL COMMENT '店铺绑定的手机',
    tel                varchar(20)    NULL COMMENT '店铺联系电话',
    shop_lat           varchar(20)    NULL COMMENT '店铺所在纬度',
    shop_lng           varchar(20)    NULL COMMENT '店铺所在经度',
    shop_address       varchar(100)   NULL COMMENT '店铺详细地址',
    province           varchar(10)    NULL COMMENT '店铺所在省份',
    city               varchar(10)    NULL COMMENT '店铺所在城市',
    area               varchar(10)    NULL COMMENT '店铺所在区域',
    pca_code           varchar(20)    NULL COMMENT '店铺省市区代码，用于回显',
    shop_logo          varchar(200)   NULL COMMENT '店铺LOGO',
    shop_photos        varchar(1000)  NULL COMMENT '店铺相册',
    shop_status        tinyint(1)     NULL COMMENT '店铺状态, 0：未开通, 1：营业中, 2：停业中',
    full_free_shipping decimal(15, 2) NULL COMMENT '满X包邮',
    create_time        datetime       NULL COMMENT '创建时间',
    update_time        datetime       NULL COMMENT '更新时间'
) COMMENT '店铺表' charset = utf8mb4;

drop table if exists tb_prod;

create table tb_prod
(
    prod_id      bigint unsigned auto_increment COMMENT '产品ID'
        primary key,
    prod_name    varchar(300) DEFAULT '' NOT NULL COMMENT '商品名称',
    shop_id      bigint                  NULL COMMENT '店铺ID',
    price        decimal(15, 2)          NULL COMMENT '现价',
    brief        varchar(500) DEFAULT '' NULL COMMENT '描述',
    pic          varchar(255)            NULL COMMENT '商品主图',
    images       varchar(1000)           NULL COMMENT '商品图片，以,分割',
    status       tinyint(1)   DEFAULT 1  NULL COMMENT '默认是1, 表示正常状态, 2表示下架, 3表示删除',
    category_id  bigint unsigned         NULL COMMENT '商品分类',
    sold_num     int                     NULL COMMENT '销量',
    total_stocks int          DEFAULT 0  NULL COMMENT '总库存',
    create_time  datetime                NULL COMMENT '录入时间',
    update_time  datetime                NULL COMMENT '修改时间',
    putaway_time datetime                NULL COMMENT '上架时间',
    version      int                     NULL COMMENT '版本号'
) COMMENT '商品表' charset = utf8mb4;

drop table if exists tb_sku;

create table tb_sku
(
    sku_id      bigint unsigned auto_increment COMMENT '单品ID'
        primary key,
    sku_name    varchar(120)         NULL COMMENT 'sku名称',
    prod_id     bigint unsigned      NOT NULL COMMENT '商品ID',
    prod_name   varchar(255)         NULL COMMENT '商品名称',
    properties  varchar(2000)        NULL COMMENT '销售属性组合字符串 格式是p1：v1;p2：v2',
    price       decimal(15, 2)       NULL COMMENT '价格',
    stocks      int                  NULL COMMENT '实际库存',
    party_code  varchar(100)         NULL COMMENT '商家编码',
    model_id    varchar(100)         NULL COMMENT '商品条形码',
    pic         varchar(500)         NULL COMMENT 'sku图片',
    weight      double               NULL COMMENT '商品重量',
    volume      double               NULL COMMENT '商品体积',
    status      tinyint(1) DEFAULT 1 NULL COMMENT '0 禁用 1 启用',
    create_time datetime             NULL COMMENT '录入时间',
    update_time datetime             NULL COMMENT '修改时间',
    version     int        DEFAULT 0 NOT NULL COMMENT '版本号'
) COMMENT '商品规格表' charset = utf8mb4;

drop table if exists tb_order;

create table tb_order
(
    order_item_id       bigint unsigned auto_increment COMMENT '订单项ID' primary key,
    shop_id             bigint                      NULL COMMENT '店铺Id',
    shop_name           varchar(50)                 NULL COMMENT '店铺名称',
    user_id             varchar(36)                 NOT NULL COMMENT '订购用户ID',
    prod_name           varchar(1000)  DEFAULT ''   NOT NULL COMMENT '产品名称',
    prod_count          int(11)                     NOT NULL DEFAULT '1' COMMENT '产品个数',
    prod_id             bigint unsigned             NOT NULL COMMENT '商品ID',
    sku_id              bigint unsigned             NOT NULL COMMENT '单品ID',
    order_serial_number varchar(50)                 NOT NULL COMMENT '订购流水号',
    cost                decimal(15, 2) DEFAULT 0.00 NOT NULL COMMENT '总值',
    actual_cost         decimal(15, 2)              NULL COMMENT '实际总值',
    addr_order_id       bigint                      NULL COMMENT '用户订单地址Id',
    remarks             varchar(1024)               NULL COMMENT '订单备注',
    pay_type            tinyint(1)                  NULL COMMENT '支付方式, 0：手动代付, 1：微信支付, 2：支付宝',
    status              int            DEFAULT 1    NOT NULL COMMENT '订单状态, 1：待付款, 2：待发货, 3：待收货, 4：待评价, 5：成功, 6：失败',
    dvy_id              bigint                      NULL COMMENT '配送方式ID',
    dvy_type            varchar(10)                 NULL COMMENT '配送类型, 1：物流配送, 2：无需配送',
    dvy_flow_id         varchar(100)                NULL COMMENT '物流单号',
    dvy_time            datetime                    NULL COMMENT '发货时间',
    freight_amount      decimal(15, 2) DEFAULT 0.00 NULL COMMENT '订单运费',
    create_time         datetime                    NOT NULL COMMENT '订购时间',
    update_time         datetime                    NULL COMMENT '订单更新时间',
    pay_time            datetime                    NULL COMMENT '付款时间',
    finally_time        datetime                    NULL COMMENT '完成时间',
    cancel_time         datetime                    NULL COMMENT '取消时间',
    is_payed            tinyint(1)     DEFAULT 0    NULL COMMENT '是否已支付, 1：已支付, 0：未支付',
    is_delete           tinyint(1)     DEFAULT 0    NULL COMMENT '用户订单删除状态, 0：未删除, 1：已删除',
    reduce_amount       decimal(15, 2)              NULL COMMENT '优惠总额',
    close_type          tinyint(1)                  NULL COMMENT '订单关闭原因, 1：超时未支付, 2：退款关闭, 3：买家取消, 4：已完成'
) COMMENT '订单项' charset = utf8mb4;

drop table if exists tb_basket;

CREATE TABLE `tb_basket`
(
    `basket_id`   bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `shop_id`     bigint(20)          NOT NULL COMMENT '店铺ID',
    `prod_id`     bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '产品ID',
    `sku_id`      bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'SkuID',
    `user_id`     varchar(50)         NOT NULL COMMENT '用户ID',
    `status`      int(1)              NOT NULL DEFAULT '1' COMMENT '状态 1 正常 0 无效',
    `create_time` datetime            NOT NULL COMMENT '加入时间',
    `update_time` datetime            NULL COMMENT '修改时间',
    PRIMARY KEY (`basket_id`),
    UNIQUE KEY `uk_user_shop_sku_1` (`user_id`, `sku_id`, `shop_id`),
    KEY `shop_id` (`shop_id`),
    KEY `user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='购物车';

drop table if exists tb_user;

CREATE TABLE `tb_user`
(
    `user_id`        varchar(36) NOT NULL DEFAULT '' COMMENT 'ID',
    `nick_name`      varchar(50)          DEFAULT NULL COMMENT '用户昵称',
    `real_name`      varchar(50)          DEFAULT NULL COMMENT '真实姓名',
    `user_mail`      varchar(100)         DEFAULT NULL COMMENT '用户邮箱',
    `login_password` varchar(255)         DEFAULT NULL COMMENT '登录密码',
    `pay_password`   varchar(50)          DEFAULT NULL COMMENT '支付密码',
    `user_mobile`    varchar(50)          DEFAULT NULL COMMENT '手机号码',
    `register_time`  datetime    NOT NULL COMMENT '注册时间',
    `modify_time`    datetime    NOT NULL COMMENT '修改时间',
    `sex`            char(1)              DEFAULT 'M' COMMENT 'M(男) or F(女)',
    `birth_date`     char(10)             DEFAULT NULL COMMENT '例如：2009-11-27',
    `pic`            varchar(255)         DEFAULT NULL COMMENT '头像图片路径',
    `status`         int(1)      NOT NULL DEFAULT '1' COMMENT '状态 1 正常 0 无效',
    `score`          int(11)              DEFAULT NULL COMMENT '用户积分',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `ud_user_mail` (`user_mail`),
    UNIQUE KEY `ud_user_unique_mobile` (`user_mobile`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户表';

drop table if exists tb_user_addr;

CREATE TABLE `tb_user_addr`
(
    `addr_id`     bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id`     varchar(36)         NOT NULL DEFAULT '0' COMMENT '用户ID',
    `receiver`    varchar(50)                  DEFAULT NULL COMMENT '收货人',
    `province`    varchar(100)                 DEFAULT NULL COMMENT '省',
    `city`        varchar(20)                  DEFAULT NULL COMMENT '城市',
    `area`        varchar(20)                  DEFAULT NULL COMMENT '区',
    `post_code`   varchar(15)                  DEFAULT NULL COMMENT '邮编',
    `addr`        varchar(1000)                DEFAULT NULL COMMENT '地址',
    `mobile`      varchar(20)                  DEFAULT NULL COMMENT '手机',
    `status`      int(1)              NOT NULL COMMENT '状态, 1正常, 0无效',
    `common_addr` int(1)              NOT NULL DEFAULT '0' COMMENT '是否默认地址 1是',
    `create_time` datetime            NOT NULL COMMENT '建立时间',
    `update_time` datetime            NOT NULL COMMENT '更新时间',
    `used_time`   datetime            NOT NULL COMMENT '最后使用时间',
    `version`     int(5)              NOT NULL DEFAULT '0' COMMENT '版本号',
    PRIMARY KEY (`addr_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户配送地址';

drop table if exists tb_user_addr_order;

CREATE TABLE `tb_user_addr_order`
(
    `addr_order_id`       bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `addr_id`             bigint(20) unsigned NOT NULL COMMENT '地址ID',
    `user_id`             varchar(36)         NOT NULL DEFAULT '0' COMMENT '用户ID',
    `order_serial_number` varchar(50)         NOT NULL COMMENT '订购流水号',
    `receiver`            varchar(50)                  DEFAULT NULL COMMENT '收货人',
    `province`            varchar(100)                 DEFAULT NULL COMMENT '省',
    `city`                varchar(20)                  DEFAULT NULL COMMENT '城市',
    `area`                varchar(20)                  DEFAULT NULL COMMENT '区',
    `addr`                varchar(1000)                DEFAULT NULL COMMENT '地址',
    `post_code`           varchar(15)                  DEFAULT NULL COMMENT '邮编',
    `mobile`              varchar(20)                  DEFAULT NULL COMMENT '手机',
    `create_time`         datetime            NOT NULL COMMENT '建立时间',
    `version`             int(5)              NOT NULL DEFAULT '0' COMMENT '版本号',
    PRIMARY KEY (`addr_order_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户订单配送地址';
