package com.ruiyihong.toyshop.bean.mine;

import java.util.List;

/**
 * Created by 81521 on 2017/8/24.
 * 我的历史优惠券
 */

public class MyPreferentialOverLineBean {


    /**
     * status : 1
     * djq : [{"dhtime":"2017-09-04 14:39:57","id":3,"dmian":5,"duse":99,"dluo":50,"dimg":"20170921\\76b2d0d4513372df4d7bfc986127bc80.png","dhcs":22,"dqtime":"1970-01-01 08:00:03","olddjq":"20170921\\f060afaa75e03dd80ff87745eb682a05.png","time":"1505993909"},{"dhtime":"2017-09-04 14:39:53","id":5,"dmian":10,"duse":99,"dluo":100,"dimg":"20170921\\cf04644fd6790f6899b03d6dca3af865.png","dhcs":34,"dqtime":"1970-01-01 08:00:03","olddjq":"20170921\\bb8aa1acbe5d8e28a673f6cfeff33a39.png","time":"1505993920"},{"dhtime":"2017-09-01 16:46:35","id":4,"dmian":1,"duse":88,"dluo":10,"dimg":"20170921\\9de692d6f9fe57c732d6f5a971729c22.png","dhcs":22,"dqtime":"1970-01-01 08:00:03","olddjq":"20170921\\7df8f504cc4ab3bf236d37e92cf34588.png","time":"1505993895"},{"dhtime":"2017-09-04 14:40:00","id":5,"dmian":10,"duse":99,"dluo":100,"dimg":"20170921\\cf04644fd6790f6899b03d6dca3af865.png","dhcs":34,"dqtime":"1970-01-01 08:00:03","olddjq":"20170921\\bb8aa1acbe5d8e28a673f6cfeff33a39.png","time":"1505993920"},{"dhtime":"2017-09-22 10:43:27","id":3,"dmian":5,"duse":99,"dluo":50,"dimg":"20170921\\76b2d0d4513372df4d7bfc986127bc80.png","dhcs":22,"dqtime":"1970-01-01 08:00:03","olddjq":"20170921\\f060afaa75e03dd80ff87745eb682a05.png","time":"1505993909"},{"dhtime":"2017-09-24 11:54:11","id":5,"dmian":10,"duse":99,"dluo":100,"dimg":"20170921\\cf04644fd6790f6899b03d6dca3af865.png","dhcs":34,"dqtime":"1970-01-01 08:00:03","olddjq":"20170921\\bb8aa1acbe5d8e28a673f6cfeff33a39.png","time":"1505993920"}]
     * hb : [{"id":3,"dhtime":"2017-09-04 14:39:57","pnum":5,"pic":"20170921\\d9a3807d81116f64c65ee5c713fa093a.png","ischou":1,"dqtime":"1970-01-01 08:00:03","jiang":2,"oldhb":"20170921\\6b5ada3e5e19e7b835af6030fad4e5d6.png","wdhb":"20170921\\8b6eb67a84c5e9fe7da11111d4c68de3.png","time":"1505994235"},{"id":5,"dhtime":"2017-09-04 14:39:53","pnum":10,"pic":"20170921\\6ec6a8ea7aafc4cf50da53ab15e7a71c.png","ischou":1,"dqtime":"1970-01-01 08:00:03","jiang":1,"oldhb":"20170921\\cb5bf2dee2b27bc11bbcc55f6f399d75.png","wdhb":"20170921\\26244facf7f813ef65a2500432abaebc.png","time":"1505994267"},{"id":4,"dhtime":"2017-09-01 16:46:35","pnum":1,"pic":"20170921\\09caf2c6b9b60617a46a20204c56470e.png","ischou":1,"dqtime":"1970-01-01 08:00:03","jiang":3,"oldhb":"20170921\\307d9461b594b80838a971fd088c78d7.png","wdhb":"20170921\\eeffb3887fbe5b4695eb91f61816589a.png","time":"1505993939"},{"id":5,"dhtime":"2017-09-04 14:40:00","pnum":10,"pic":"20170921\\6ec6a8ea7aafc4cf50da53ab15e7a71c.png","ischou":1,"dqtime":"1970-01-01 08:00:03","jiang":1,"oldhb":"20170921\\cb5bf2dee2b27bc11bbcc55f6f399d75.png","wdhb":"20170921\\26244facf7f813ef65a2500432abaebc.png","time":"1505994267"},{"id":3,"dhtime":"2017-09-22 10:43:27","pnum":5,"pic":"20170921\\d9a3807d81116f64c65ee5c713fa093a.png","ischou":1,"dqtime":"1970-01-01 08:00:03","jiang":2,"oldhb":"20170921\\6b5ada3e5e19e7b835af6030fad4e5d6.png","wdhb":"20170921\\8b6eb67a84c5e9fe7da11111d4c68de3.png","time":"1505994235"},{"id":5,"dhtime":"2017-09-24 11:54:11","pnum":10,"pic":"20170921\\6ec6a8ea7aafc4cf50da53ab15e7a71c.png","ischou":1,"dqtime":"1970-01-01 08:00:03","jiang":1,"oldhb":"20170921\\cb5bf2dee2b27bc11bbcc55f6f399d75.png","wdhb":"20170921\\26244facf7f813ef65a2500432abaebc.png","time":"1505994267"}]
     */

