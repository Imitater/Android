package com.ruiyihong.toyshop.bean.mine;

import java.util.List;

/**
 * Created by 81521 on 2017/8/28.
 * 订单 bean
 */

public class OrderDataBean {


    /**
     * status : 1
     * data : [{"uid":26,"did":"3d2892fab1448e4d","shu":2,"jia":4512,"id":13,"name":"小小玩具-4","shoptext":"开发智力","shopimg":"20170809\\0611518a3fd059e0c23d4766a9d13736.png","shopprice":4512,"classname":"科普知识","isState":0,"istui":1,"suitage":"2岁-3岁","brand":"凯迪拉克1","brandplace":"德州","cname":"秦皇岛","material":"塑料","weight":"6g","spec":"20cm*20cm*20cm","disinfect":"酒精","ability":"<p>锻炼宝宝学习能力110<\/p>","feature":"<p>益智<\/p>","showImg":"","detail":"<p>1<\/p>","yhprice":"11","yhendtime":"","yhstartime":"1502208000","isdiscount":0,"isimg":1,"Uid":"","liulan":30,"xiaoshou":"23","shoucang":"23","read":"23","dpj":1,"time":"1502278675","isbw":0,"kcl":345,"longtime":"34","lbb":434,"ischange":0,"dhcs":0,"isnew":1,"ptime":"1","uaddr":"河北省秦皇岛市海港区G205(山深线)汇佳幼儿园123","unames":"a 女士","uphone":"18033551952","spnum":3,"expense":10,"total":10},{"uid":26,"did":"3d2892fab1448e4d","shu":1,"jia":4512,"id":15,"name":"小小玩具-6","shoptext":"开发智力","shopimg":"20170724\\2347a250c257b8ad9d6636ddce3867a9.png","shopprice":4512,"classname":"经典故事","isState":1,"istui":1,"suitage":"1岁半-2岁","brand":"奔驰","brandplace":"德州","cname":"上海","material":"塑料","weight":"6g","spec":"20cm*20cm*20cm","disinfect":"酒精","ability":"锻炼宝宝学习能力111","feature":"益智","showImg":"","detail":"1","yhprice":"34","yhendtime":"","yhstartime":"","isdiscount":0,"isimg":1,"Uid":"","liulan":133,"xiaoshou":"23","shoucang":"23","read":"23","dpj":0,"time":"1500860120","isbw":0,"kcl":53,"longtime":"2天","lbb":500,"ischange":1,"dhcs":18,"isnew":1,"ptime":"1","uaddr":"河北省秦皇岛市海港区G205(山深线)汇佳幼儿园123","unames":"a 女士","uphone":"18033551952","spnum":3,"expense":10,"total":10}]
     */

    private int status;
    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * uid : 26
         * did : 3d2892fab1448e4d
         * shu : 2
         * jia : 4512
         * id : 13
         * name : 小小玩具-4
         * shoptext : 开发智力
         * shopimg : 20170809\0611518a3fd059e0c23d4766a9d13736.png
         * shopprice : 4512
         * classname : 科普知识
         * isState : 0
         * istui : 1
         * suitage : 2岁-3岁
         * brand : 凯迪拉克1
         * brandplace : 德州
         * cname : 秦皇岛
         * material : 塑料
         * weight : 6g
         * spec : 20cm*20cm*20cm
         * disinfect : 酒精
         * ability : <p>锻炼宝宝学习能力110</p>
         * feature : <p>益智</p>
         * showImg :
         * detail : <p>1</p>
         * yhprice : 11
         * yhendtime :
         * yhstartime : 1502208000
         * isdiscount : 0
         * isimg : 1
         * Uid :
         * liulan : 30
         * xiaoshou : 23
         * shoucang : 23
         * read : 23
         * dpj : 1
         * time : 1502278675
         * isbw : 0
         * kcl : 345
         * longtime : 34
         * lbb : 434
         * ischange : 0
         * dhcs : 0
         * isnew : 1
         * ptime : 1
         * uaddr : 河北省秦皇岛市海港区G205(山深线)汇佳幼儿园123
         * unames : a 女士
         * uphone : 18033551952
         * spnum : 3
         * expense : 10
         * total : 10
         * type:0
         */

