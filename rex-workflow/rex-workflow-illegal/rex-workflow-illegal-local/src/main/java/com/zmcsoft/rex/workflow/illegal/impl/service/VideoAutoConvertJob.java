package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.video.VideoService;
import org.hswebframework.ezorm.rdb.executor.SqlExecutor;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;

public class VideoAutoConvertJob {

    @Autowired
    private VideoService videoService;


    @Autowired
    private SqlExecutor sqlExecutor;


    public void run() {
        try {
            sqlExecutor.list("select f_spdz_ys from t_illegal_user_order");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
