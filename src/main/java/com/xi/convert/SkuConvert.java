package com.xi.convert;

import com.xi.entity.tb.SkuDo;
import com.xi.entity.dto.SkuDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SkuConvert {
    SkuConvert INSTANCE = Mappers.getMapper(SkuConvert.class);

    SkuDto SkuDoToDto(SkuDo skuDo);
}
