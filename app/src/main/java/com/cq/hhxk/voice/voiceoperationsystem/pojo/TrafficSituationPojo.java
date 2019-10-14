package com.cq.hhxk.voice.voiceoperationsystem.pojo;

/**
 * @title  交通态势数据实体类
 * @date   2019/07/23
 * @author enmaoFu
 */
public class TrafficSituationPojo {

    /**
     * status : 200
     * body : {"status":"1","infocode":"10000","info":"OK","trafficinfo":{"evaluation":{"congested":"0.00%","expedite":"100.00%","blocked":"0.00%","status":"1","unknown":"0.00%","description":"整体畅通"},"description":"金兴大道：双向畅通。"}}
     * header : {"gsid":"011136049005156385644755200077615987190","Content-Type":"application/json;charset=UTF-8","Access-Control-Allow-Methods":"*","Access-Control-Allow-Origin":"*","sc":"0.025","Content-Length":242,"Access-Control-Allow-Headers":"DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,key,x-biz,x-info,platinfo,encr,enginever,gzipped,poiid","X-Powered-By":"ring/1.0.0"}
     */

    private int status;
    private BodyBean body;
    private HeaderBean header;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public static class BodyBean {
        /**
         * status : 1
         * infocode : 10000
         * info : OK
         * trafficinfo : {"evaluation":{"congested":"0.00%","expedite":"100.00%","blocked":"0.00%","status":"1","unknown":"0.00%","description":"整体畅通"},"description":"金兴大道：双向畅通。"}
         */

        private String status;
        private String infocode;
        private String info;
        private TrafficinfoBean trafficinfo;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getInfocode() {
            return infocode;
        }

        public void setInfocode(String infocode) {
            this.infocode = infocode;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public TrafficinfoBean getTrafficinfo() {
            return trafficinfo;
        }

        public void setTrafficinfo(TrafficinfoBean trafficinfo) {
            this.trafficinfo = trafficinfo;
        }

        public static class TrafficinfoBean {
            /**
             * evaluation : {"congested":"0.00%","expedite":"100.00%","blocked":"0.00%","status":"1","unknown":"0.00%","description":"整体畅通"}
             * description : 金兴大道：双向畅通。
             */

            private EvaluationBean evaluation;
            private String description;

            public EvaluationBean getEvaluation() {
                return evaluation;
            }

            public void setEvaluation(EvaluationBean evaluation) {
                this.evaluation = evaluation;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public static class EvaluationBean {
                /**
                 * congested : 0.00%
                 * expedite : 100.00%
                 * blocked : 0.00%
                 * status : 1
                 * unknown : 0.00%
                 * description : 整体畅通
                 */

                private String congested;
                private String expedite;
                private String blocked;
                private String status;
                private String unknown;
                private String description;

                public String getCongested() {
                    return congested;
                }

                public void setCongested(String congested) {
                    this.congested = congested;
                }

                public String getExpedite() {
                    return expedite;
                }

                public void setExpedite(String expedite) {
                    this.expedite = expedite;
                }

                public String getBlocked() {
                    return blocked;
                }

                public void setBlocked(String blocked) {
                    this.blocked = blocked;
                }

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getUnknown() {
                    return unknown;
                }

                public void setUnknown(String unknown) {
                    this.unknown = unknown;
                }

                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }
            }
        }
    }

    public static class HeaderBean {
        /**
         * gsid : 011136049005156385644755200077615987190
         * Content-Type : application/json;charset=UTF-8
         * Access-Control-Allow-Methods : *
         * Access-Control-Allow-Origin : *
         * sc : 0.025
         * Content-Length : 242
         * Access-Control-Allow-Headers : DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,key,x-biz,x-info,platinfo,encr,enginever,gzipped,poiid
         * X-Powered-By : ring/1.0.0
         */

        private String gsid;
        private String ContentType;
        private String AccessControlAllowMethods;
        private String AccessControlAllowOrigin;
        private String sc;
        private int ContentLength;
        private String AccessControlAllowHeaders;
        private String XPoweredBy;

        public String getGsid() {
            return gsid;
        }

        public void setGsid(String gsid) {
            this.gsid = gsid;
        }

        public String getContentType() {
            return ContentType;
        }

        public void setContentType(String ContentType) {
            this.ContentType = ContentType;
        }

        public String getAccessControlAllowMethods() {
            return AccessControlAllowMethods;
        }

        public void setAccessControlAllowMethods(String AccessControlAllowMethods) {
            this.AccessControlAllowMethods = AccessControlAllowMethods;
        }

        public String getAccessControlAllowOrigin() {
            return AccessControlAllowOrigin;
        }

        public void setAccessControlAllowOrigin(String AccessControlAllowOrigin) {
            this.AccessControlAllowOrigin = AccessControlAllowOrigin;
        }

        public String getSc() {
            return sc;
        }

        public void setSc(String sc) {
            this.sc = sc;
        }

        public int getContentLength() {
            return ContentLength;
        }

        public void setContentLength(int ContentLength) {
            this.ContentLength = ContentLength;
        }

        public String getAccessControlAllowHeaders() {
            return AccessControlAllowHeaders;
        }

        public void setAccessControlAllowHeaders(String AccessControlAllowHeaders) {
            this.AccessControlAllowHeaders = AccessControlAllowHeaders;
        }

        public String getXPoweredBy() {
            return XPoweredBy;
        }

        public void setXPoweredBy(String XPoweredBy) {
            this.XPoweredBy = XPoweredBy;
        }
    }
}
