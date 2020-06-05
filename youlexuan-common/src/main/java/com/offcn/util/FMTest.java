package com.offcn.util;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class FMTest {
    public static void main(String[] args) throws Exception{
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板所在路径
        configuration.setDirectoryForTemplateLoading(new File("C:\\Users\\Administrator\\IdeaProjects\\youlexuan-parent\\youlexuan-common\\src\\main\\resources"));
        configuration.setDefaultEncoding("utf-8");

        Template template = configuration.getTemplate("demo.ftl");//模板
        //模型数据
        Map dataModel = new HashMap<>();
        dataModel.put("username","offcn");
        dataModel.put("userage","21");
        dataModel.put("bol",false);

        Map map1 = new HashMap();
        map1.put("name","tom");

        Map map2 = new HashMap();
        map2.put("name","tim");

        List<Map> stuList = new ArrayList<>();
        stuList.add(map1);
        stuList.add(map2);

        dataModel.put("stuList",stuList);

        dataModel.put("count",123456789);
        dataModel.put("date",new Date());

        FileWriter fileWriter = new FileWriter(new File("C:\\Users\\Administrator\\IdeaProjects\\youlexuan-parent\\youlexuan-common\\src\\main\\resources\\offcn.html"));

        template.process(dataModel,fileWriter);

        fileWriter.close();

    }
}
