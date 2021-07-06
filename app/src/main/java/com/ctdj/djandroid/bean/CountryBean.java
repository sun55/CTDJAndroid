package com.ctdj.djandroid.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName : 咪兔
 * @Author : Sun
 * @Time : 2021/4/20 20:18
 * @Description :
 */
public class CountryBean implements Serializable {

    private List<Province> province;

    public List<Province> getProvince() {
        return province;
    }

    public void setProvince(List<Province> province) {
        this.province = province;
    }

    public static class Province {
        private String name;
        private List<City> city;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<City> getCity() {
            return city;
        }

        public void setCity(List<City> city) {
            this.city = city;
        }

        public static class City {
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
