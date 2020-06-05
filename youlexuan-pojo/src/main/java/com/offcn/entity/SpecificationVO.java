package com.offcn.entity;


import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * 组合实体类，用来接收entity
 * VO：view object 视图对象 ， 接收或返回页面数据
 * {"specification":{},"specificationOptionList":[]}
 */
public class SpecificationVO implements Serializable {

    TbSpecification specification;//规格

    List<TbSpecificationOption> specificationOptionList;//规格选项


    public TbSpecification getSpecification() {
        return specification;
    }
    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }
    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }
    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
