package com.wonders.sjic.service;

import com.wonders.sjic.entity.InterfaceInfoBo;

import java.util.Map;

public interface  _BaseService {
    Ret execute(InterfaceInfoBo infoBo,Map<String,Object> params);
}
