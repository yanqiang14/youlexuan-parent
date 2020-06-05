package com.offcn.entity;

import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsDesc;
import com.offcn.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

public class GoodsVO implements Serializable {

    TbGoods goods;

    TbGoodsDesc goodsDesc;

    List<TbItem> skuList;

    public List<TbItem> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<TbItem> skuList) {
        this.skuList = skuList;
    }

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }
}
