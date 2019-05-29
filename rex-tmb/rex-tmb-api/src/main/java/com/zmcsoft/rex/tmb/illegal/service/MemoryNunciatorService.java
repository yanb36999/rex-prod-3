package com.zmcsoft.rex.tmb.illegal.service;

import com.zmcsoft.rex.tmb.illegal.entity.Nunciator;
import com.zmcsoft.rex.tmb.illegal.entity.NunciatorInfo;
import org.hswebframework.utils.time.DateFormatter;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhouhao
 */
public class MemoryNunciatorService implements NunciatorService {

    private static Map<String, NunciatorInfo> repo;

    private static NunciatorInfo defaultInfo;

    //TODO: TO DATABASE

    static {
        String[] data = ("510101000000\t005374\t张节\t071424\t顾锦梅\t2012-01-25\t2022-01-01\t武侯区\t交警一分局\t成都市高新区科园一路6号\t85130122\t成都市公安局交通管理局\t武侯区人民法院\t成都市公安局交通管理局第一分局\n" +
                "510102000000\t005402\t徐晶晶\t005403\t王嘉\t2012-01-01\t2022-01-01\t金牛区\t交警二分局\t成都市金牛区马鞍山路1号\t83332604\t成都市公安局交通管理局\t金牛区人民法院\t成都市公安局交通管理局第二分局\n" +
                "510103000000\t005426\t郑涛\t005425\t李纪红\t2012-01-01\t2022-01-01\t锦江区\t交警三分局\t成都市锦江区东怡街50号\t84412780\t成都市公安局交通管理局\t锦江区人民法院\t成都市公安局交通管理局第三分局\n" +
                "510104000000\t005452\t李琳玲\t004968\t严燕\t2012-01-01\t2022-01-01\t青羊区\t交警四分局\t成都市金牛区蜀西环街36号\t87771111\t成都市公安局交通管理局\t金牛区人民法院\t成都市公安局交通管理局第四分局\n" +
                "510105000000\t075790\t江月\t071324\t高瑜蓉\t2012-01-01\t2022-01-01\t成华区\t交警五分局\t成都市成华区桃蹊路53号\t83245050\t成都市公安局交通管理局\t成华区人民法院\t成都市公安局交通管理局第五分局\n" +
                "510106000000\t091526\t邢丽\t005400\t李雪\t2012-01-01\t2022-01-01\t高新区\t交警六分局\t成都市金牛区外西土桥兴科北路155号\t87591000\t成都市公安局交通管理局\t高新区人民法院\t成都市公安局交通管理局第六分局\n" +
                "510107000000\t078443\t陶俊\t078385\t叶凯\t2012-01-01\t2022-01-01\t天府新区\t交警七分局\t成都市天府新区万安镇和韵南路333号\t85657921\t成都市公安局交通管理局\t天府新区人民法院\t成都市公安局交通管理局第七分局\n" +
                "510112000000\t017853\t宋希良\t017174\t李有斌\t2012-01-01\t2022-01-01\t龙泉驿区\t龙泉驿区大队\t龙泉镇建材路2号\t84840636\t成都市公安局龙泉驿区公安分局\t龙泉驿区人民法院\t成都市公安局龙泉驿区分局交通警察大队\n" +
                "510113000000\t012404\t张毅妮\t012608\t温鹏\t2012-01-01\t2022-01-01\t青白江区\t青白江区大队\t成都市青白江华金大道689号\t83665039\t成都市公安局青白江区公安分局\t青白江区人民法院\t成都市公安局青白江区分局交通警察大队\n" +
                "510114000000\t075838\t梁世平\t013113\t王继跃\t2012-01-01\t2022-01-01\t新都区\t新都区大队\t成都市新都区新都镇蓉都大道560号\t83966344\t成都市公安局新都区公安分局\t新都区人民法院\t成都市公安局新都区分局交通警察大队\n" +
                "510115000000\t072709\t田飞\t012795\t陈太安\t2012-01-01\t2022-01-01\t温江区\t温江区大队\t成都市温江区战备渠南路66号\t82722388\t成都市公安局温江区公安分局\t温江区人民法院\t成都市公安局温江区分局交通警察大队\n" +
                "510121000000\t074935\t骆欢\t074944\t陈冬\t2012-01-01\t2022-01-01\t金堂县\t金堂县大队\t金堂县赵镇滨江路465号\t84920260\t成都市公安局龙泉驿区公安分局\t龙泉驿区人民法院\t成都市公安局金堂县公安局交通警察大队\n" +
                "510122000000\t013809\t张静\t013819\t侯川\t2012-01-01\t2022-01-01\t双流区\t双流区大队\t双流县东升镇北坛路110号\t85770110\t成都市公安局双流区公安分局\t双流区人民法院\t成都市公安局双流区分局交通警察大队\n" +
                "510185000000\t062238\t辜伟\t062193\t曾荣华\t2012-01-01\t2022-01-01\t简阳市\t简阳市交警大队\t简阳市石桥镇回龙寺村3组\t27276318\t成都市公安局简阳市公安分局\t简阳市人民法院\t成都市公安局简阳市公安局交通警察大队\n" +
                "510124000000\t016441\t曾洪斌\t016435\t许春梅\t2012-01-01\t2022-01-01\t郫都区\t郫都区大队\t郫都区郫筒镇望丛西路466号\t87862249\t成都市公安局郫都区公安分局\t郫都区人民法院\t成都市公安局郫都区分局交通警察大队\n" +
                "510129000000\t016595\t张开良\t016794\t鲁艳茹\t2012-01-01\t2022-01-01\t大邑县\t大邑县大队\t大邑县晋原镇富民路中段1号\t88292122\t成都市公安局大邑县公安分局\t大邑县人民法院\t成都市公安局大邑县公安局交通警察大队\n" +
                "510131000000\t074537\t许婷\t014616\t马彦勋\t2012-01-31\t2022-01-01\t蒲江县\t蒲江县大队\t蒲江县朝阳大道312号\t88550899\t成都市公安局浦江县公安分局\t浦江县人民法院\t成都市公安局蒲江县公安局交通警察大队\n" +
                "510132000000\t017112\t陈林建\t017118\t何亚军\t2012-01-01\t2022-01-01\t新津县\t新津县大队\t新津县五津镇五津西路16号\t82524516\t成都市公安局新津县公安分局\t新津县人民法院\t成都市公安局新津县公安局交通警察大队\n" +
                "510181000000\t017728\t袁兰兰\t017730\t王慧宇\t2012-01-01\t2022-01-01\t都江堰市\t都江堰市大队\t都江堰市学府路二段285号\t87203122\t成都市公安局都江堰市公安分局\t都江堰市人民法院\t成都市公安局都江堰市公安局交通警察大队\n" +
                "510182000000\t014422\t杨林\t014521\t陈志根\t2012-01-01\t2022-01-01\t彭州市\t彭州市大队\t彭州市天彭镇繁江北路108号\t83872800\t成都市公安局彭州市公安分局\t彭州市人民法院\t成都市公安局彭州市公安局交通警察大队\n" +
                "510183000000\t015197\t赵俊华\t015260\t赵娜\t2012-01-01\t2022-01-01\t邛崃市\t邛崃市大队\t邛崃市文南路496号\t88742071\t成都市公安局邛崃市公安分局\t邛崃市人民法院\t成都市公安局邛崃市公安局交通警察大队\n" +
                "510184000000\t014893\t孙照辉\t014924\t干军\t2012-01-01\t2022-01-01\t崇州市\t崇州市大队\t崇州市崇阳镇蜀州路世纪广场侧\t82213060\t成都市公安局崇州市公安分局\t崇州市人民法院\t成都市公安局崇州市公安局交通警察大队").split("\n");

        List<BiConsumer<NunciatorInfo, String>>
                consumers = Arrays.asList(
                NunciatorInfo::setOrgCode,
                NunciatorInfo::setPoliceCode,
                NunciatorInfo::setName,
                NunciatorInfo::setPoliceCode2,
                NunciatorInfo::setName2,
                (info, date) -> info.setStartWith(DateFormatter.fromString(date)),
                (info, date) -> info.setEndWith(DateFormatter.fromString(date)),
                NunciatorInfo::setDistrictName,
                NunciatorInfo::setOrgName,
                NunciatorInfo::setAddress,
                NunciatorInfo::setPhone,
                NunciatorInfo::setAgainOffice,
                NunciatorInfo::setCourt,
                NunciatorInfo::setFullName
        );

        repo = Arrays.stream(data).map(str -> str.split("\t"))
                .map(arr -> {
                    NunciatorInfo info = new NunciatorInfo();
                    for (int i = 0; i < arr.length; i++) {
                        consumers.get(i).accept(info, arr[i]);
                    }
                    return info;
                }).collect(Collectors.toMap(NunciatorInfo::getOrgCode, Function.identity()));
        defaultInfo = repo.get("510106000000");
    }

    @Override
    public NunciatorInfo getByOrgCode(String orgCode) {
        if (orgCode == null) {
            return null;
        }
        if (orgCode.length() <= 6) {
            orgCode = orgCode.concat("000000");
        }
        return repo.get(orgCode);
    }

    @Override
    public NunciatorInfo getByOrgCode(String orgCode, boolean returnDefaultIfNotFound) {
        if (orgCode.length() <= 6) {
            orgCode = orgCode.concat("000000");
        }
        //临时处理，由于目前机构只到二级，对于二级以下的机构信息默认取二级机构的
        if(!orgCode.substring(6).equals("000000")){
            orgCode = orgCode.substring(0,6).concat("000000");
        }
        return repo.getOrDefault(orgCode, returnDefaultIfNotFound ? defaultInfo : null);
    }

    @Override
    public List<NunciatorInfo> getAll() {
        return new ArrayList<>(repo.values());
    }

    @Override
    public NunciatorInfo getDefault() {
        return defaultInfo;
    }


}
