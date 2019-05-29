package com.zmcsoft.rex.tmb.illegal.service;

import com.zmcsoft.rex.tmb.illegal.entity.NunciatorInfo;

import java.util.List;

/**
 * @author zhouhao
 * @since 1.0
 */
public interface NunciatorService {

    /**
     * 根据告知单位编码获取告知人信息
     *
     * @param orgCode 单位编码
     * @return 告知人信息, 不存在则返回<code>null</code>
     */
    NunciatorInfo getByOrgCode(String orgCode);

    /**
     * 根据告知单位编码获取告知人信息,并可指定是否返回默认告知人
     *
     * @param orgCode                 单位编码
     * @param returnDefaultIfNotFound 当未找到告知人信息时,是否返回默认告知人
     * @return 告知人信息, 不存在则返回默认告知人
     * @see {{@link #getDefault()}}
     */
    NunciatorInfo getByOrgCode(String orgCode, boolean returnDefaultIfNotFound);

    /**
     * 获取全部告知人信息
     *
     * @return 全部告知人信息
     */
    List<NunciatorInfo> getAll();

    /**
     * 获取默认的告知人信息
     *
     * @return 默认的告知人
     */
    NunciatorInfo getDefault();
}
