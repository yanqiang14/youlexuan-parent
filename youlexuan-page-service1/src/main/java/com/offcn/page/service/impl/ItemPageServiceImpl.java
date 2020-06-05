package com.offcn.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    String pageDir ;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Override
    public void genItemHtml(Long id) {
        Configuration configuration = freemarkerConfig.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");
            Map dataModel = new HashMap<>();

            // 1、goods  2、 goodsDesc  3、skuList
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            dataModel.put("goods",tbGoods);
            dataModel.put("goodsDesc",goodsDescMapper.selectByPrimaryKey(id));
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            dataModel.put("skuList",itemMapper.selectByExample(example));

            //三个商品分类名称
            dataModel.put("catagory1Name",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName());
            dataModel.put("catagory2Name",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName());
            dataModel.put("catagory3Name",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName());

            FileWriter writer = new FileWriter(new File(pageDir + id + ".html"));

            template.process(dataModel,writer);

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteItemHtml(Long id) {
        new File(pageDir + id + ".html").delete();
    }
}
