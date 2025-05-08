package com.xi.convert;

import com.xi.entity.tb.ShopDo;
import com.xi.entity.dto.ShopDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopConvert {
    ShopConvert INSTANCE = Mappers.getMapper(ShopConvert.class);

    ShopDto ShopDoToShopDto(ShopDo shopDo);
}
