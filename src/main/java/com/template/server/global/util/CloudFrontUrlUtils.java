package com.template.server.global.util;

public class CloudFrontUrlUtils {
    public static String convertToCloudFrontUrl(String s3Url) {
        return s3Url.replace("https://mine-7730.s3.ap-northeast-2.amazonaws.com", "https://d2xbqs28wc0ywi.cloudfront.net");
    }
}