        private int uid;
        private String did;
        private int shu;
        private int jia;
        private int id;
        private String name;
        private String shoptext;
        private String shopimg;
        private int shopprice;
        private String classname;
        private int isState;
        private int istui;
        private String suitage;
        private String brand;
        private String brandplace;
        private String cname;
        private String material;
        private String weight;
        private String spec;
        private String disinfect;
        private String ability;
        private String feature;
        private String showImg;
        private String detail;
        private String yhprice;
        private String yhendtime;
        private String yhstartime;
        private int isdiscount;
        private int isimg;
        private String Uid;
        private int liulan;
        private String xiaoshou;
        private String shoucang;
        private String read;
        private int dpj;
        private String time;
        private int isbw;
        private int kcl;
        private String longtime;
        private int lbb;
        private int ischange;
        private int dhcs;
        private int isnew;
        private String ptime;
        private String uaddr;
        private String unames;
        private String uphone;
        private int spnum;
        private int expense;
        private int total;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        private int type;

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getDid() {
            return did;
        }

        public void setDid(String did) {
            this.did = did;
        }

        public int getShu() {
            return shu;
        }

        public void setShu(int shu) {
            this.shu = shu;
        }

        public int getJia() {
            return jia;
        }

        public void setJia(int jia) {
            this.jia = jia;
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

        public int getShopprice() {
            return shopprice;
        }

        public void setShopprice(int shopprice) {
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

        public int getIstui() {
            return istui;
        }

        public void setIstui(int istui) {
            this.istui = istui;
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

        public String getCname() {
            return cname;
        }

        public void setCname(String cname) {
            this.cname = cname;
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

        public String getYhprice() {
            return yhprice;
        }

        public void setYhprice(String yhprice) {
            this.yhprice = yhprice;
        }

        public String getYhendtime() {
            return yhendtime;
        }

        public void setYhendtime(String yhendtime) {
            this.yhendtime = yhendtime;
        }

        public String getYhstartime() {
            return yhstartime;
        }

        public void setYhstartime(String yhstartime) {
            this.yhstartime = yhstartime;
        }

        public int getIsdiscount() {
            return isdiscount;
        }

        public void setIsdiscount(int isdiscount) {
            this.isdiscount = isdiscount;
        }

        public int getIsimg() {
            return isimg;
        }

        public void setIsimg(int isimg) {
            this.isimg = isimg;
        }


        public int getLiulan() {
            return liulan;
        }

        public void setLiulan(int liulan) {
            this.liulan = liulan;
        }

        public String getXiaoshou() {
            return xiaoshou;
        }

        public void setXiaoshou(String xiaoshou) {
            this.xiaoshou = xiaoshou;
        }

        public String getShoucang() {
            return shoucang;
        }

        public void setShoucang(String shoucang) {
            this.shoucang = shoucang;
        }

        public String getRead() {
            return read;
        }

        public void setRead(String read) {
            this.read = read;
        }

        public int getDpj() {
            return dpj;
        }

        public void setDpj(int dpj) {
            this.dpj = dpj;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getIsbw() {
            return isbw;
        }

        public void setIsbw(int isbw) {
            this.isbw = isbw;
        }

        public int getKcl() {
            return kcl;
        }

        public void setKcl(int kcl) {
            this.kcl = kcl;
        }

        public String getLongtime() {
            return longtime;
        }

        public void setLongtime(String longtime) {
            this.longtime = longtime;
        }

        public int getLbb() {
            return lbb;
        }

        public void setLbb(int lbb) {
            this.lbb = lbb;
        }

        public int getIschange() {
            return ischange;
        }

        public void setIschange(int ischange) {
            this.ischange = ischange;
        }

        public int getDhcs() {
            return dhcs;
        }

        public void setDhcs(int dhcs) {
            this.dhcs = dhcs;
        }

        public int getIsnew() {
            return isnew;
        }

        public void setIsnew(int isnew) {
            this.isnew = isnew;
        }

        public String getPtime() {
            return ptime;
        }

        public void setPtime(String ptime) {
            this.ptime = ptime;
        }

        public String getUaddr() {
            return uaddr;
        }

        public void setUaddr(String uaddr) {
            this.uaddr = uaddr;
        }

        public String getUnames() {
            return unames;
        }

        public void setUnames(String unames) {
            this.unames = unames;
        }

        public String getUphone() {
            return uphone;
        }

        public void setUphone(String uphone) {
            this.uphone = uphone;
        }

        public int getSpnum() {
            return spnum;
        }

        public void setSpnum(int spnum) {
            this.spnum = spnum;
        }

        public int getExpense() {
            return expense;
        }

        public void setExpense(int expense) {
            this.expense = expense;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}

