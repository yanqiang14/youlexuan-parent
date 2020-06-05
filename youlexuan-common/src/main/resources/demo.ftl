<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>

<#assign str="hello fm" />
<#assign str2="{'id':1,'name':'tom'}" />

<h1>欢迎使用网页静态化技术 </h1><br>
<#include "common.ftl" />

<#--注释-->
${username}
${userage}

<br>
${str}<br>
${str2} <br>

<#--${str3}-->
<#if bol>
bol=true
<#else>
bol=false
</#if>

<br>

    <#list stuList as stu>
        ${stu.name}
    </#list>

    stuList的长度：${stuList?size}

<br>

${str2}

<#assign info=str2?eval />

${info.id}
${info.name}

<br>
${count}
<br>
${count?c}

<br>
${date?datetime}
${date?date}
${date?time}
${date?string("yyyy年MM月dd日")}

</body>
</html>