package com.xi.domain.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CanalMessage<T> {

    private String type;

    private String table;

    private String database;

    private List<T> data;

    private List<T> old;

}
