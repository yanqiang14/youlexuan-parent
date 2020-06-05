package com.offcn.search.service;

import com.offcn.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * solr的全文检索
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map<String,Object> searchMap);


    void importItemList(List<TbItem> list);

    void removeSkuList(Long[] ids);

}
