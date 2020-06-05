package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;


@Service(timeout = 30000)
public class ItemSearchServiceImpl implements ItemSearchService {


    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    RedisTemplate redisTemplate;



    /**
     * 根据商品分类名称从redis中读取品牌和规格
     * @param categoryName 商品分类的名称
     * @return
     */
    private Map findBrandAndSpecByCategoryName(String categoryName){

        // typeId 模板id
        Long typeId = (Long)redisTemplate.boundHashOps("categoryHash").get(categoryName);

        // 根据模板id取出品牌和规格

        Map map = new HashMap();

        if(typeId!=null){
            List brandList = (List)redisTemplate.boundHashOps("brandHash").get(typeId);
            List specList = (List)redisTemplate.boundHashOps("specHash").get(typeId);

            map.put("brandList",brandList);// 【{id，text}】
            map.put("specList",specList);// 【{}，{id，text，options}】
        }

        return map;
    }

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {

        //处理关键词空格
        String keywords = (String)searchMap.get("keywords");
        if(keywords!=null && keywords.length()>0){
            keywords = keywords.replaceAll(" ","");
            searchMap.put("keywords",keywords);
        }

        Map<String, Object> result = new HashMap<>();

        //1、根据关键词查询
        result.putAll(searchList(searchMap));//rows -- list(每一个tb_item中的title字段具有高亮的前后缀)

        //2、分类名称
        List list = searchCategoryList(searchMap);
        result.put("categoryList",list);// categoryList list(商品分类的名称)

        //3、 品牌和规格
        String category = (String) searchMap.get("category");
        if(category.equals("")){
            //没有传递 商品分类  条件
            if(list!=null && list.size()>0){
                Map map = findBrandAndSpecByCategoryName((String) list.get(0));
                result.putAll(map);
            }
        }else{
            Map map = findBrandAndSpecByCategoryName(category);
            result.putAll(map);
        }


        return result;//rows categoryList brandList  specList
    }

    @Override
    public void importItemList(List<TbItem> list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void removeSkuList(Long[] ids) {
        // "item_goodsid": 1,

        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(Arrays.asList(ids));

        query.addCriteria(criteria);

        solrTemplate.delete(query);
        solrTemplate.commit();

    }

    private Map searchList(Map<String, Object> searchMap){


        Map<String, Object> result = new HashMap<>();

        SimpleHighlightQuery query = new SimpleHighlightQuery();//高亮查询的query对象

        //设置高亮的参数（字段，前缀，后缀）
        HighlightOptions options = new HighlightOptions();
        options.addField("item_title");
        options.setSimplePrefix("<font color='red'>");
        options.setSimplePostfix("</font>");

        query.setHighlightOptions(options);

        //1、keywords查询条件
        String keywords = (String) searchMap.get("keywords");
        if(keywords!=null && keywords.length()>0){
            //处理空格
            keywords = keywords.replaceAll(" ","");
            Criteria criteria = new Criteria("item_keywords").is(keywords);
            query.addCriteria(criteria);
        }

        //2、分类
        String category = (String)searchMap.get("category");
        if(!category.equals("")){
            //过滤查询
            Criteria c2 = new Criteria("item_category").is(category);
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(c2);
//            query.addCriteria(filterQuery);
            query.addFilterQuery(filterQuery);
        }

        //3、品牌
        String brand = (String)searchMap.get("brand");
        if(!brand.equals("")){
            //过滤查询
            Criteria c3 = new Criteria("item_brand").is(brand);
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(c3);
//            query.addCriteria(filterQuery);
            query.addFilterQuery(filterQuery);
        }

        //4、规格
        Map spec = (Map) searchMap.get("spec");// {}
        if(spec!=null){
            for(Object key : spec.keySet()){
                String field = "item_spec_"+ key;//规格名称
                String option  = (String)spec.get(key);//规格选项

                Criteria c4 = new Criteria(field).is(option);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(c4);
                query.addFilterQuery(filterQuery);
            }
        }

        // 5、价格区间的条件
        String price = (String) searchMap.get("price");// 500-1000   3000-*
        if(price!=null && price.length()>0){
            String[] split = price.split("-");
            String left = split[0];
            String right = split[1];
            Criteria c = new Criteria("item_price").greaterThanEqual(left);
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(c);
            query.addFilterQuery(filterQuery);

            if(!right.equals("*")){
                Criteria c2 = new Criteria("item_price").lessThanEqual(right);
                SimpleFilterQuery filterQuery2 = new SimpleFilterQuery(c2);
                query.addFilterQuery(filterQuery2);
            }

        }

        // 6、排序
        Map map = (Map)searchMap.get("sort");//"sort":{"item_price":"desc",'updateTime':''}
        if(map!=null){
            for (Object field : map.keySet()){
                String dir = (String)map.get(field);// asc desc
                Sort.Direction direction = Sort.Direction.fromString(dir);
                Sort sort = new Sort(direction, (String) field);
                query.addSort(sort);
            }
        }


        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        List<TbItem> list = page.getContent();//这个list集合是  docs

        for (TbItem item : list) {
            List<HighlightEntry.Highlight> highlights = page.getHighlights(item);//

            //判断
            if(highlights!=null && highlights.size()>0){
                List<String> snipplets = highlights.get(0).getSnipplets();
                //"东芝（TOSHIBA） 39L2303C 39英寸 高清LED 液晶<font color='red'>电视</font>（黑色）"
                String s = snipplets.get(0);
                item.setTitle(s);//将带有高亮前后缀的tile赋值给tb_item
            }
        }

        result.put("rows",list);
        return result;
    }


    /**
     * 根据keywords查询，按照商品分类分组，取出每一组的组名（商品分类的名称），形成一个List集合
     * @param searchMap
     * @return
     */
    private List searchCategoryList(Map<String, Object> searchMap){

        List<String> categoryList = new ArrayList<>();

        SimpleQuery query = new SimpleQuery();
        //根据keyword查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //按照商品分类进行分组

        GroupOptions groupOptions = new GroupOptions();//分组对象
        groupOptions.addGroupByField("item_category");//设置分组的字段

        query.setGroupOptions(groupOptions);

        //query查询方法
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);

        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");//获取分组结果

        List<GroupEntry<TbItem>> list = groupResult.getGroupEntries().getContent();//分组之后的集合

        for (GroupEntry<TbItem> entry : list) {
            String categoryName = entry.getGroupValue();//组名(分类的名称)
//            Page<TbItem> result = entry.getResult();
            categoryList.add(categoryName);
        }

        return categoryList;
    }

}
