package com.design.warehousemanagement.mapper.wms;

import com.design.warehousemanagement.pojo.vo.task.InboundOrderDetailLiteVO;
import com.design.warehousemanagement.pojo.vo.task.InboundOrderLiteVO;
import com.design.warehousemanagement.pojo.vo.task.WorkTaskInsertVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WorkTaskMapper {

    @Select("SELECT id, order_no, warehouse_id, order_status FROM wms_inbound_order WHERE id = #{orderId} AND dr = 0 LIMIT 1")
    InboundOrderLiteVO findInboundOrderById(@Param("orderId") Long orderId);

    @Select("SELECT id, model_id, motorcycle_id, vin, target_bin_id, detail_status FROM wms_inbound_order_detail WHERE order_id = #{orderId} AND dr = 0")
    List<InboundOrderDetailLiteVO> findInboundDetailsByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT COUNT(1) FROM wms_work_task WHERE task_type = 1 AND inbound_order_id = #{inboundOrderId} AND model_id = #{modelId} " +
            "AND (target_bin_id = #{targetBinId} OR (target_bin_id IS NULL AND #{targetBinId} IS NULL)) AND task_status IN (0,1,2) AND dr = 0")
    int countActiveInboundTask(@Param("inboundOrderId") Long inboundOrderId,
                               @Param("modelId") Long modelId,
                               @Param("targetBinId") Long targetBinId);

    @Select("SELECT COUNT(1) FROM wms_work_task WHERE task_no = #{taskNo}")
    int existsTaskNo(@Param("taskNo") String taskNo);

    void insertWorkTask(WorkTaskInsertVO task);

    void updateInboundOrderStatusToPending(@Param("orderId") Long orderId, @Param("operator") String operator);
}