    private int status;
    private List<DjqBean> djq;
    private List<HbBean> hb;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DjqBean> getDjq() {
        return djq;
    }

    public void setDjq(List<DjqBean> djq) {
        this.djq = djq;
    }

    public List<HbBean> getHb() {
        return hb;
    }

    public void setHb(List<HbBean> hb) {
        this.hb = hb;
    }

    public static class DjqBean {
        /**
         * dhtime : 2017-09-04 14:39:57
         * id : 3
         * dmian : 5
         * duse : 99
         * dluo : 50
         * dimg : 20170921\76b2d0d4513372df4d7bfc986127bc80.png
         * dhcs : 22
         * dqtime : 1970-01-01 08:00:03
         * olddjq : 20170921\f060afaa75e03dd80ff87745eb682a05.png
         * time : 1505993909
         */

        private String dhtime;
        private int id;
        private int dmian;
        private int duse;
        private int dluo;
        private String dimg;
        private int dhcs;
        private String dqtime;
        private String olddjq;
        private String time;

        public String getDhtime() {
            return dhtime;
        }

        public void setDhtime(String dhtime) {
            this.dhtime = dhtime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDmian() {
            return dmian;
        }

        public void setDmian(int dmian) {
            this.dmian = dmian;
        }

        public int getDuse() {
            return duse;
        }

        public void setDuse(int duse) {
            this.duse = duse;
        }

        public int getDluo() {
            return dluo;
        }

        public void setDluo(int dluo) {
            this.dluo = dluo;
        }

        public String getDimg() {
            return dimg;
        }

        public void setDimg(String dimg) {
            this.dimg = dimg;
        }

        public int getDhcs() {
            return dhcs;
        }

        public void setDhcs(int dhcs) {
            this.dhcs = dhcs;
        }

        public String getDqtime() {
            return dqtime;
        }

        public void setDqtime(String dqtime) {
            this.dqtime = dqtime;
        }

        public String getOlddjq() {
            return olddjq;
        }

        public void setOlddjq(String olddjq) {
            this.olddjq = olddjq;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    public static class HbBean {
        /**
         * id : 3
         * dhtime : 2017-09-04 14:39:57
         * pnum : 5
         * pic : 20170921\d9a3807d81116f64c65ee5c713fa093a.png
         * ischou : 1
         * dqtime : 1970-01-01 08:00:03
         * jiang : 2
         * oldhb : 20170921\6b5ada3e5e19e7b835af6030fad4e5d6.png
         * wdhb : 20170921\8b6eb67a84c5e9fe7da11111d4c68de3.png
         * time : 1505994235
         */

        private int id;
        private String dhtime;
        private int pnum;
        private String pic;
        private int ischou;
        private String dqtime;
        private int jiang;
        private String oldhb;
        private String wdhb;
        private String time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDhtime() {
            return dhtime;
        }

        public void setDhtime(String dhtime) {
            this.dhtime = dhtime;
        }

        public int getPnum() {
            return pnum;
        }

        public void setPnum(int pnum) {
            this.pnum = pnum;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public int getIschou() {
            return ischou;
        }

        public void setIschou(int ischou) {
            this.ischou = ischou;
        }

        public String getDqtime() {
            return dqtime;
        }

        public void setDqtime(String dqtime) {
            this.dqtime = dqtime;
        }

        public int getJiang() {
            return jiang;
        }

        public void setJiang(int jiang) {
            this.jiang = jiang;
        }

        public String getOldhb() {
            return oldhb;
        }

        public void setOldhb(String oldhb) {
            this.oldhb = oldhb;
        }

        public String getWdhb() {
            return wdhb;
        }

        public void setWdhb(String wdhb) {
            this.wdhb = wdhb;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
