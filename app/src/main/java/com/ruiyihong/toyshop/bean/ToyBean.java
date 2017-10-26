package com.ruiyihong.toyshop.bean;

/**
 * Created by 李晓曼 on 2017/7/7.
 * url: index/Xiang/xiang
 Method : POST
 Par : id(玩具的ID)
 Callback:略
 Id->ID
 name -> 商品名
 shoptext->商品描述
 shopimg->商品图片
 shopprice->商品价格
 classname->分类
 isState->是否显示在主页0或者1
 suitage->适合年龄
 dpj->掉牌价
 brand->玩具品牌
 brandplace->品牌所属地
 material->玩具材质
 weight->玩具重量
 spec->包装规格
 disinfect->消毒方式
 ability->锻炼能力描述
 feature->玩具特色
 showImg->玩具展示
 detail->玩具细节
 isimg->轮播图显示在主页
 time->时间
 */

public class ToyBean {
    private int id;
    private String name;
    private String shoptext;
    private String dpj;
    private String shopimg;
    private String shopprice;
    private String classname;
    private int isState;
    private String suitage;
    private String brand;
    private String brandplace;
    private String material;
    private String weight;
    private String spec;
    private String disinfect;
    private String ability;
    private String feature;
    private String showImg;
    private String detail;
    private int isimg;
    private String time;
    private int kcl;

    public int getKcl() {
        return kcl;
    }

    public void setKcl(int kcl) {
        this.kcl = kcl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShoptext() {
        return shoptext;
    }

    public void setShoptext(String shoptext) {
        this.shoptext = shoptext;
    }

    public String getShopimg() {
        return shopimg;
    }

    public void setShopimg(String shopimg) {
        this.shopimg = shopimg;
    }

    public String getShopprice() {
        return shopprice;
    }

    public void setShopprice(String shopprice) {
        this.shopprice = shopprice;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public int getIsState() {
        return isState;
    }

    public void setIsState(int isState) {
        this.isState = isState;
    }

    public String getSuitage() {
        return suitage;
    }

    public void setSuitage(String suitage) {
        this.suitage = suitage;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrandplace() {
        return brandplace;
    }

    public void setBrandplace(String brandplace) {
        this.brandplace = brandplace;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSpec() {
        return spec;
    }

    public String getDpj() {
        return dpj;
    }

    public void setDpj(String dpj) {
        this.dpj = dpj;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getDisinfect() {
        return disinfect;
    }

    public void setDisinfect(String disinfect) {
        this.disinfect = disinfect;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getShowImg() {
        return showImg;
    }

    public void setShowImg(String showImg) {
        this.showImg = showImg;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getIsimg() {
        return isimg;
    }

    public void setIsimg(int isimg) {
        this.isimg = isimg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ToyBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shoptext='" + shoptext + '\'' +
                ", shopimg='" + shopimg + '\'' +
                ", shopprice='" + shopprice + '\'' +
                ", classname='" + classname + '\'' +
                ", isState=" + isState +
                ", suitage='" + suitage + '\'' +
                ", brand='" + brand + '\'' +
                ", brandplace='" + brandplace + '\'' +
                ", material='" + material + '\'' +
                ", weight='" + weight + '\'' +
                ", spec='" + spec + '\'' +
                ", disinfect='" + disinfect + '\'' +
                ", ability='" + ability + '\'' +
                ", feature='" + feature + '\'' +
                ", showImg='" + showImg + '\'' +
                ", detail='" + detail + '\'' +
                ", isimg=" + isimg +
                ", time='" + time + '\'' +
                '}';
    }
}
