package com.zmcsoft.rex.workflow.illegal.api.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportIllegalDetail {
    private ReportIllegalCar illegalCar;

    private List<ReportIllegalInfo> illegalInfo;

    private ReportInfo reportInfo;

    private List<IllegalReportLog> reportLogs;
}
