package com.wgx.common.constants;

/**
 * @author wgx
 * @since 2023/3/11 16:19
 */
public class WareConstant {

    public enum PurchaseStatus {
        CREATED(0, "新建状态"),
        ASSIGNED(1, "已分配"),
        RECEIVED(2, "已领取"),
        FINISH(3, "已完成"),
        ERROR(4, "有异常");
        private Integer code;
        private String msg;

        PurchaseStatus(Integer code, String msg) {
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

    public enum PurchaseDetailStatus {
        CREATED(0, "新建状态"),
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        FINISH(3, "已完成"),
        ERROR(4, "采购失败");
        private Integer code;
        private String msg;

        PurchaseDetailStatus(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
