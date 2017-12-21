package com.wonders.tiles.extend.setup;

import com.wonders.tiles.autocrud.cacher.AutoCrudCache;
import com.wonders.tiles.dic.DicConfigManager;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.mvc.annotation.Filters;

@Filters
public class ConfigSetup implements Setup {

	public void init(NutConfig nc) {
		Ioc ioc = nc.getIoc();
		DicConfigManager manager = ioc.get(DicConfigManager.class);
		manager.loadAllDic();

		// 加载增删改查配置表
		AutoCrudCache autoCrudCache = ioc.get(AutoCrudCache.class);
		autoCrudCache.load();

/*		// 启动定时器
		XmlSchedule xmlSchedule = ioc.get(XmlSchedule.class);
		xmlSchedule.startSchedule();
		JsonSchedule jsonSchedule = ioc.get(JsonSchedule.class);
		jsonSchedule.startSchedule();
		EchartsSchedule echartsSchedule = ioc.get(EchartsSchedule.class);
		echartsSchedule.startSchedule();
		ApiSchedule apiSchedule = ioc.get(ApiSchedule.class);
		apiSchedule.startSchedule();
		SjtbSchedule sjtbSchedule = ioc.get(SjtbSchedule.class);
		sjtbSchedule.startSchedule();
		SmsSchedule smsSchedule = ioc.get(SmsSchedule.class);
		smsSchedule.startSchedule();
		BuildingSchedule buildingSchedule = ioc.get(BuildingSchedule.class);
		buildingSchedule.startSchedule();
		CommunityAcceptanceSchedule communityAcceptanceSchedule = ioc.get(CommunityAcceptanceSchedule.class);*/
//		communityAcceptanceSchedule.startSchedule();
//		QfbWangtingSchedule qfbWangtingSchedule = ioc.get(QfbWangtingSchedule.class);
//		qfbWangtingSchedule.startSchedule();
//		HbjHuanjingjianceSchedule hbjHuanjingjianceSchedule = ioc.get(HbjHuanjingjianceSchedule.class);
//		hbjHuanjingjianceSchedule.startSchedule();
//		MzjYiliaoSchedule mzjYiliaoSchedule = ioc.get(MzjYiliaoSchedule.class);
//		mzjYiliaoSchedule.startSchedule();
//		TzbShenghuoSchedule tzbShenghuoSchedule = ioc.get(TzbShenghuoSchedule.class);
//		tzbShenghuoSchedule.startSchedule();
	}

	public void destroy(NutConfig nc) {
//		Ioc ioc = nc.getIoc();

//		// 停止定时器
//		XmlSchedule xmlSchedule = ioc.get(XmlSchedule.class);
//		xmlSchedule.shutdownSchedule();
//    JsonSchedule jsonSchedule = ioc.get(JsonSchedule.class);
//		jsonSchedule.shutdownSchedule();
//    EchartsSchedule echartsSchedule = ioc.get(EchartsSchedule.class);
//		echartsSchedule.shutdownSchedule();
//    SmsSchedule smsSchedule = ioc.get(SmsSchedule.class);
//		smsSchedule.shutdownSchedule();
//    BuildingSchedule buildingSchedule = ioc.get(BuildingSchedule.class);
//		buildingSchedule.shutdownSchedule();
//    CommunityAcceptanceSchedule communityAcceptanceSchedule = ioc.get(CommunityAcceptanceSchedule.class);
//		communityAcceptanceSchedule.shutdownSchedule();
//    QfbWangtingSchedule qfbWangtingSchedule = ioc.get(QfbWangtingSchedule.class);
//		qfbWangtingSchedule.shutdownSchedule();
//    HbjHuanjingjianceSchedule hbjHuanjingjianceSchedule = ioc.get(HbjHuanjingjianceSchedule.class);
//		hbjHuanjingjianceSchedule.shutdownSchedule();
//    MzjYiliaoSchedule mzjYiliaoSchedule = ioc.get(MzjYiliaoSchedule.class);
//		mzjYiliaoSchedule.shutdownSchedule();
//    TzbShenghuoSchedule tzbShenghuoSchedule = ioc.get(TzbShenghuoSchedule.class);
//		tzbShenghuoSchedule.shutdownSchedule();
//    ApiSchedule apiSchedule = ioc.get(ApiSchedule.class);
//		apiSchedule.shutdownSchedule();
}

}
