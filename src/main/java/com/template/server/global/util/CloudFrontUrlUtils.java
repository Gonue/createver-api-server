package com.template.server.global.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CloudFrontUrlUtils {
    public static String convertToCloudFrontUrl(String s3Url) {
        return s3Url.replace("https://mine-7730.s3.ap-northeast-2.amazonaws.com", "https://d2xbqs28wc0ywi.cloudfront.net");
    }


    public static String convertImgUrlsInHtmlContent(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);
        Elements imgTags = doc.select("img");

        for (Element img : imgTags) {
            String src = img.attr("src");
            String newSrc = convertToCloudFrontUrl(src);
            img.attr("src", newSrc);
        }

        return doc.toString();
    }
}
