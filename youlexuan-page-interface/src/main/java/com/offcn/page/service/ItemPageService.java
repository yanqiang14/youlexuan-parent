package com.offcn.page.service;

public interface ItemPageService {

    /**
     * 根据指定商品spuid生成静态页  spuId.html
     * @param id spuId
     */
    void genItemHtml(Long id);

    void deleteItemHtml(Long id);
}
