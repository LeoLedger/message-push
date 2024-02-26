package hw.topevery.logic;

import hw.topevery.dao.DataPushParamsDao;
import hw.topevery.framework.db.base.BaseEntityDao;
import hw.topevery.framework.db.base.BaseEntityLogic;
import hw.topevery.pojo.po.DgDataPushParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据推送参数 Logic
 *
 * @author LeoLedger
 * @date 2023-12-23
 */
@Service
public class DataPushParamsLogic implements BaseEntityLogic<DgDataPushParams, Integer> {

    @Autowired
    private DataPushParamsDao dataPushParamsDao;

    @Override
    public BaseEntityDao<DgDataPushParams, Integer> getDao() {
        return dataPushParamsDao;
    }

    public List<DgDataPushParams> findAll() {
        return dataPushParamsDao.findAll();
    }
}
