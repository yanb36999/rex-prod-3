package com.zmcsoft.rex.workflow.illegal.impl.dao;

import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalReportLog;
import com.zmcsoft.rex.workflow.illegal.api.entity.ReportIllegalDetail;
import org.apache.ibatis.annotations.Param;
import org.hswebframework.web.commons.entity.Entity;
import org.hswebframework.web.dao.CrudDao;

import java.util.List;

public interface CwtReportIllegalDetailDao extends CrudDao<ReportIllegalDetail, String> {
    List<IllegalReportLog> selectLogsByReportId(String reportId);

    List<ReportIllegalDetail> queryFromRex(Entity entity);

   int countFromRex(Entity entity);

   int updateStatus(@Param("reportId") String reportId,
                    @Param("plateType") String plateType,
                    @Param("plateNumber")String plateNumber,
                    @Param("status") String status);

   int updateCheckFailStatus(@Param("reportId") String reportId,
                          @Param("plateType") String plateType,
                          @Param("plateNumber")String plateNumber,
                          @Param("status") String status);

   int acciptReport(@Param("reportId") String reportId);

   int acceptReportCar(@Param("reportId") String reportId,@Param("plateNo") String plateNo,@Param("plateType") String plateType);
}
