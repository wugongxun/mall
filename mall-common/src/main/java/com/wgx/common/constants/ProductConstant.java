package com.wgx.common.constants;

/**
 * @author wgx
 * @since 2023/3/9 17:18
 */
public class ProductConstant {
    public enum AttrEnum {
        ATTR_TYPE_SALE(0, "销售属性"), ATTR_TYPE_BASE(1, "基础属性");

        private int code;
        private String msg;

        AttrEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
