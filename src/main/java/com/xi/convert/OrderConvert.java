package com.xi.convert;

import com.xi.entity.tb.OrderDo;
import com.xi.entity.dto.OrderDto;
import com.xi.entity.param.OrderParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderConvert {
    OrderConvert INSTANCE = Mappers.getMapper(OrderConvert.class);

    OrderDto OrderParamToDto(OrderParam orderParam);

    OrderDo OrderDtoToDo(OrderDto orderDto);

    OrderDto OrderDoToDto(OrderDo orderDo);
}
