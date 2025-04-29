package com.xi.convert;

import com.xi.domain.SkuDo;
import com.xi.domain.dto.SkuDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import javax.swing.*;

@Mapper
public interface SkuConvert {
    SkuConvert INSTANCE = Mappers.getMapper(SkuConvert.class);

    SkuDto SkuDoToDto(SkuDo skuDo);
}
