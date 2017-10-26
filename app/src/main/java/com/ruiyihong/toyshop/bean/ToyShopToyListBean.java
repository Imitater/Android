package com.ruiyihong.toyshop.bean;

import java.util.List;

/**
 * Created by 81521 on 2017/7/10.
 */

public class ToyShopToyListBean {

    /**
     * data : {"total":11,"per_page":16,"current_page":1,"data":[{"id":7,"shopimg":"20170723\\60cc4aabf6e36c7ed4fa6dfccbd821df.png","name":"大大泡泡堂","shopprice":120,"dpj":6660,"kcl":3454},{"id":8,"shopimg":"20170708\\4aace76a87fdb1f45524d31e684c5153.png","name":"1222221sd","shopprice":23,"dpj":6660,"kcl":345},{"id":11,"shopimg":"20170724\\c41f49b0ce618ac801f2ae8c69b4c862.jpg","name":"小小玩具-2","shopprice":4512,"dpj":9878,"kcl":23},{"id":12,"shopimg":"20170724\\303d864db89dd8d0edcf9bae965dd1c6.png","name":"小小玩具-3","shopprice":4512,"dpj":127889,"kcl":35},{"id":20,"shopimg":"20170708\\0ec638978114445e3f2f499b2e61c46c.jpg","name":"大大玩具1","shopprice":4512,"dpj":120111,"kcl":10},{"id":22,"shopimg":"20170708\\0ec638978114445e3f2f499b2e61c46c.jpg","name":"大大玩具3","shopprice":4512,"dpj":120111,"kcl":19},{"id":23,"shopimg":"20170708\\0ec638978114445e3f2f499b2e61c46c.jpg","name":"大大玩具4","shopprice":4512,"dpj":120111,"kcl":45},{"id":27,"shopimg":"20170708\\0ec638978114445e3f2f499b2e61c46c.jpg","name":"大大玩具8","shopprice":4512,"dpj":120111,"kcl":89},{"id":31,"shopimg":"20170809\\cf64e7045c9753aaaac4b259d0e980f8.png","name":"益智玩具掏槽111","shopprice":12,"dpj":0,"kcl":0},{"id":32,"shopimg":"20170809\\a8b1af20e002c4f6c0f0af9c4227e211.jpg","name":"惠达玩具1","shopprice":123,"dpj":0,"kcl":0},{"id":34,"shopimg":"20170805\\56e5d79b572db28e7e16b195263fe7f8.png","name":"电动玩具","shopprice":12,"dpj":2345,"kcl":1234}]}
     */

    private DataBeanX data;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public  class DataBeanX {
        /**
         * total : 11
         * per_page : 16
         * current_page : 1
         * data : [{"id":7,"shopimg":"20170723\\60cc4aabf6e36c7ed4fa6dfccbd821df.png","name":"大大泡泡堂","shopprice":120,"dpj":6660,"kcl":3454},{"id":8,"shopimg":"20170708\\4aace76a87fdb1f45524d31e684c5153.png","name":"1222221sd","shopprice":23,"dpj":6660,"kcl":345},{"id":11,"shopimg":"20170724\\c41f49b0ce618ac801f2ae8c69b4c862.jpg","name":"小小玩具-2","shopprice":4512,"dpj":9878,"kcl":23},{"id":12,"shopimg":"20170724\\303d864db89dd8d0edcf9bae965dd1c6.png","name":"小小玩具-3","shopprice":4512,"dpj":127889,"kcl":35},{"id":20,"shopimg":"20170708\\0ec638978114445e3f2f499b2e61c46c.jpg","name":"大大玩具1","shopprice":4512,"dpj":120111,"kcl":10},{"id":22,"shopimg":"20170708\\0ec638978114445e3f2f499b2e61c46c.jpg","name":"大大玩具3","shopprice":4512,"dpj":120111,"kcl":19},{"id":23,"shopimg":"20170708\\0ec638978114445e3f2f499b2e61c46c.jpg","name":"大大玩具4","shopprice":4512,"dpj":120111,"kcl":45},{"id":27,"shopimg":"20170708\\0ec638978114445e3f2f499b2e61c46c.jpg","name":"大大玩具8","shopprice":4512,"dpj":120111,"kcl":89},{"id":31,"shopimg":"20170809\\cf64e7045c9753aaaac4b259d0e980f8.png","name":"益智玩具掏槽111","shopprice":12,"dpj":0,"kcl":0},{"id":32,"shopimg":"20170809\\a8b1af20e002c4f6c0f0af9c4227e211.jpg","name":"惠达玩具1","shopprice":123,"dpj":0,"kcl":0},{"id":34,"shopimg":"20170805\\56e5d79b572db28e7e16b195263fe7f8.png","name":"电动玩具","shopprice":12,"dpj":2345,"kcl":1234}]
         */

        private int total;
        private int per_page;
        private int current_page;
        private List<DataBean> data;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPer_page() {
            return per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public  class DataBean {
            /**
             * id : 7
             * shopimg : 20170723\60cc4aabf6e36c7ed4fa6dfccbd821df.png
             * name : 大大泡泡堂
             * shopprice : 120
             * dpj : 6660
             * kcl : 3454
             */

            private int id;
            private String shopimg;
            private String name;
            private int shopprice;
            private int dpj;
            private int kcl;
            private int type;


            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getShopimg() {
                return shopimg;
            }

            public void setShopimg(String shopimg) {
                this.shopimg = shopimg;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getShopprice() {
                return shopprice;
            }

            public void setShopprice(int shopprice) {
                this.shopprice = shopprice;
            }

            public int getDpj() {
                return dpj;
            }

            public void setDpj(int dpj) {
                this.dpj = dpj;
            }

            public int getKcl() {
                return kcl;
            }

            public void setKcl(int kcl) {
                this.kcl = kcl;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }
    }
}
