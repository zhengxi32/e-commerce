package com.xi.convert;

import com.xi.domain.ShopDo;
import com.xi.domain.dto.ShopDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopConvert {
    ShopConvert INSTANCE = Mappers.getMapper(ShopConvert.class);

    ShopDto ShopDoToShopDto(ShopDo shopDo);
}
