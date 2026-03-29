package com.design.warehousemanagement.mapper.wms;

import com.design.warehousemanagement.pojo.vo.inbound.InboundOrderDetailInsertVO;
import com.design.warehousemanagement.pojo.vo.inbound.InboundOrderInsertVO;
import com.design.warehousemanagement.pojo.vo.inbound.MotorcycleModelLiteVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InboundOrderMapper {

    void insertOrder(InboundOrderInsertVO order);

    void batchInsertDetails(@Param("details") List<InboundOrderDetailInsertVO> details);

    @Select("SELECT id, model_code, model_name FROM wms_motorcycle_model WHERE model_code = #{modelCode} AND dr = 0 AND status = 1 LIMIT 1")
    MotorcycleModelLiteVO findModelByCode(@Param("modelCode") String modelCode);

    @Select("SELECT COUNT(1) FROM wms_inbound_order WHERE order_no = #{orderNo}")
    int existsOrderNo(@Param("orderNo") String orderNo);
}
