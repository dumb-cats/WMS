package com.design.warehousemanagement.mapper.wms;

import com.design.warehousemanagement.pojo.vo.task.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface WorkTaskMapper {

    @Select("SELECT id, order_no, warehouse_id, order_status FROM wms_inbound_order WHERE id = #{orderId} AND dr = 0 LIMIT 1")
    InboundOrderLiteVO findInboundOrderById(@Param("orderId") Long orderId);

    @Select("SELECT id, model_id, motorcycle_id, vin, target_bin_id, detail_status FROM wms_inbound_order_detail WHERE order_id = #{orderId}")
    List<InboundOrderDetailLiteVO> findInboundDetailsByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT COUNT(1) FROM wms_work_task WHERE task_type = 1 AND inbound_order_id = #{inboundOrderId} AND model_id = #{modelId} " +
            "AND (target_bin_id = #{targetBinId} OR (target_bin_id IS NULL AND #{targetBinId} IS NULL)) AND task_status IN (0,1,2) AND dr = 0")
    int countActiveInboundTask(@Param("inboundOrderId") Long inboundOrderId,
                               @Param("modelId") Long modelId,
                               @Param("targetBinId") Long targetBinId);

    @Select("SELECT COUNT(1) FROM wms_work_task WHERE task_no = #{taskNo}")
    int existsTaskNo(@Param("taskNo") String taskNo);

    @Select("SELECT id, task_no, task_type, warehouse_id, inbound_order_id, model_id, motorcycle_id, vin, source_bin_id, target_bin_id, task_status FROM wms_work_task WHERE id = #{taskId} AND dr = 0 LIMIT 1")
    WorkTaskLiteVO findTaskById(@Param("taskId") Long taskId);

    /**
     * 查询待分配任务候选集（按优先级升序）。
     */
    @Select("SELECT id, task_no, task_type, task_status, priority, inbound_order_id, model_id, vin, target_bin_id " +
            "FROM wms_work_task WHERE warehouse_id = #{warehouseId} AND dr = 0 AND task_status = 0 " +
            "AND assigned_user_id IS NULL AND priority <= #{maxPriority} ORDER BY priority ASC, id ASC LIMIT #{limit}")
    List<WorkerTodoTaskVO> findDispatchCandidates(@Param("warehouseId") Long warehouseId,
                                                  @Param("maxPriority") Integer maxPriority,
                                                  @Param("limit") Integer limit);

    /**
     * 原子分配任务：仅对仍处于待分配状态且无人领取的任务生效。
     */
    @Update("UPDATE wms_work_task SET task_status = 1, assigned_user_id = #{workerId}, assign_time = NOW(), " +
            "last_operator_id = #{operatorId} WHERE id = #{taskId} AND dr = 0 AND task_status = 0 AND assigned_user_id IS NULL")
    int assignTask(@Param("taskId") Long taskId,
                   @Param("workerId") Long workerId,
                   @Param("operatorId") Long operatorId);

    /**
     * 查询工人待办（已分配/执行中）任务列表。
     */
    @Select("SELECT id AS taskId, task_no, task_type, task_status, priority, inbound_order_id, model_id, vin, target_bin_id " +
            "FROM wms_work_task WHERE assigned_user_id = #{workerId} AND dr = 0 AND task_status IN (1,2) " +
            "ORDER BY CASE task_status WHEN 2 THEN 0 ELSE 1 END, priority ASC, id ASC LIMIT #{limit}")
    List<WorkerTodoTaskVO> listWorkerTodoTasks(@Param("workerId") Long workerId,
                                               @Param("limit") Integer limit);

    @Select("SELECT id, quantity, available_quantity FROM wms_inventory WHERE warehouse_id = #{warehouseId} AND model_id = #{modelId} " +
            "AND (bin_id = #{binId} OR (bin_id IS NULL AND #{binId} IS NULL)) " +
            "AND (motorcycle_id = #{motorcycleId} OR (motorcycle_id IS NULL AND #{motorcycleId} IS NULL)) LIMIT 1")
    InventoryLiteVO findInventory(@Param("warehouseId") Long warehouseId,
                                  @Param("modelId") Long modelId,
                                  @Param("binId") Long binId,
                                  @Param("motorcycleId") Long motorcycleId);

    void insertWorkTask(WorkTaskInsertVO task);

    void updateInboundOrderStatusToPending(@Param("orderId") Long orderId, @Param("operator") String operator);

    int startTask(@Param("taskId") Long taskId, @Param("operatorId") Long operatorId);

    void insertCheckpoint(@Param("taskId") Long taskId,
                          @Param("checkpointType") Integer checkpointType,
                          @Param("scanContent") String scanContent,
                          @Param("expectedContent") String expectedContent,
                          @Param("verifyResult") Integer verifyResult,
                          @Param("operatorId") Long operatorId,
                          @Param("deviceId") String deviceId,
                          @Param("locationInfo") String locationInfo,
                          @Param("remark") String remark,
                          @Param("scanTime") LocalDateTime scanTime);

    int completeTask(@Param("taskId") Long taskId, @Param("operatorId") Long operatorId);

    int abandonTask(@Param("taskId") Long taskId,
                    @Param("operatorId") Long operatorId,
                    @Param("reason") String reason,
                    @Param("abandonTime") LocalDateTime abandonTime);

    int updateInventory(@Param("id") Long id,
                        @Param("delta") Integer delta,
                        @Param("operator") String operator,
                        @Param("inboundTime") LocalDateTime inboundTime);

    void insertInventory(@Param("warehouseId") Long warehouseId,
                         @Param("modelId") Long modelId,
                         @Param("binId") Long binId,
                         @Param("motorcycleId") Long motorcycleId,
                         @Param("quantity") Integer quantity,
                         @Param("operator") String operator,
                         @Param("inboundTime") LocalDateTime inboundTime);

    void insertMovement(@Param("warehouseId") Long warehouseId,
                        @Param("modelId") Long modelId,
                        @Param("motorcycleId") Long motorcycleId,
                        @Param("vin") String vin,
                        @Param("beforeQuantity") Integer beforeQuantity,
                        @Param("afterQuantity") Integer afterQuantity,
                        @Param("targetBinId") Long targetBinId,
                        @Param("taskId") Long taskId,
                        @Param("orderNo") String orderNo,
                        @Param("operatorId") Long operatorId,
                        @Param("operatorName") String operatorName,
                        @Param("movementTime") LocalDateTime movementTime,
                        @Param("remark") String remark);

    void incrementInboundOrderActualQuantity(@Param("orderId") Long orderId,
                                             @Param("quantity") Integer quantity,
                                             @Param("operator") String operator,
                                             @Param("actualTime") LocalDateTime actualTime);

    void refreshInboundOrderStatus(@Param("orderId") Long orderId, @Param("operator") String operator);
}
